package com.company.attendancetracker.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Attendance entity representing punch IN/OUT records.
 *
 * Each record tracks one work session for an employee:
 * - punchInTime: When employee starts work
 * - punchOutTime: When employee ends work (null if still active)
 * - isActive: true between IN and OUT (used to identify current active session)
 */
@Entity(
    tableName = "attendance",
    foreignKeys = [
        ForeignKey(
            entity = Employee::class,
            parentColumns = ["id"],
            childColumns = ["employeeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("employeeId"), Index("date"), Index("isActive")]
)
data class Attendance(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // Reference to Employee table
    val employeeId: Long,

    // Date in format YYYY-MM-DD for easy filtering
    val date: String,

    // Timestamp when employee punched IN (milliseconds since epoch)
    val punchInTime: Long,

    // Timestamp when employee punched OUT (null if not yet punched out)
    val punchOutTime: Long? = null,

    // True when employee is currently punched IN (between IN and OUT)
    // Used to quickly find active attendance sessions
    val isActive: Boolean = true,

    // Optional: Notes or reason for attendance
    val notes: String = ""
)
