package com.company.attendancetracker.data.repository

import com.company.attendancetracker.data.local.dao.LocationLogDao
import com.company.attendancetracker.data.local.entity.LocationLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Repository for location tracking operations.
 *
 * Handles saving and retrieving GPS location logs.
 */
class LocationRepository(
    private val locationLogDao: LocationLogDao
) {

    /**
     * Save location log to database.
     *
     * @param attendanceId The attendance session this location belongs to
     * @param latitude GPS latitude
     * @param longitude GPS longitude
     * @param accuracy Location accuracy in meters
     * @param speed Speed in meters/second (optional)
     * @return Location log ID
     */
    suspend fun saveLocationLog(
        attendanceId: Long,
        latitude: Double,
        longitude: Double,
        accuracy: Float? = null,
        speed: Float? = null
    ): Long {
        return withContext(Dispatchers.IO) {
            val locationLog = LocationLog(
                attendanceId = attendanceId,
                timestamp = System.currentTimeMillis(),
                latitude = latitude,
                longitude = longitude,
                accuracy = accuracy,
                speed = speed,
                synced = false
            )

            val logId = locationLogDao.insert(locationLog)

            // PRODUCTION TODO: Queue for server sync
            // workManager.enqueue(SyncLocationWorker.createRequest())
            // OR send immediately:
            // apiService.uploadLocation(locationLog)

            logId
        }
    }

    /**
     * Get location logs for specific attendance session.
     */
    fun getLogsForAttendance(attendanceId: Long): Flow<List<LocationLog>> {
        return locationLogDao.getLogsForAttendance(attendanceId)
    }

    /**
     * Get location logs for attendance (non-Flow version).
     * Useful for WorkManager or background operations.
     */
    suspend fun getLogsForAttendanceSync(attendanceId: Long): List<LocationLog> {
        return withContext(Dispatchers.IO) {
            locationLogDao.getLogsForAttendanceSync(attendanceId)
        }
    }

    /**
     * Get unsynced location logs for server upload.
     */
    suspend fun getUnsyncedLogs(): List<LocationLog> {
        return withContext(Dispatchers.IO) {
            locationLogDao.getUnsyncedLogs()
        }
    }

    /**
     * Mark location log as synced to server.
     */
    suspend fun markAsSynced(logId: Long) {
        withContext(Dispatchers.IO) {
            locationLogDao.markAsSynced(logId)
        }
    }

    /**
     * Mark multiple logs as synced.
     */
    suspend fun markMultipleAsSynced(logIds: List<Long>) {
        withContext(Dispatchers.IO) {
            locationLogDao.markMultipleAsSynced(logIds)
        }
    }

    /**
     * Get count of logs for attendance session.
     */
    suspend fun getLogCount(attendanceId: Long): Int {
        return withContext(Dispatchers.IO) {
            locationLogDao.getLogCount(attendanceId)
        }
    }

    /**
     * Clean up old location logs (e.g., older than 30 days).
     * Can be scheduled as periodic WorkManager task.
     */
    suspend fun cleanupOldLogs(daysToKeep: Int = 30) {
        withContext(Dispatchers.IO) {
            val cutoffTime = System.currentTimeMillis() - (daysToKeep * 24 * 60 * 60 * 1000L)
            locationLogDao.deleteOlderThan(cutoffTime)
        }
    }
}
