package com.phoenix.nothingwidget.widgets.weather

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import java.util.Locale
import kotlin.coroutines.resume
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

data class WeatherCoordinates(
    val latitude: Double,
    val longitude: Double,
)

/**
 * Single location pipeline:
 * Permission → FusedLocation → Geocoder → DataStore(weather_city) → widget update.
 */
object WeatherLocationHelper {

    private const val TAG = "WeatherLocation"
    private const val LOCATION_TIMEOUT_MS = 15_000L
    private const val MAX_LOCATION_AGE_MS = 10L * 60L * 1000L
    private const val GEOCODER_MAX_RESULTS = 5
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun hasLocationPermission(context: Context): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    fun captureAndPersist(
        context: Context,
        onComplete: (city: String?) -> Unit,
    ) {
        if (!hasLocationPermission(context)) {
            onComplete(null)
            return
        }
        val appContext = context.applicationContext
        scope.launch {
            val city = runCatching { fetchAndSave(appContext) }
                .onFailure { error -> Log.e(TAG, "Location capture failed", error) }
                .getOrNull()
            withContext(Dispatchers.Main) {
                onComplete(city)
            }
        }
    }

    suspend fun fetchAndSave(context: Context): String? {
        if (!hasLocationPermission(context)) return null

        val coordinates = resolveCoordinates(context) ?: return null
        val city = resolvePlaceName(context, coordinates)
            .trim()
            .ifBlank { WeatherConfig.LOCATION_UNKNOWN }
        WeatherLocationStore.save(context, city, coordinates)
        return city
    }

    @SuppressLint("MissingPermission")
    suspend fun resolveCoordinates(context: Context): WeatherCoordinates? {
        if (!hasLocationPermission(context)) return null

        val client = LocationServices.getFusedLocationProviderClient(context.applicationContext)

        val last = client.lastLocation.awaitResult(LOCATION_TIMEOUT_MS)
        if (last != null) {
            return WeatherCoordinates(last.latitude, last.longitude)
        }

        val balanced = requestCurrentLocationAwait(
            context,
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
        )
        if (balanced != null) {
            return WeatherCoordinates(balanced.latitude, balanced.longitude)
        }

        val highAccuracy = requestCurrentLocationAwait(
            context,
            Priority.PRIORITY_HIGH_ACCURACY,
        )
        if (highAccuracy != null) {
            return WeatherCoordinates(highAccuracy.latitude, highAccuracy.longitude)
        }

        return WeatherLocationStore.coordinates(context)
    }

    suspend fun resolvePlaceName(context: Context, coordinates: WeatherCoordinates): String {
        val addresses = reverseGeocodeAll(context, coordinates)
        if (addresses.isEmpty()) return WeatherConfig.LOCATION_UNKNOWN
        return pickMostSpecificPlace(addresses).ifBlank { WeatherConfig.LOCATION_UNKNOWN }
    }

    /**
     * Priority:
     * subLocality → locality → subAdminArea → adminArea → Kathmandu
     * Scans all geocoder hits so a later result can supply a better neighborhood name.
     */
    private fun pickMostSpecificPlace(addresses: List<Address>): String {
        fun first(selector: (Address) -> String?): String? {
            return addresses
                .asSequence()
                .mapNotNull { address -> selector(address)?.trim()?.takeIf { it.isNotBlank() } }
                .firstOrNull()
        }

        return first { it.subLocality }
            ?: first { it.locality }
            ?: first { it.subAdminArea }
            ?: first { it.adminArea }
            ?: WeatherConfig.LOCATION_UNKNOWN
    }

    @SuppressLint("MissingPermission")
    private suspend fun requestCurrentLocationAwait(
        context: Context,
        priority: Int,
    ): Location? {
        val client = LocationServices.getFusedLocationProviderClient(context.applicationContext)
        val token = CancellationTokenSource()
        val request = CurrentLocationRequest.Builder()
            .setPriority(priority)
            .setDurationMillis(LOCATION_TIMEOUT_MS)
            .setMaxUpdateAgeMillis(MAX_LOCATION_AGE_MS)
            .build()
        return try {
            client.getCurrentLocation(request, token.token)
                .awaitResult(LOCATION_TIMEOUT_MS)
                .also { token.cancel() }
        } catch (error: Exception) {
            Log.e(TAG, "getCurrentLocation failed priority=$priority", error)
            token.cancel()
            null
        }
    }

    private suspend fun reverseGeocodeAll(
        context: Context,
        coordinates: WeatherCoordinates,
    ): List<Address> {
        if (!Geocoder.isPresent()) return emptyList()
        val geocoder = Geocoder(context, Locale.getDefault())
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                withTimeoutOrNull(LOCATION_TIMEOUT_MS) {
                    suspendCancellableCoroutine { cont ->
                        geocoder.getFromLocation(
                            coordinates.latitude,
                            coordinates.longitude,
                            GEOCODER_MAX_RESULTS,
                            object : Geocoder.GeocodeListener {
                                override fun onGeocode(addresses: MutableList<Address>) {
                                    if (cont.isActive) cont.resume(addresses.toList())
                                }

                                override fun onError(errorMessage: String?) {
                                    Log.w(TAG, "Geocoder.onError=$errorMessage")
                                    if (cont.isActive) cont.resume(emptyList())
                                }
                            },
                        )
                    }
                } ?: emptyList()
            } else {
                @Suppress("DEPRECATION")
                geocoder.getFromLocation(
                    coordinates.latitude,
                    coordinates.longitude,
                    GEOCODER_MAX_RESULTS,
                ).orEmpty()
            }
        } catch (error: Exception) {
            Log.e(TAG, "Geocoder failed", error)
            emptyList()
        }
    }

    private suspend fun <T> Task<T>.awaitResult(timeoutMs: Long): T? =
        withTimeoutOrNull(timeoutMs) {
            suspendCancellableCoroutine { cont ->
                addOnCompleteListener { task ->
                    if (!cont.isActive) return@addOnCompleteListener
                    if (task.isSuccessful) {
                        cont.resume(task.result)
                    } else {
                        cont.resume(null)
                    }
                }
            }
        }
}
