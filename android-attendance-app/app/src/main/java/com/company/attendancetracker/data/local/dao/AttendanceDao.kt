package com.company.attendancetracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.company.attendancetracker.data.local.entity.Attendance
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Attendance operations.
 */
@Dao
interface AttendanceDao {

    /**
     * Insert new attendance record (punch IN).
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attendance: Attendance): Long

    /**
     * Update existing attendance record (e.g., punch OUT).
     */
    @Update
    suspend fun update(attendance: Attendance)

    /**
     * Get active attendance for an employee.
     * Active means isActive = true (punched IN but not OUT).
     */
    @Query("SELECT * FROM attendance WHERE employeeId = :employeeId AND isActive = 1 LIMIT 1")
    suspend fun getActiveAttendance(employeeId: Long): Attendance?

    /**
     * Get active attendance as Flow for reactive UI updates.
     */
    @Query("SELECT * FROM attendance WHERE employeeId = :employeeId AND isActive = 1 LIMIT 1")
    fun getActiveAttendanceFlow(employeeId: Long): Flow<Attendance?>

    /**
     * Get attendance by ID.
     */
    @Query("SELECT * FROM attendance WHERE id = :id")
    suspend fun getById(id: Long): Attendance?

    /**
     * Get today's attendance for an employee.
     */
    @Query("SELECT * FROM attendance WHERE employeeId = :employeeId AND date = :date ORDER BY punchInTime DESC")
    fun getTodayAttendance(employeeId: Long, date: String): Flow<List<Attendance>>

    /**
     * Get all attendance records for an employee.
     */
    @Query("SELECT * FROM attendance WHERE employeeId = :employeeId ORDER BY punchInTime DESC")
    fun getAllAttendanceForEmployee(employeeId: Long): Flow<List<Attendance>>

    /**
     * Get attendance records for a date range.
     */
    @Query("SELECT * FROM attendance WHERE employeeId = :employeeId AND date BETWEEN :startDate AND :endDate ORDER BY punchInTime DESC")
    fun getAttendanceByDateRange(employeeId: Long, startDate: String, endDate: String): Flow<List<Attendance>>

    /**
     * Deactivate all active attendance sessions.
     * Used in case of cleanup or forced logout.
     */
    @Query("UPDATE attendance SET isActive = 0 WHERE employeeId = :employeeId AND isActive = 1")
    suspend fun deactivateAllSessions(employeeId: Long)

    /**
     * Delete all attendance records (for testing).
     */
    @Query("DELETE FROM attendance")
    suspend fun deleteAll()
}
