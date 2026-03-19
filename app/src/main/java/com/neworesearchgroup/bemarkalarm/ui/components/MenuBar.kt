package com.neworesearchgroup.bemarkalarm.ui.components

import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppMenu(
    onMonitor: () -> Unit,
    onReport: () -> Unit,
    onLogout: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text("Infant Monitor")
        },
        actions = {
            IconButton(
                onClick = { expanded = true }
            ) {
                Icon(Icons.Default.MoreVert, contentDescription = "Menu")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Monitor") },
                    onClick = {
                        expanded = false
                        onMonitor()
                    }
                )

                DropdownMenuItem(
                    text = { Text("Reports") },
                    onClick = {
                        expanded = false
                        onReport()
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