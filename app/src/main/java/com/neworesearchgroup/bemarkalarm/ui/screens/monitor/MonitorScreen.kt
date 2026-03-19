package com.neworesearchgroup.bemarkalarm.ui.screens.monitor

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
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.neworesearchgroup.bemarkalarm.controls.audio.AudioMonitorService
import com.neworesearchgroup.bemarkalarm.data.viewmodel.MonitorViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonitorScreen(
    viewModel: MonitorViewModel,
    onAlert: (Float, Float) -> Unit,
    onGoToReport: () -> Unit,
    onLogout: () -> Unit
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

    val isListening by AudioMonitorService.isListening.collectAsState()

    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Infant Monitor")
                },
                actions = {
                    IconButton(
                        onClick = { expanded = true }
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "Menu"
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {
                            expanded = false
                        }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Reports") },
                            onClick = {
                                expanded = false
                                onGoToReport()
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Logout") },
                            onClick = {
                                expanded = false
                                onLogout()
                            }
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = if (isListening)
                    "Monitoring active"
                else
                    "Monitoring stopped",
                fontSize = 22.sp
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {

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

            Spacer(Modifier.height(16.dp))

            Text(
                text = if (isListening)
                    "🎤 Listening..."
                else
                    "⛔ Not listening",
                fontSize = 18.sp
            )

            Spacer(Modifier.height(16.dp))

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
}

private fun startAudioService(context: Context) {
    val intent = Intent(context, AudioMonitorService::class.java)
    ContextCompat.startForegroundService(context, intent)
}