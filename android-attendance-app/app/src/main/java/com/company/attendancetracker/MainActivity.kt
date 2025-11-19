package com.company.attendancetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.company.attendancetracker.data.local.AppDatabase
import com.company.attendancetracker.data.repository.AttendanceRepository
import com.company.attendancetracker.data.repository.AuthRepository
import com.company.attendancetracker.data.repository.LocationRepository
import com.company.attendancetracker.ui.home.HomeScreen
import com.company.attendancetracker.ui.home.HomeViewModel
import com.company.attendancetracker.ui.login.LoginScreen
import com.company.attendancetracker.ui.login.LoginViewModel
import com.company.attendancetracker.ui.theme.AttendanceTrackerTheme
import com.company.attendancetracker.utils.SessionManager

/**
 * Main Activity - Entry point of the app.
 *
 * Sets up:
 * - Navigation between Login and Home screens
 * - Dependency injection (manual for simplicity)
 * - Theme configuration
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize dependencies
        val database = AppDatabase.getDatabase(applicationContext)
        val sessionManager = SessionManager(applicationContext)

        val authRepository = AuthRepository(database.employeeDao())
        val attendanceRepository = AttendanceRepository(database.attendanceDao())
        val locationRepository = LocationRepository(database.locationLogDao())

        setContent {
            AttendanceTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AttendanceApp(
                        authRepository = authRepository,
                        attendanceRepository = attendanceRepository,
                        locationRepository = locationRepository,
                        sessionManager = sessionManager
                    )
                }
            }
        }
    }
}

/**
 * Main app navigation.
 */
@Composable
fun AttendanceApp(
    authRepository: AuthRepository,
    attendanceRepository: AttendanceRepository,
    locationRepository: LocationRepository,
    sessionManager: SessionManager
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        // Login Screen
        composable("login") {
            val loginViewModel = viewModel<LoginViewModel>(
                factory = LoginViewModelFactory(authRepository, sessionManager)
            )

            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    // Navigate to home and clear login from back stack
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // Home Screen
        composable("home") {
            val homeViewModel = viewModel<HomeViewModel>(
                factory = HomeViewModelFactory(
                    application = navController.context as android.app.Application,
                    authRepository = authRepository,
                    attendanceRepository = attendanceRepository,
                    locationRepository = locationRepository,
                    sessionManager = sessionManager
                )
            )

            HomeScreen(
                viewModel = homeViewModel,
                onLogout = {
                    // Navigate back to login and clear home from back stack
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}

/**
 * ViewModel Factory for LoginViewModel.
 * Required because ViewModel has custom constructor parameters.
 */
class LoginViewModelFactory(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(com.company.attendancetracker.ui.login.LoginViewModel::class.java)) {
            return com.company.attendancetracker.ui.login.LoginViewModel(
                authRepository,
                sessionManager
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

/**
 * ViewModel Factory for HomeViewModel.
 */
class HomeViewModelFactory(
    private val application: android.app.Application,
    private val authRepository: AuthRepository,
    private val attendanceRepository: AttendanceRepository,
    private val locationRepository: LocationRepository,
    private val sessionManager: SessionManager
) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(com.company.attendancetracker.ui.home.HomeViewModel::class.java)) {
            return com.company.attendancetracker.ui.home.HomeViewModel(
                application,
                authRepository,
                attendanceRepository,
                locationRepository,
                sessionManager
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
