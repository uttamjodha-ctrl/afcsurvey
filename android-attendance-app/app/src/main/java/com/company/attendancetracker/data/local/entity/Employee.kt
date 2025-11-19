package com.company.attendancetracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Employee entity representing user account information.
 *
 * This table stores employee credentials and personal details.
 * In production, passwords should be hashed, not stored in plain text.
 */
@Entity(tableName = "employees")
data class Employee(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val username: String,

    // WARNING: In production, NEVER store plain text passwords!
    // Use proper hashing (bcrypt, argon2) on the server side
    val password: String,

    val name: String,

    // Unique employee identifier (e.g., "EMP001")
    val employeeId: String,

    val department: String = "",

    val phone: String = "",

    val email: String = "",

    val createdAt: Long = System.currentTimeMillis()
)
