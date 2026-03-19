package com.neworesearchgroup.bemarkalarm.ui.screens.monitor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.neworesearchgroup.bemarkalarm.data.model.MonitorEvent
import com.neworesearchgroup.bemarkalarm.ui.components.AppMenu

@Composable
fun ReportScreen(
    events: List<MonitorEvent>,
    onContinue: () -> Unit,
    onClear: () -> Unit,
    onLogout: () -> Unit,
    onCorrectAlert: (MonitorEvent) -> Unit,
    onFalseAlert: (MonitorEvent) -> Unit
) {

    Scaffold(
        topBar = {
            AppMenu(
                title = "Reports",
                onGoToReport = { },
                onLogout = onLogout
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {

            Text(
                text = "Monitoring Report",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(Modifier.height(20.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(events) { event ->

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Text(
                                text = "Score: ${event.score}",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(Modifier.height(6.dp))

                            Text(
                                text = "Decision: ${event.decisionValue}",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Spacer(Modifier.height(12.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {


                                if (event.wasConfirmed == null) {

                                    Button(
                                        onClick = {
                                            onCorrectAlert(event)
                                        }
                                    ) {
                                        Text("Correct")
                                    }

                                    Button(
                                        onClick = {
                                            onFalseAlert(event)
                                        }
                                    ) {
                                        Text("False alert")
                                    }

                                } else if (event.wasConfirmed == true) {

                                    Button(
                                        onClick = {},
                                        enabled = false
                                    ) {
                                        Text("Correct ✓")
                                    }

                                    OutlinedButton(
                                        onClick = {},
                                        enabled = false
                                    ) {
                                        Text("False alert")
                                    }

                                } else {

                                    OutlinedButton(
                                        onClick = {},
                                        enabled = false
                                    ) {
                                        Text("Correct")
                                    }

                                    Button(
                                        onClick = {},
                                        enabled = false
                                    ) {
                                        Text("False alert ✓")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continue monitoring")
            }

            OutlinedButton(
                onClick = onClear,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Clear alerts")
            }
        }
    }
}