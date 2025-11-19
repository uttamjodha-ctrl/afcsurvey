package com.company.attendancetracker.data.repository

import com.company.attendancetracker.data.local.dao.EmployeeDao
import com.company.attendancetracker.data.local.entity.Employee
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for authentication operations.
 *
 * In production, this would:
 * 1. Call backend API for authentication
 * 2. Store auth tokens securely
 * 3. Handle token refresh
 * 4. Sync employee data from server
 *
 * Current implementation uses local Room database for demo purposes.
 */
class AuthRepository(
    private val employeeDao: EmployeeDao
) {

    /**
     * Authenticate user with username and password.
     *
     * PRODUCTION TODO:
     * - Send credentials to server API
     * - Receive and store JWT/OAuth tokens
     * - Store tokens in encrypted storage (EncryptedSharedPreferences)
     * - Never store passwords locally
     *
     * @return Employee if authentication successful, null otherwise
     */
    suspend fun login(username: String, password: String): Employee? {
        return withContext(Dispatchers.IO) {
            try {
                // For demo: Check against local database
                employeeDao.authenticate(username, password)

                // PRODUCTION CODE (commented out):
                // val response = apiService.login(LoginRequest(username, password))
                // if (response.isSuccessful) {
                //     val authToken = response.body()?.token
                //     sessionManager.saveAuthToken(authToken)
                //     return response.body()?.employee
                // }
                // return null
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    /**
     * Get employee by ID.
     */
    suspend fun getEmployeeById(id: Long): Employee? {
        return withContext(Dispatchers.IO) {
            try {
                employeeDao.getById(id)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    /**
     * Register new employee.
     * In production, this would be done via admin portal/API.
     */
    suspend fun registerEmployee(employee: Employee): Long {
        return withContext(Dispatchers.IO) {
            employeeDao.insert(employee)
        }
    }
}
