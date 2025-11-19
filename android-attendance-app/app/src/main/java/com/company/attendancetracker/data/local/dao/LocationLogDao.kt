package com.company.attendancetracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.company.attendancetracker.data.local.entity.LocationLog
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for LocationLog operations.
 */
@Dao
interface LocationLogDao {

    /**
     * Insert new location log.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(locationLog: LocationLog): Long

    /**
     * Insert multiple location logs at once.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(locationLogs: List<LocationLog>)

    /**
     * Update location log (e.g., mark as synced).
     */
    @Update
    suspend fun update(locationLog: LocationLog)

    /**
     * Get all location logs for a specific attendance session.
     */
    @Query("SELECT * FROM location_logs WHERE attendanceId = :attendanceId ORDER BY timestamp DESC")
    fun getLogsForAttendance(attendanceId: Long): Flow<List<LocationLog>>

    /**
     * Get location logs for an attendance session (non-Flow version for WorkManager).
     */
    @Query("SELECT * FROM location_logs WHERE attendanceId = :attendanceId ORDER BY timestamp DESC")
    suspend fun getLogsForAttendanceSync(attendanceId: Long): List<LocationLog>

    /**
     * Get unsynced location logs (for server sync).
     */
    @Query("SELECT * FROM location_logs WHERE synced = 0 ORDER BY timestamp ASC")
    suspend fun getUnsyncedLogs(): List<LocationLog>

    /**
     * Get location logs for a specific time range.
     */
    @Query("SELECT * FROM location_logs WHERE attendanceId = :attendanceId AND timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp ASC")
    fun getLogsByTimeRange(attendanceId: Long, startTime: Long, endTime: Long): Flow<List<LocationLog>>

    /**
     * Mark location log as synced.
     */
    @Query("UPDATE location_logs SET synced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: Long)

    /**
     * Mark multiple logs as synced.
     */
    @Query("UPDATE location_logs SET synced = 1 WHERE id IN (:ids)")
    suspend fun markMultipleAsSynced(ids: List<Long>)

    /**
     * Get total count of logs for an attendance session.
     */
    @Query("SELECT COUNT(*) FROM location_logs WHERE attendanceId = :attendanceId")
    suspend fun getLogCount(attendanceId: Long): Int

    /**
     * Delete location logs older than a specific timestamp (for cleanup).
     */
    @Query("DELETE FROM location_logs WHERE timestamp < :timestamp")
    suspend fun deleteOlderThan(timestamp: Long)

    /**
     * Delete all location logs (for testing).
     */
    @Query("DELETE FROM location_logs")
    suspend fun deleteAll()
}
