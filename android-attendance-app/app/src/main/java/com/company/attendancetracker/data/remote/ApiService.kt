package com.company.attendancetracker.data.remote

import com.company.attendancetracker.data.local.entity.Attendance
import com.company.attendancetracker.data.local.entity.Employee
import com.company.attendancetracker.data.local.entity.LocationLog
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API Service for backend integration.
 *
 * PRODUCTION SETUP GUIDE:
 * =======================
 *
 * 1. UPDATE BASE URL:
 *    - In Constants.kt, replace API_BASE_URL with your actual server URL
 *    - Example: "https://api.yourcompany.com/"
 *
 * 2. CREATE RETROFIT INSTANCE:
 *    ```
 *    object RetrofitClient {
 *        private val loggingInterceptor = HttpLoggingInterceptor().apply {
 *            level = HttpLoggingInterceptor.Level.BODY
 *        }
 *
 *        private val authInterceptor = Interceptor { chain ->
 *            val sessionManager = SessionManager(context)
 *            val token = runBlocking { sessionManager.getAuthToken() }
 *            val request = chain.request().newBuilder()
 *                .addHeader("Authorization", "Bearer $token")
 *                .build()
 *            chain.proceed(request)
 *        }
 *
 *        private val client = OkHttpClient.Builder()
 *            .addInterceptor(authInterceptor)
 *            .addInterceptor(loggingInterceptor)
 *            .connectTimeout(Constants.API_TIMEOUT_SECONDS, TimeUnit.SECONDS)
 *            .readTimeout(Constants.API_TIMEOUT_SECONDS, TimeUnit.SECONDS)
 *            .build()
 *
 *        val retrofit: Retrofit = Retrofit.Builder()
 *            .baseUrl(Constants.API_BASE_URL)
 *            .client(client)
 *            .addConverterFactory(GsonConverterFactory.create())
 *            .build()
 *
 *        val apiService: ApiService = retrofit.create(ApiService::class.java)
 *    }
 *    ```
 *
 * 3. USE IN REPOSITORIES:
 *    - Inject ApiService into repositories
 *    - Call API methods alongside local database operations
 *    - Implement offline-first strategy: save locally first, then sync to server
 *
 * 4. ERROR HANDLING:
 *    - Wrap API calls in try-catch
 *    - Handle network errors gracefully
 *    - Queue failed requests for retry
 *
 * 5. AUTHENTICATION:
 *    - Login endpoint returns JWT/OAuth token
 *    - Store token securely using EncryptedSharedPreferences
 *    - Add token to all requests via interceptor (shown above)
 *    - Implement token refresh logic when 401 received
 */
interface ApiService {

    // ========================================
    // AUTHENTICATION ENDPOINTS
    // ========================================

    /**
     * Login with username and password.
     *
     * Request Body:
     * {
     *   "username": "uttam01",
     *   "password": "123456"
     * }
     *
     * Response:
     * {
     *   "success": true,
     *   "token": "jwt_token_here",
     *   "employee": { ... employee data ... }
     * }
     */
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    /**
     * Logout (invalidate token).
     */
    @POST("auth/logout")
    suspend fun logout(
        @Header("Authorization") token: String
    ): Response<BasicResponse>

    /**
     * Refresh authentication token.
     */
    @POST("auth/refresh")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest
    ): Response<RefreshTokenResponse>

    // ========================================
    // EMPLOYEE ENDPOINTS
    // ========================================

    /**
     * Get employee profile.
     */
    @GET("employees/{id}")
    suspend fun getEmployee(
        @Header("Authorization") token: String,
        @Path("id") employeeId: Long
    ): Response<Employee>

    /**
     * Update employee profile.
     */
    @PUT("employees/{id}")
    suspend fun updateEmployee(
        @Header("Authorization") token: String,
        @Path("id") employeeId: Long,
        @Body employee: Employee
    ): Response<Employee>

    // ========================================
    // ATTENDANCE ENDPOINTS
    // ========================================

    /**
     * Punch IN - Start work shift.
     *
     * Request Body:
     * {
     *   "employeeId": 1,
     *   "timestamp": 1634567890000,
     *   "latitude": 12.9716,
     *   "longitude": 77.5946
     * }
     *
     * Response:
     * {
     *   "success": true,
     *   "attendanceId": 123,
     *   "message": "Punched in successfully"
     * }
     */
    @POST("attendance/punch-in")
    suspend fun punchIn(
        @Header("Authorization") token: String,
        @Body request: PunchInRequest
    ): Response<PunchResponse>

    /**
     * Punch OUT - End work shift.
     */
    @POST("attendance/punch-out")
    suspend fun punchOut(
        @Header("Authorization") token: String,
        @Body request: PunchOutRequest
    ): Response<PunchResponse>

    /**
     * Get attendance history for employee.
     */
    @GET("attendance/employee/{employeeId}")
    suspend fun getAttendanceHistory(
        @Header("Authorization") token: String,
        @Path("employeeId") employeeId: Long,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): Response<List<Attendance>>

    /**
     * Get active attendance for employee.
     */
    @GET("attendance/employee/{employeeId}/active")
    suspend fun getActiveAttendance(
        @Header("Authorization") token: String,
        @Path("employeeId") employeeId: Long
    ): Response<Attendance?>

    // ========================================
    // LOCATION TRACKING ENDPOINTS
    // ========================================

    /**
     * Upload single location log.
     *
     * Request Body:
     * {
     *   "attendanceId": 123,
     *   "timestamp": 1634567890000,
     *   "latitude": 12.9716,
     *   "longitude": 77.5946,
     *   "accuracy": 10.5
     * }
     *
     * Response:
     * {
     *   "success": true,
     *   "locationId": 456
     * }
     */
    @POST("locations/upload")
    suspend fun uploadLocation(
        @Header("Authorization") token: String,
        @Body request: LocationUploadRequest
    ): Response<LocationUploadResponse>

    /**
     * Batch upload multiple location logs.
     * More efficient than uploading one by one.
     */
    @POST("locations/upload-batch")
    suspend fun uploadLocationBatch(
        @Header("Authorization") token: String,
        @Body request: LocationBatchUploadRequest
    ): Response<LocationBatchUploadResponse>

    /**
     * Get location logs for specific attendance session.
     */
    @GET("locations/attendance/{attendanceId}")
    suspend fun getLocationLogs(
        @Header("Authorization") token: String,
        @Path("attendanceId") attendanceId: Long
    ): Response<List<LocationLog>>
}

// ========================================
// REQUEST/RESPONSE DATA CLASSES
// ========================================

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val token: String?,
    val employee: Employee?,
    val message: String?
)

data class RefreshTokenRequest(
    val refreshToken: String
)

data class RefreshTokenResponse(
    val success: Boolean,
    val token: String?,
    val message: String?
)

data class PunchInRequest(
    val employeeId: Long,
    val timestamp: Long,
    val latitude: Double?,
    val longitude: Double?
)

data class PunchOutRequest(
    val attendanceId: Long,
    val timestamp: Long,
    val latitude: Double?,
    val longitude: Double?
)

data class PunchResponse(
    val success: Boolean,
    val attendanceId: Long?,
    val message: String?
)

data class LocationUploadRequest(
    val attendanceId: Long,
    val timestamp: Long,
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float?,
    val speed: Float?
)

data class LocationUploadResponse(
    val success: Boolean,
    val locationId: Long?,
    val message: String?
)

data class LocationBatchUploadRequest(
    val locations: List<LocationUploadRequest>
)

data class LocationBatchUploadResponse(
    val success: Boolean,
    val uploadedCount: Int,
    val failedCount: Int,
    val message: String?
)

data class BasicResponse(
    val success: Boolean,
    val message: String?
)
