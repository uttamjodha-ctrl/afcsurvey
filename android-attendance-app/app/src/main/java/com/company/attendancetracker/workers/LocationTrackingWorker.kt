package com.company.attendancetracker.workers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.company.attendancetracker.data.local.AppDatabase
import com.company.attendancetracker.data.repository.AttendanceRepository
import com.company.attendancetracker.data.repository.LocationRepository
import com.company.attendancetracker.utils.SessionManager
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.tasks.await

/**
 * WorkManager worker for periodic location tracking.
 *
 * This worker runs every X minutes (configured in Constants.LOCATION_TRACKING_INTERVAL_MINUTES)
 * while the employee is punched IN.
 *
 * RESPONSIBILITIES:
 * 1. Check if user is still punched IN
 * 2. Get current GPS location
 * 3. Save location to Room database
 * 4. (Optional) Upload to server
 *
 * LIFECYCLE:
 * - Started when employee punches IN
 * - Runs periodically in background
 * - Stopped when employee punches OUT
 * - Survives app restarts and device reboots (with proper configuration)
 *
 * GOOGLE PLAY POLICY COMPLIANCE:
 * - Only runs between punch IN and OUT
 * - Clear user consent before starting
 * - Transparent about location usage
 * - Minimal battery impact through WorkManager optimization
 */
class LocationTrackingWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        private const val TAG = "LocationTrackingWorker"
        const val KEY_ATTENDANCE_ID = "attendance_id"
        const val KEY_EMPLOYEE_ID = "employee_id"
    }

    private val database = AppDatabase.getDatabase(context)
    private val locationRepository = LocationRepository(database.locationLogDao())
    private val attendanceRepository = AttendanceRepository(database.attendanceDao())
    private val sessionManager = SessionManager(context)
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    /**
     * Main work execution.
     * Called by WorkManager at scheduled intervals.
     */
    override suspend fun doWork(): Result {
        Log.d(TAG, "LocationTrackingWorker started")

        try {
            // Step 1: Verify attendance session is still active
            val attendanceId = sessionManager.getActiveAttendanceId()
            if (attendanceId == null) {
                Log.w(TAG, "No active attendance session found. Stopping work.")
                return Result.success() // This will stop further executions
            }

            // Verify attendance is actually active in database
            val attendance = attendanceRepository.getAttendanceById(attendanceId)
            if (attendance == null || !attendance.isActive) {
                Log.w(TAG, "Attendance session is not active. Stopping work.")
                sessionManager.clearActiveAttendanceId()
                return Result.success()
            }

            // Step 2: Check location permissions
            if (!hasLocationPermission()) {
                Log.e(TAG, "Location permission not granted. Cannot track location.")
                return Result.failure()
            }

            // Step 3: Get current location
            val location = getCurrentLocation()
            if (location == null) {
                Log.w(TAG, "Could not obtain location. Will retry.")
                return Result.retry()
            }

            // Step 4: Save location to database
            val logId = locationRepository.saveLocationLog(
                attendanceId = attendanceId,
                latitude = location.latitude,
                longitude = location.longitude,
                accuracy = location.accuracy,
                speed = if (location.hasSpeed()) location.speed else null
            )

            Log.d(
                TAG,
                "Location saved: ID=$logId, " +
                        "Lat=${location.latitude}, " +
                        "Lng=${location.longitude}, " +
                        "Accuracy=${location.accuracy}m"
            )

            // Step 5: (Optional) Upload to server
            // Uncomment when backend API is ready:
            // uploadLocationToServer(logId, location)

            return Result.success()

        } catch (e: Exception) {
            Log.e(TAG, "Error in LocationTrackingWorker", e)
            // Retry on failure (WorkManager will handle retry logic)
            return Result.retry()
        }
    }

    /**
     * Check if location permission is granted.
     */
    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Get current GPS location using FusedLocationProviderClient.
     *
     * This uses Google Play Services for best accuracy and battery efficiency.
     * Combines GPS, WiFi, and cellular data for location.
     */
    private suspend fun getCurrentLocation(): Location? {
        try {
            // Check permission again (required by Android)
            if (!hasLocationPermission()) {
                return null
            }

            // Request current location with high accuracy
            val location = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).await()

            if (location == null) {
                Log.w(TAG, "Location is null. GPS might be disabled.")
                // Try last known location as fallback
                return fusedLocationClient.lastLocation.await()
            }

            return location

        } catch (e: SecurityException) {
            Log.e(TAG, "Security exception getting location", e)
            return null
        } catch (e: Exception) {
            Log.e(TAG, "Error getting location", e)
            return null
        }
    }

    /**
     * Upload location to server (for production).
     *
     * IMPLEMENTATION GUIDE:
     * 1. Create API endpoint: POST /api/locations
     * 2. Send location data with auth token
     * 3. Mark as synced in local database on success
     * 4. Handle failures with retry logic
     *
     * Example:
     * ```
     * private suspend fun uploadLocationToServer(logId: Long, location: Location) {
     *     try {
     *         val authToken = sessionManager.getAuthToken()
     *         val request = LocationUploadRequest(
     *             attendanceId = attendanceId,
     *             latitude = location.latitude,
     *             longitude = location.longitude,
     *             timestamp = System.currentTimeMillis(),
     *             accuracy = location.accuracy
     *         )
     *
     *         val response = apiService.uploadLocation(
     *             token = "Bearer $authToken",
     *             request = request
     *         )
     *
     *         if (response.isSuccessful) {
     *             locationRepository.markAsSynced(logId)
     *             Log.d(TAG, "Location uploaded to server successfully")
     *         } else {
     *             Log.e(TAG, "Failed to upload location: ${response.code()}")
     *         }
     *     } catch (e: Exception) {
     *         Log.e(TAG, "Error uploading location to server", e)
     *         // Location is still saved locally, will retry later
     *     }
     * }
     * ```
     */

    /**
     * DEVICE REBOOT HANDLING:
     *
     * To make WorkManager survive device reboots:
     *
     * 1. Add permission in AndroidManifest.xml (already added):
     *    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
     *
     * 2. WorkManager automatically reschedules work after reboot if:
     *    - Work was scheduled with setInitialDelay() or as periodic
     *    - Work is not in CANCELLED state
     *
     * 3. On app startup (in Application.onCreate()):
     *    - Check if there's an active attendance session
     *    - If yes, ensure WorkManager task is scheduled
     *    - This handles edge cases where WorkManager might not restore
     *
     * See AttendanceApplication.kt for implementation.
     */
}
