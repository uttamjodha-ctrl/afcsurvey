package com.company.attendancetracker.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * LocationLog entity for storing GPS coordinates tracked during work hours.
 *
 * Each record is linked to an Attendance session and captured periodically
 * (e.g., every 10-20 minutes) while employee is punched IN.
 *
 * These logs can be:
 * 1. Displayed to employee in real-time
 * 2. Synced to server for management review
 * 3. Used for route tracking and movement analysis
 */
@Entity(
    tableName = "location_logs",
    foreignKeys = [
        ForeignKey(
            entity = Attendance::class,
            parentColumns = ["id"],
            childColumns = ["attendanceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("attendanceId"), Index("timestamp")]
)
data class LocationLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // Reference to Attendance session
    val attendanceId: Long,

    // When this location was captured
    val timestamp: Long,

    // GPS coordinates
    val latitude: Double,
    val longitude: Double,

    // Location accuracy in meters (from GPS)
    val accuracy: Float? = null,

    // Optional: Speed in meters/second
    val speed: Float? = null,

    // Flag to track if this has been synced to server
    val synced: Boolean = false,

    // Optional: Address reverse geocoded from coordinates
    val address: String? = null
)
