package com.neworesearchgroup.bemarkalarm.ui.screens.monitor

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import com.neworesearchgroup.bemarkalarm.data.model.MonitorEvent
import com.neworesearchgroup.bemarkalarm.ui.components.AppMenu

@Composable
fun ReportScreen(
    events: List<MonitorEvent>,
    onContinue: () -> Unit,
    onLogout: () -> Unit
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
                fontSize = 24.sp
            )

            Spacer(Modifier.height(16.dp))

            events.forEach { event ->
                Text(
                    text = "• Score: ${event.score} | Decision: ${event.decisionValue}",
                    fontSize = 16.sp
                )

                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = onContinue
            ) {
                Text("Continue monitoring")
            }
        }
    }
}