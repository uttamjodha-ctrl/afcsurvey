package com.company.attendancetracker.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Helper class for handling location permission requests and checks.
 *
 * Handles both foreground and background location permissions with
 * proper flow according to Android best practices.
 */
object LocationPermissionHelper {

    /**
     * Check if foreground location permission is granted.
     */
    fun hasForegroundLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Check if background location permission is granted.
     * Only relevant for Android 10 (Q) and above.
     */
    fun hasBackgroundLocationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // Background location not needed before Android 10
            true
        }
    }

    /**
     * Check if all required location permissions are granted.
     */
    fun hasAllLocationPermissions(context: Context): Boolean {
        return hasForegroundLocationPermission(context) &&
                hasBackgroundLocationPermission(context)
    }

    /**
     * Request foreground location permission.
     * Should be called first before requesting background permission.
     */
    fun requestForegroundLocationPermission(activity: Activity, requestCode: Int) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            requestCode
        )
    }

    /**
     * Request background location permission.
     * Should only be called AFTER foreground permission is granted.
     *
     * Google Play Policy requires:
     * 1. Request foreground permission first
     * 2. Show clear explanation to user why background access is needed
     * 3. Request background permission separately
     */
    fun requestBackgroundLocationPermission(activity: Activity, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                requestCode
            )
        }
    }

    /**
     * Check if we should show rationale for location permission.
     * Used to display explanation dialog.
     */
    fun shouldShowLocationRationale(activity: Activity): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    /**
     * Check if we should show rationale for background location.
     */
    fun shouldShowBackgroundLocationRationale(activity: Activity): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        } else {
            false
        }
    }

    /**
     * Open app settings page.
     * Used when user denies permission and we need them to enable it manually.
     */
    fun openAppSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
        context.startActivity(intent)
    }

    /**
     * Get explanation message for location permission request.
     */
    fun getForegroundLocationRationaleMessage(): String {
        return """
            This app requires location access to track your attendance and movement during work hours.

            Location tracking:
            • Only occurs between Punch IN and Punch OUT
            • Helps verify your work location
            • Records are stored securely

            Please grant location permission to continue.
        """.trimIndent()
    }

    /**
     * Get explanation message for background location permission.
     * This is REQUIRED by Google Play policy when requesting background location.
     */
    fun getBackgroundLocationRationaleMessage(): String {
        return """
            This app needs to track your location while running in the background.

            Why we need this:
            • Track your location even when the app is not actively open
            • Ensure continuous tracking during your work shift
            • Location is ONLY tracked between Punch IN and Punch OUT

            Your privacy:
            • Tracking stops when you Punch OUT
            • Location is never tracked outside work hours
            • Data is stored securely and used only for attendance verification

            This is required for accurate attendance and movement monitoring.
        """.trimIndent()
    }

    /**
     * Check notification permission (Android 13+).
     */
    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    /**
     * Request notification permission (Android 13+).
     */
    fun requestNotificationPermission(activity: Activity, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                requestCode
            )
        }
    }
}
