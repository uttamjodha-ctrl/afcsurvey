package com.company.attendancetracker.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * SessionManager handles user session persistence using DataStore.
 *
 * Stores:
 * - Logged in employee ID
 * - Active attendance ID (when punched IN)
 * - Auth token (for production API integration)
 *
 * DataStore is preferred over SharedPreferences for:
 * - Type safety
 * - Coroutine support
 * - Data consistency guarantees
 */
class SessionManager(private val context: Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session_prefs")

        private val KEY_IS_LOGGED_IN = stringPreferencesKey("is_logged_in")
        private val KEY_EMPLOYEE_ID = longPreferencesKey("employee_id")
        private val KEY_ACTIVE_ATTENDANCE_ID = longPreferencesKey("active_attendance_id")
        private val KEY_AUTH_TOKEN = stringPreferencesKey("auth_token")
    }

    /**
     * Save login session.
     * Called after successful authentication.
     */
    suspend fun saveLoginSession(employeeId: Long) {
        context.dataStore.edit { preferences ->
            preferences[KEY_IS_LOGGED_IN] = "true"
            preferences[KEY_EMPLOYEE_ID] = employeeId
        }
    }

    /**
     * Check if user is logged in.
     */
    suspend fun isLoggedIn(): Boolean {
        val preferences = context.dataStore.data.first()
        return preferences[KEY_IS_LOGGED_IN] == "true"
    }

    /**
     * Get logged in employee ID.
     */
    suspend fun getEmployeeId(): Long? {
        val preferences = context.dataStore.data.first()
        return preferences[KEY_EMPLOYEE_ID]
    }

    /**
     * Get employee ID as Flow for reactive updates.
     */
    fun getEmployeeIdFlow(): Flow<Long?> {
        return context.dataStore.data.map { preferences ->
            preferences[KEY_EMPLOYEE_ID]
        }
    }

    /**
     * Save active attendance ID (when punched IN).
     * This is used to resume tracking after app restart.
     */
    suspend fun saveActiveAttendanceId(attendanceId: Long) {
        context.dataStore.edit { preferences ->
            preferences[KEY_ACTIVE_ATTENDANCE_ID] = attendanceId
        }
    }

    /**
     * Get active attendance ID.
     */
    suspend fun getActiveAttendanceId(): Long? {
        val preferences = context.dataStore.data.first()
        return preferences[KEY_ACTIVE_ATTENDANCE_ID]
    }

    /**
     * Clear active attendance ID (when punched OUT).
     */
    suspend fun clearActiveAttendanceId() {
        context.dataStore.edit { preferences ->
            preferences.remove(KEY_ACTIVE_ATTENDANCE_ID)
        }
    }

    /**
     * Save auth token (for production API authentication).
     */
    suspend fun saveAuthToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_AUTH_TOKEN] = token
        }
    }

    /**
     * Get auth token.
     */
    suspend fun getAuthToken(): String? {
        val preferences = context.dataStore.data.first()
        return preferences[KEY_AUTH_TOKEN]
    }

    /**
     * Logout - Clear all session data.
     */
    suspend fun logout() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    /**
     * Clear only attendance session (for testing/cleanup).
     * Keeps user logged in.
     */
    suspend fun clearAttendanceSession() {
        context.dataStore.edit { preferences ->
            preferences.remove(KEY_ACTIVE_ATTENDANCE_ID)
        }
    }
}
