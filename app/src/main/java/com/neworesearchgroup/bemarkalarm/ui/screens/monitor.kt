package com.neworesearchgroup.bemarkalarm.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.neworesearchgroup.bemarkalarm.controls.audio.AudioMonitorService
import com.neworesearchgroup.bemarkalarm.data.viewmodel.MonitorViewModel


@Composable
fun MonitorScreen(
    viewModel: MonitorViewModel,
    onAlert: (Float, Float) -> Unit
) {
    val context = LocalContext.current

    val audioPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                startAudioService(context)
            }
        }

    val notificationPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { }

    // 👇 estado real del sistema
    val isListening by AudioMonitorService.isListening.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = if (isListening) "Monitoring active"
            else "Monitoring stopped",
            fontSize = 22.sp
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {

                // Android 13+ notifications
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        notificationPermissionLauncher.launch(
                            Manifest.permission.POST_NOTIFICATIONS
                        )
                    }
                }

                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.RECORD_AUDIO
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    startAudioService(context)
                } else {
                    audioPermissionLauncher.launch(
                        Manifest.permission.RECORD_AUDIO
                    )
                }
            }
        ) {
            Text("Start Monitoring")
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = if (isListening) "🎤 Listening..." else "⛔ Not listening",
            fontSize = 18.sp
        )

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                context.stopService(
                    Intent(context, AudioMonitorService::class.java)
                )
            }
        ) {
            Text("Stop Monitoring")
        }
    }
}


private fun startAudioService(context: Context) {
    val intent = Intent(context, AudioMonitorService::class.java)
    ContextCompat.startForegroundService(context, intent)
}