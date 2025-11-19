package com.company.attendancetracker

import android.app.Application
import android.util.Log
import androidx.work.*
import com.company.attendancetracker.data.local.AppDatabase
import com.company.attendancetracker.data.repository.AttendanceRepository
import com.company.attendancetracker.utils.Constants
import com.company.attendancetracker.utils.SessionManager
import com.company.attendancetracker.workers.LocationTrackingWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * Application class - Entry point for app initialization.
 *
 * RESPONSIBILITIES:
 * 1. Initialize WorkManager configuration
 * 2. Restore location tracking after device reboot
 * 3. Initialize any app-wide singletons
 *
 * DEVICE REBOOT HANDLING:
 * When device reboots:
 * - WorkManager automatically reschedules periodic work
 * - But we need to verify if there's an active attendance session
 * - If yes, ensure WorkManager task is properly scheduled
 * - This handles edge cases where WorkManager might not restore correctly
 */
class AttendanceApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Application created")

        // Initialize WorkManager with custom configuration
        initializeWorkManager()

        // Check and restore location tracking if needed
        restoreLocationTrackingIfNeeded()
    }

    /**
     * Initialize WorkManager with custom configuration.
     *
     * Configuration:
     * - Sets minimum log level for debugging
     * - Configures worker factory if needed
     * - Sets execution guarantees
     */
    private fun initializeWorkManager() {
        val config = Configuration.Builder()
            .setMinimumLoggingLevel(Log.INFO)
            // For production, consider using custom WorkerFactory for dependency injection
            .build()

        try {
            WorkManager.initialize(this, config)
            Log.d(TAG, "WorkManager initialized")
        } catch (e: IllegalStateException) {
            // WorkManager already initialized
            Log.d(TAG, "WorkManager already initialized")
        }
    }

    /**
     * Restore location tracking after app restart or device reboot.
     *
     * SCENARIO:
     * 1. Employee punches IN, tracking starts
     * 2. Device reboots or app is killed
     * 3. On next app launch, we check if there's an active attendance session
     * 4. If yes, ensure WorkManager task is running
     *
     * This ensures continuous tracking even after unexpected app termination.
     */
    private fun restoreLocationTrackingIfNeeded() {
        applicationScope.launch {
            try {
                val database = AppDatabase.getDatabase(applicationContext)
                val sessionManager = SessionManager(applicationContext)
                val attendanceRepository = AttendanceRepository(database.attendanceDao())

                // Check if there's an active attendance session
                val activeAttendanceId = sessionManager.getActiveAttendanceId()
                Log.d(TAG, "Checking active attendance: $activeAttendanceId")

                if (activeAttendanceId != null) {
                    // Verify attendance is actually active in database
                    val attendance = attendanceRepository.getAttendanceById(activeAttendanceId)

                    if (attendance != null && attendance.isActive) {
                        Log.d(TAG, "Active attendance found. Restoring location tracking...")

                        // Restart location tracking
                        startLocationTracking(activeAttendanceId, attendance.employeeId)

                        Log.d(TAG, "Location tracking restored successfully")
                    } else {
                        // Attendance not active, clear session
                        Log.d(TAG, "Attendance not active. Clearing session.")
                        sessionManager.clearActiveAttendanceId()
                    }
                } else {
                    Log.d(TAG, "No active attendance session found")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error restoring location tracking", e)
            }
        }
    }

    /**
     * Start location tracking with WorkManager.
     * Same logic as in HomeViewModel.
     */
    private fun startLocationTracking(attendanceId: Long, employeeId: Long) {
        val workManager = WorkManager.getInstance(applicationContext)

        val workRequest = PeriodicWorkRequestBuilder<LocationTrackingWorker>(
            repeatInterval = Constants.LOCATION_TRACKING_INTERVAL_MINUTES,
            repeatIntervalTimeUnit = TimeUnit.MINUTES,
            flexTimeInterval = Constants.LOCATION_TRACKING_FLEX_MINUTES,
            flexTimeIntervalUnit = TimeUnit.MINUTES
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(false)
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

        workManager.enqueueUniquePeriodicWork(
            Constants.LOCATION_TRACKING_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP, // KEEP existing if already running
            workRequest
        )
    }

    companion object {
        private const val TAG = "AttendanceApplication"
    }
}
