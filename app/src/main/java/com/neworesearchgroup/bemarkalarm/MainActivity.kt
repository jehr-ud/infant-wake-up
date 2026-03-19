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
import com.neworesearchgroup.bemarkalarm.ui.screens.auth.LoginScreen
import com.neworesearchgroup.bemarkalarm.ui.screens.monitor.MonitorScreen
import com.neworesearchgroup.bemarkalarm.ui.screens.auth.RegisterScreen
import com.neworesearchgroup.bemarkalarm.ui.screens.monitor.ReportScreen
import com.neworesearchgroup.bemarkalarm.ui.theme.BemarkTheme
import com.neworesearchgroup.bemarkalarm.data.enums.FlowScreenStatus

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NotificationUtils.createChannel(this)

        setContent {
            BemarkTheme {

                val navController = rememberNavController()
                val auth = FirebaseAuth.getInstance()
                val context = LocalContext.current

                val database = remember {
                    Room.databaseBuilder(
                        context.applicationContext,
                        BemarkDatabase::class.java,
                        "bemark_db"
                    ).build()
                }

                val startDestination = if (auth.currentUser != null) {
                    FlowScreenStatus.MONITOR.toString()
                } else {
                    FlowScreenStatus.LOGIN.toString()
                }

                NavHost(
                    navController = navController,
                    startDestination = startDestination
                ) {

                    composable(FlowScreenStatus.LOGIN.toString()) {
                        val viewModel: LoginViewModel = viewModel()

                        LoginScreen(
                            viewModel = viewModel,
                            onLoginSuccess = {
                                navController.navigate(FlowScreenStatus.MONITOR.toString()) {
                                    popUpTo(FlowScreenStatus.LOGIN.toString()) { inclusive = true }
                                }
                            },
                            onGoToRegister = {
                                navController.navigate(FlowScreenStatus.REGISTER.toString())
                            }
                        )
                    }

                    composable(FlowScreenStatus.MONITOR.toString()) {

                        val monitorViewModel = remember {
                            MonitorViewModel(database.monitorEventDao())
                        }

                        MonitorScreen(
                            viewModel = monitorViewModel,
                            onAlert = { score, decisionValue ->
                                monitorViewModel.saveAlert(score, decisionValue)
                                navController.navigate(FlowScreenStatus.REPORT.toString())
                            }
                        )
                    }

                    composable(FlowScreenStatus.REPORT.toString()) {

                        val dao = database.monitorEventDao()
                        var events by remember { mutableStateOf<List<MonitorEvent>>(emptyList()) }

                        LaunchedEffect(Unit) {
                            events = dao.getAll()
                        }

                        ReportScreen(
                            events = events,
                            onContinue = {
                                navController.navigate(FlowScreenStatus.MONITOR.toString()) {
                                    popUpTo(FlowScreenStatus.REPORT.toString()) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(FlowScreenStatus.REGISTER.toString()) {
                        val viewModel: RegisterViewModel = viewModel()

                        RegisterScreen(
                            viewModel = viewModel,
                            onRegisterSuccess = {
                                navController.navigate(FlowScreenStatus.MONITOR.toString()) {
                                    popUpTo(FlowScreenStatus.REGISTER.toString()) { inclusive = true }
                                }
                            },
                            onGoToLogin = {
                                navController.navigate(FlowScreenStatus.LOGIN.toString())
                            }
                        )
                    }
                }
            }
        }
    }
}