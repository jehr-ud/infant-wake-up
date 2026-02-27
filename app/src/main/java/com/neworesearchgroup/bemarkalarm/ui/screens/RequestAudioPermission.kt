package com.neworesearchgroup.bemarkalarm.ui.screens

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat


@Composable
fun RequestAudioPermission(onGranted: () -> Unit) {
    val context = LocalContext.current
    val permission = android.Manifest.permission.RECORD_AUDIO

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) onGranted()
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context, permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            launcher.launch(permission)
        } else {
            onGranted()
        }
    }
}
