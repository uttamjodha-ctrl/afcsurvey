package com.company.attendancetracker.utils

/**
 * Constants used throughout the application.
 */
object Constants {

    // Location tracking configuration
    // ================================

    /**
     * Location tracking interval in MINUTES.
     *
     * HOW TO CHANGE: Modify this value to change how frequently
     * locations are captured. For example:
     * - 10 = Track every 10 minutes
     * - 15 = Track every 15 minutes
     * - 20 = Track every 20 minutes
     * - 30 = Track every 30 minutes
     *
     * PRODUCTION CONSIDERATIONS:
     * - Shorter intervals = More accurate tracking but higher battery drain
     * - Longer intervals = Better battery life but less accurate tracking
     * - Consider device battery level and adjust dynamically
     * - Comply with Google Play's background location policies
     */
    const val LOCATION_TRACKING_INTERVAL_MINUTES = 15L

    /**
     * Flex interval for WorkManager (in minutes).
     * WorkManager will execute within this window around the interval.
     * Setting to 5 minutes allows some flexibility for battery optimization.
     */
    const val LOCATION_TRACKING_FLEX_MINUTES = 5L

    /**
     * Minimum time between location updates (milliseconds).
     * Used by FusedLocationProviderClient.
     */
    const val MIN_LOCATION_UPDATE_INTERVAL_MS = 60000L // 1 minute

    /**
     * Minimum distance change for location updates (meters).
     * Update if moved at least this many meters.
     */
    const val MIN_LOCATION_UPDATE_DISTANCE_METERS = 10f // 10 meters

    /**
     * Location accuracy priority.
     * - PRIORITY_HIGH_ACCURACY: GPS + WiFi + Cellular
     * - PRIORITY_BALANCED_POWER_ACCURACY: WiFi + Cellular (no GPS)
     */
    const val LOCATION_PRIORITY = com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY

    // WorkManager configuration
    // =========================

    /**
     * Unique work name for location tracking.
     * Used to identify and cancel the periodic work.
     */
    const val LOCATION_TRACKING_WORK_NAME = "location_tracking_work"

    /**
     * WorkManager tag for location tracking tasks.
     */
    const val LOCATION_TRACKING_WORK_TAG = "location_tracking"

    // API configuration (for future backend integration)
    // ==================================================

    /**
     * Base URL for production API.
     * Replace with your actual backend URL.
     */
    const val API_BASE_URL = "https://api.yourcompany.com/"

    /**
     * API timeout in seconds.
     */
    const val API_TIMEOUT_SECONDS = 30L

    // Database configuration
    // ======================

    /**
     * Number of days to keep location logs before cleanup.
     */
    const val LOCATION_LOG_RETENTION_DAYS = 30

    // Permission request codes
    // ========================

    const val PERMISSION_REQUEST_LOCATION = 100
    const val PERMISSION_REQUEST_BACKGROUND_LOCATION = 101
    const val PERMISSION_REQUEST_NOTIFICATION = 102

    // Notification configuration
    // ==========================

    const val NOTIFICATION_CHANNEL_ID = "attendance_tracking_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Attendance Tracking"
    const val NOTIFICATION_ID = 1001

    // Date/Time formats
    // =================

    const val DATE_FORMAT = "yyyy-MM-dd"
    const val TIME_FORMAT = "HH:mm:ss"
    const val DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss"
    const val DISPLAY_TIME_FORMAT = "hh:mm a"
    const val DISPLAY_DATE_FORMAT = "MMM dd, yyyy"
}
