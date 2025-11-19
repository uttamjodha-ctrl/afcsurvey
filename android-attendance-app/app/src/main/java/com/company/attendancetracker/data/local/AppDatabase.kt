package com.company.attendancetracker.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.company.attendancetracker.data.local.dao.AttendanceDao
import com.company.attendancetracker.data.local.dao.EmployeeDao
import com.company.attendancetracker.data.local.dao.LocationLogDao
import com.company.attendancetracker.data.local.entity.Attendance
import com.company.attendancetracker.data.local.entity.Employee
import com.company.attendancetracker.data.local.entity.LocationLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Room Database for Attendance Tracker app.
 *
 * Contains three tables:
 * - employees: User accounts and credentials
 * - attendance: Punch IN/OUT records
 * - location_logs: GPS tracking data
 *
 * Database version should be incremented when schema changes.
 */
@Database(
    entities = [
        Employee::class,
        Attendance::class,
        LocationLog::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun employeeDao(): EmployeeDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun locationLogDao(): LocationLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "attendance_tracker_db"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    /**
     * Database callback to seed initial demo data.
     * This runs when database is created for the first time.
     */
    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database.employeeDao())
                }
            }
        }

        /**
         * Seed demo employee data.
         * In production, employees would be synced from server.
         */
        suspend fun populateDatabase(employeeDao: EmployeeDao) {
            // Check if already populated
            if (employeeDao.getCount() > 0) return

            // Insert demo employees
            val demoEmployees = listOf(
                Employee(
                    username = "uttam01",
                    password = "123456", // WARNING: Plain text for demo only!
                    name = "Uttam Singh",
                    employeeId = "EMP001",
                    department = "Engineering",
                    phone = "+91-9876543210",
                    email = "uttam@company.com"
                ),
                Employee(
                    username = "john.doe",
                    password = "password",
                    name = "John Doe",
                    employeeId = "EMP002",
                    department = "Sales",
                    phone = "+91-9876543211",
                    email = "john@company.com"
                ),
                Employee(
                    username = "jane.smith",
                    password = "password",
                    name = "Jane Smith",
                    employeeId = "EMP003",
                    department = "Marketing",
                    phone = "+91-9876543212",
                    email = "jane@company.com"
                )
            )

            demoEmployees.forEach { employee ->
                employeeDao.insert(employee)
            }
        }
    }
}
