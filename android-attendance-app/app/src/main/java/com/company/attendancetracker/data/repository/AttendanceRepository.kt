package com.company.attendancetracker.data.repository

import com.company.attendancetracker.data.local.dao.AttendanceDao
import com.company.attendancetracker.data.local.entity.Attendance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

/**
 * Repository for attendance operations.
 *
 * Handles punch IN/OUT and attendance record management.
 */
class AttendanceRepository(
    private val attendanceDao: AttendanceDao
) {

    /**
     * Punch IN - Create new attendance record.
     *
     * @return Attendance ID for linking location logs
     */
    suspend fun punchIn(employeeId: Long): Long {
        return withContext(Dispatchers.IO) {
            val currentTime = System.currentTimeMillis()
            val date = getCurrentDate()

            // Deactivate any existing active sessions (cleanup)
            attendanceDao.deactivateAllSessions(employeeId)

            // Create new attendance record
            val attendance = Attendance(
                employeeId = employeeId,
                date = date,
                punchInTime = currentTime,
                punchOutTime = null,
                isActive = true
            )

            val attendanceId = attendanceDao.insert(attendance)

            // PRODUCTION TODO: Sync to server
            // apiService.punchIn(PunchInRequest(employeeId, currentTime))

            attendanceId
        }
    }

    /**
     * Punch OUT - Update attendance record with punch out time.
     */
    suspend fun punchOut(attendanceId: Long) {
        withContext(Dispatchers.IO) {
            val attendance = attendanceDao.getById(attendanceId)
            if (attendance != null && attendance.isActive) {
                val updatedAttendance = attendance.copy(
                    punchOutTime = System.currentTimeMillis(),
                    isActive = false
                )
                attendanceDao.update(updatedAttendance)

                // PRODUCTION TODO: Sync to server
                // apiService.punchOut(PunchOutRequest(attendanceId, updatedAttendance.punchOutTime))
            }
        }
    }

    /**
     * Get active attendance session for employee.
     */
    suspend fun getActiveAttendance(employeeId: Long): Attendance? {
        return withContext(Dispatchers.IO) {
            attendanceDao.getActiveAttendance(employeeId)
        }
    }

    /**
     * Get active attendance as Flow for reactive updates.
     */
    fun getActiveAttendanceFlow(employeeId: Long): Flow<Attendance?> {
        return attendanceDao.getActiveAttendanceFlow(employeeId)
    }

    /**
     * Get today's attendance records.
     */
    fun getTodayAttendance(employeeId: Long): Flow<List<Attendance>> {
        val today = getCurrentDate()
        return attendanceDao.getTodayAttendance(employeeId, today)
    }

    /**
     * Get all attendance records for employee.
     */
    fun getAllAttendance(employeeId: Long): Flow<List<Attendance>> {
        return attendanceDao.getAllAttendanceForEmployee(employeeId)
    }

    /**
     * Get attendance by ID.
     */
    suspend fun getAttendanceById(id: Long): Attendance? {
        return withContext(Dispatchers.IO) {
            attendanceDao.getById(id)
        }
    }

    /**
     * Get current date in YYYY-MM-DD format.
     */
    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}
