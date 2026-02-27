package com.neworesearchgroup.bemarkalarm.ui.screens

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
import com.neworesearchgroup.bemarkalarm.data.model.MonitorEvent

@Composable
fun ReportScreen(
    events: List<MonitorEvent>,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp)
    ) {
        Text("Monitoring Report", fontSize = 24.sp)

        Spacer(Modifier.height(16.dp))

        events.forEach {
            Text(
                "• ${it.score} - ${it.decisionValue}",
                fontSize = 16.sp
            )
        }

        Spacer(Modifier.height(24.dp))

        Button(onClick = onContinue) {
            Text("Continue monitoring")
        }
    }
}
