package com.phoenix.nothingwidget.widgets.quick_actions

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.util.Log
import androidx.core.content.ContextCompat

/** Torch toggle via [CameraManager]: no CameraX. */
object QuickActionsFlashlight {

    private const val TAG = "QuickActionsFlash"
    private const val PREFS = "quick_actions_flashlight"
    private const val KEY_ON = "torch_on"

    fun isOn(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_ON, false)

    fun toggle(context: Context): Boolean {
        val next = !isOn(context)
        return setEnabled(context, next)
    }

    fun setEnabled(context: Context, enabled: Boolean): Boolean {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w(TAG, "CAMERA permission not granted")
            return false
        }

        val cameraManager = context.getSystemService(CameraManager::class.java) ?: return false
        val cameraId = torchCameraId(cameraManager) ?: return false
        return try {
            cameraManager.setTorchMode(cameraId, enabled)
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(KEY_ON, enabled)
                .apply()
            true
        } catch (error: Exception) {
            Log.e(TAG, "Torch failed", error)
            false
        }
    }

    private fun torchCameraId(cameraManager: CameraManager): String? {
        for (id in cameraManager.cameraIdList) {
            val chars = cameraManager.getCameraCharacteristics(id)
            val hasFlash = chars.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            val facing = chars.get(CameraCharacteristics.LENS_FACING)
            if (hasFlash && facing == CameraCharacteristics.LENS_FACING_BACK) {
                return id
            }
        }
        for (id in cameraManager.cameraIdList) {
            val chars = cameraManager.getCameraCharacteristics(id)
            if (chars.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true) return id
        }
        return null
    }
}
