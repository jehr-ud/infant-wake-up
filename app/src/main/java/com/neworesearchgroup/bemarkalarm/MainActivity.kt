package com.neworesearchgroup.bemarkalarm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.neworesearchgroup.bemarkalarm.controls.notification.NotificationUtils
import com.neworesearchgroup.bemarkalarm.data.database.BemarkDatabase
import com.neworesearchgroup.bemarkalarm.data.model.MonitorEvent
import com.neworesearchgroup.bemarkalarm.data.viewmodel.LoginViewModel
import com.neworesearchgroup.bemarkalarm.data.viewmodel.MonitorViewModel
import com.neworesearchgroup.bemarkalarm.data.viewmodel.RegisterViewModel
import com.neworesearchgroup.bemarkalarm.ui.screens.LoginScreen
import com.neworesearchgroup.bemarkalarm.ui.screens.MonitorScreen
import com.neworesearchgroup.bemarkalarm.ui.screens.RegisterScreen
import com.neworesearchgroup.bemarkalarm.ui.screens.ReportScreen
import com.neworesearchgroup.bemarkalarm.ui.theme.BemarkTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Crear canal de notificaciones
        NotificationUtils.createChannel(this)

        setContent {
            BemarkTheme {

                val navController = rememberNavController()
                val auth = FirebaseAuth.getInstance()
                val context = LocalContext.current

                // ✅ ÚNICA instancia de Room para toda la app
                val database = remember {
                    Room.databaseBuilder(
                        context.applicationContext,
                        BemarkDatabase::class.java,
                        "bemark_db"
                    ).build()
                }

                // Decide pantalla inicial
                val startDestination = if (auth.currentUser != null) {
                    "monitor"
                } else {
                    "login"
                }

                NavHost(
                    navController = navController,
                    startDestination = startDestination
                ) {

                    // ---------- LOGIN ----------
                    composable("login") {
                        val viewModel: LoginViewModel = viewModel()

                        LoginScreen(
                            viewModel = viewModel,
                            onLoginSuccess = {
                                navController.navigate("monitor") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onGoToRegister = {
                                navController.navigate("register")
                            }
                        )
                    }

                    // ---------- MONITOR ----------
                    composable("monitor") {

                        val monitorViewModel = remember {
                            MonitorViewModel(database.monitorEventDao())
                        }

                        MonitorScreen(
                            viewModel = monitorViewModel,
                            onAlert = { score, decisionValue ->
                                // ✅ Guardar SOLO cuando hay alerta
                                monitorViewModel.saveAlert(score, decisionValue)

                                navController.navigate("report")
                            }
                        )
                    }

                    // ---------- REPORT ----------
                    composable("report") {

                        val dao = database.monitorEventDao()
                        var events by remember { mutableStateOf<List<MonitorEvent>>(emptyList()) }

                        LaunchedEffect(Unit) {
                            events = dao.getAll()
                        }

                        ReportScreen(
                            events = events,
                            onContinue = {
                                navController.navigate("monitor") {
                                    popUpTo("report") { inclusive = true }
                                }
                            }
                        )
                    }

                    // ---------- REGISTER ----------
                    composable("register") {
                        val viewModel: RegisterViewModel = viewModel()

                        RegisterScreen(
                            viewModel = viewModel,
                            onRegisterSuccess = {
                                navController.navigate("monitor") {
                                    popUpTo("register") { inclusive = true }
                                }
                            },
                            onGoToLogin = {
                                navController.navigate("login")
                            }
                        )
                    }
                }
            }
        }
    }
}