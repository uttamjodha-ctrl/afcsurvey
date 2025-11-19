package com.company.attendancetracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.company.attendancetracker.data.local.entity.Employee
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Employee operations.
 */
@Dao
interface EmployeeDao {

    /**
     * Insert a new employee.
     * Used for initial setup/seeding demo data.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(employee: Employee): Long

    /**
     * Authenticate employee with username and password.
     * Returns employee if credentials match, null otherwise.
     *
     * PRODUCTION NOTE: Authentication should happen on the server side!
     * This is for demo/offline mode only.
     */
    @Query("SELECT * FROM employees WHERE username = :username AND password = :password LIMIT 1")
    suspend fun authenticate(username: String, password: String): Employee?

    /**
     * Get employee by ID.
     */
    @Query("SELECT * FROM employees WHERE id = :id")
    suspend fun getById(id: Long): Employee?

    /**
     * Get employee by username.
     */
    @Query("SELECT * FROM employees WHERE username = :username")
    suspend fun getByUsername(username: String): Employee?

    /**
     * Get all employees as Flow for reactive updates.
     */
    @Query("SELECT * FROM employees ORDER BY name")
    fun getAllFlow(): Flow<List<Employee>>

    /**
     * Check if any employees exist in database.
     * Used to determine if we need to seed demo data.
     */
    @Query("SELECT COUNT(*) FROM employees")
    suspend fun getCount(): Int

    /**
     * Delete all employees (for testing/reset).
     */
    @Query("DELETE FROM employees")
    suspend fun deleteAll()
}
