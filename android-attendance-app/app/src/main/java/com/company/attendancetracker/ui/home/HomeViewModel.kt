package com.company.attendancetracker.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.company.attendancetracker.data.local.entity.Attendance
import com.company.attendancetracker.data.local.entity.Employee
import com.company.attendancetracker.data.local.entity.LocationLog
import com.company.attendancetracker.data.repository.AttendanceRepository
import com.company.attendancetracker.data.repository.AuthRepository
import com.company.attendancetracker.data.repository.LocationRepository
import com.company.attendancetracker.utils.Constants
import com.company.attendancetracker.utils.SessionManager
import com.company.attendancetracker.workers.LocationTrackingWorker
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * ViewModel for Home screen.
 *
 * Manages:
 * - Employee profile display
 * - Punch IN/OUT actions
 * - Location tracking start/stop
 * - Display of location logs
 */
class HomeViewModel(
    application: Application,
    private val authRepository: AuthRepository,
    private val attendanceRepository: AttendanceRepository,
    private val locationRepository: LocationRepository,
    private val sessionManager: SessionManager
) : AndroidViewModel(application) {

    private val workManager = WorkManager.getInstance(application)

    // Employee data
    private val _employee = MutableStateFlow<Employee?>(null)
    val employee: StateFlow<Employee?> = _employee.asStateFlow()

    // Active attendance session
    private val _activeAttendance = MutableStateFlow<Attendance?>(null)
    val activeAttendance: StateFlow<Attendance?> = _activeAttendance.asStateFlow()

    // Location logs for current attendance
    private val _locationLogs = MutableStateFlow<List<LocationLog>>(emptyList())
    val locationLogs: StateFlow<List<LocationLog>> = _locationLogs.asStateFlow()

    // UI State
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Idle)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadEmployeeData()
    }

    /**
     * Load logged in employee data and active attendance.
     */
    private fun loadEmployeeData() {
        viewModelScope.launch {
            try {
                val employeeId = sessionManager.getEmployeeId()
                if (employeeId != null) {
                    // Load employee
                    _employee.value = authRepository.getEmployeeById(employeeId)

                    // Load active attendance
                    attendanceRepository.getActiveAttendanceFlow(employeeId)
                        .collect { attendance ->
                            _activeAttendance.value = attendance

                            // Load location logs if attendance is active
                            if (attendance != null) {
                                sessionManager.saveActiveAttendanceId(attendance.id)
                                loadLocationLogs(attendance.id)
                            } else {
                                _locationLogs.value = emptyList()
                            }
                        }
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error("Failed to load data: ${e.message}")
            }
        }
    }

    /**
     * Load location logs for current attendance session.
     */
    private fun loadLocationLogs(attendanceId: Long) {
        viewModelScope.launch {
            locationRepository.getLogsForAttendance(attendanceId)
                .collect { logs ->
                    _locationLogs.value = logs
                }
        }
    }

    /**
     * Punch IN - Start work and location tracking.
     */
    fun punchIn() {
        viewModelScope.launch {
            try {
                _uiState.value = HomeUiState.Loading

                val employeeId = _employee.value?.id
                if (employeeId == null) {
                    _uiState.value = HomeUiState.Error("Employee not found")
                    return@launch
                }

                // Create attendance record
                val attendanceId = attendanceRepository.punchIn(employeeId)

                // Save to session
                sessionManager.saveActiveAttendanceId(attendanceId)

                // Start location tracking
                startLocationTracking(attendanceId, employeeId)

                _uiState.value = HomeUiState.Success("Punched IN successfully")

            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error("Failed to punch IN: ${e.message}")
            }
        }
    }

    /**
     * Punch OUT - End work and stop location tracking.
     */
    fun punchOut() {
        viewModelScope.launch {
            try {
                _uiState.value = HomeUiState.Loading

                val attendance = _activeAttendance.value
                if (attendance == null) {
                    _uiState.value = HomeUiState.Error("No active attendance found")
                    return@launch
                }

                // Update attendance record
                attendanceRepository.punchOut(attendance.id)

                // Clear session
                sessionManager.clearActiveAttendanceId()

                // Stop location tracking
                stopLocationTracking()

                _uiState.value = HomeUiState.Success("Punched OUT successfully")

            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error("Failed to punch OUT: ${e.message}")
            }
        }
    }

    /**
     * Start periodic location tracking using WorkManager.
     *
     * HOW IT WORKS:
     * 1. Creates a PeriodicWorkRequest that runs every X minutes (configured in Constants)
     * 2. WorkManager handles battery optimization and scheduling
     * 3. Work survives app restarts and device reboots
     * 4. Uses REPLACE policy to ensure only one instance runs
     */
    private fun startLocationTracking(attendanceId: Long, employeeId: Long) {
        // Build periodic work request
        val workRequest = PeriodicWorkRequestBuilder<LocationTrackingWorker>(
            // Repeat interval - minimum is 15 minutes per Android constraints
            repeatInterval = Constants.LOCATION_TRACKING_INTERVAL_MINUTES,
            repeatIntervalTimeUnit = TimeUnit.MINUTES,
            // Flex interval - allows WorkManager to optimize battery
            flexTimeInterval = Constants.LOCATION_TRACKING_FLEX_MINUTES,
            flexTimeIntervalUnit = TimeUnit.MINUTES
        )
            .setConstraints(
                Constraints.Builder()
                    // Only run when battery is not low
                    .setRequiresBatteryNotLow(false) // Set to true for production
                    .build()
            )
            .setInputData(
                workDataOf(
                    LocationTrackingWorker.KEY_ATTENDANCE_ID to attendanceId,
                    LocationTrackingWorker.KEY_EMPLOYEE_ID to employeeId
                )
            )
            .addTag(Constants.LOCATION_TRACKING_WORK_TAG)
            .build()

        // Enqueue work with REPLACE policy
        // This ensures only one location tracking task runs at a time
        workManager.enqueueUniquePeriodicWork(
            Constants.LOCATION_TRACKING_WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }

    /**
     * Stop location tracking.
     * Cancels all WorkManager tasks with the location tracking work name.
     */
    private fun stopLocationTracking() {
        workManager.cancelUniqueWork(Constants.LOCATION_TRACKING_WORK_NAME)
    }

    /**
     * Logout user and stop tracking.
     */
    fun logout() {
        viewModelScope.launch {
            try {
                // Stop location tracking if active
                if (_activeAttendance.value?.isActive == true) {
                    stopLocationTracking()
                }

                // Clear session
                sessionManager.logout()

                _uiState.value = HomeUiState.LoggedOut

            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error("Failed to logout: ${e.message}")
            }
        }
    }

    /**
     * Format timestamp to readable time string.
     */
    fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat(Constants.DISPLAY_TIME_FORMAT, Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    /**
     * Format timestamp to readable date string.
     */
    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat(Constants.DISPLAY_DATE_FORMAT, Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    /**
     * Reset UI state.
     */
    fun resetState() {
        _uiState.value = HomeUiState.Idle
    }
}

/**
 * UI State for Home screen.
 */
sealed class HomeUiState {
    object Idle : HomeUiState()
    object Loading : HomeUiState()
    data class Success(val message: String) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
    object LoggedOut : HomeUiState()
}
