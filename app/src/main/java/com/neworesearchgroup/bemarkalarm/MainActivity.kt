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
                    FlowScreenStatus.MONITOR.route
                } else {
                    FlowScreenStatus.LOGIN.route
                }

                NavHost(
                    navController = navController,
                    startDestination = startDestination
                ) {

                    composable(FlowScreenStatus.LOGIN.route) {
                        val viewModel: LoginViewModel = viewModel()

                        LoginScreen(
                            viewModel = viewModel,
                            onLoginSuccess = {
                                navController.navigate(FlowScreenStatus.MONITOR.route) {
                                    popUpTo(FlowScreenStatus.LOGIN.route) {
                                        inclusive = true
                                    }
                                }
                            },
                            onGoToRegister = {
                                navController.navigate(FlowScreenStatus.REGISTER.route)
                            }
                        )
                    }

                    composable(FlowScreenStatus.MONITOR.route) {

                        val monitorViewModel = remember {
                            MonitorViewModel(database.monitorEventDao())
                        }

                        MonitorScreen(
                            viewModel = monitorViewModel,
                            onAlert = { score, decisionValue ->
                                monitorViewModel.saveAlert(score, decisionValue)
                                navController.navigate(FlowScreenStatus.REPORT.route)
                            },
                            onGoToReport = {
                                navController.navigate(FlowScreenStatus.REPORT.route)
                            },
                            onLogout = {
                                FirebaseAuth.getInstance().signOut()
                                navController.navigate(FlowScreenStatus.LOGIN.route) {
                                    popUpTo(FlowScreenStatus.MONITOR.route) {
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }

                    composable(FlowScreenStatus.REPORT.route) {

                        val dao = database.monitorEventDao()
                        var events by remember {
                            mutableStateOf<List<MonitorEvent>>(emptyList())
                        }

                        LaunchedEffect(Unit) {
                            events = dao.getAll()
                        }

                        ReportScreen(
                            events = events,
                            onContinue = {
                                navController.navigate(FlowScreenStatus.MONITOR.route) {
                                    popUpTo(FlowScreenStatus.REPORT.route) {
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }

                    composable(FlowScreenStatus.REGISTER.route) {
                        val viewModel: RegisterViewModel = viewModel()

                        RegisterScreen(
                            viewModel = viewModel,
                            onRegisterSuccess = {
                                navController.navigate(FlowScreenStatus.MONITOR.route) {
                                    popUpTo(FlowScreenStatus.REGISTER.route) {
                                        inclusive = true
                                    }
                                }
                            },
                            onGoToLogin = {
                                navController.navigate(FlowScreenStatus.LOGIN.route)
                            }
                        )
                    }
                }
            }
        }
    }
}