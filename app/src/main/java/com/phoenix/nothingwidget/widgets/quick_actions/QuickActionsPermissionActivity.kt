package com.phoenix.nothingwidget.widgets.quick_actions

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

/**
 * One-shot trampoline: request CAMERA (for torch), toggle flashlight, finish.
 */
class QuickActionsPermissionActivity : ComponentActivity() {

    private val requestCamera = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            QuickActionsFlashlight.toggle(applicationContext)
            QuickActionsRenderer.renderAll(applicationContext)
        } else {
            Toast.makeText(
                this,
                "Camera permission is needed for the flashlight",
                Toast.LENGTH_SHORT,
            ).show()
        }
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED -> {
                QuickActionsFlashlight.toggle(applicationContext)
                QuickActionsRenderer.renderAll(applicationContext)
                finish()
            }
            else -> requestCamera.launch(Manifest.permission.CAMERA)
        }
    }

    companion object {
        fun intent(context: Context): Intent =
            Intent(context, QuickActionsPermissionActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
}
