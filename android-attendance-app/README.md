# Employee Attendance & Geo-Tracking Android App

A complete Android application for employee attendance management with GPS location tracking built using Kotlin, Jetpack Compose, and modern Android architecture components.

## Features

- **User Authentication**: Secure login system with demo credentials
- **Attendance Management**: Punch IN/OUT functionality with timestamp tracking
- **GPS Location Tracking**: Periodic background location tracking during work hours (every 15 minutes)
- **Real-time Updates**: Live display of location logs and attendance status
- **Offline-First**: All data stored locally using Room database
- **Background Processing**: WorkManager handles location tracking even when app is closed
- **Permission Management**: Proper runtime permission handling for location access
- **MVVM Architecture**: Clean separation of concerns with Repository pattern
- **Production-Ready API Integration**: Complete Retrofit setup for future backend connection

## Tech Stack

### Core Technologies
- **Language**: Kotlin 100%
- **Minimum SDK**: 26 (Android 8.0 Oreo)
- **Target SDK**: 34
- **UI Framework**: Jetpack Compose (Material 3)

### Architecture & Libraries
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: Room (SQLite)
- **Background Tasks**: WorkManager
- **Dependency Injection**: Manual (ViewModelFactory)
- **State Management**: StateFlow
- **Navigation**: Jetpack Navigation Compose
- **Location Services**: Google Play Services Location
- **Session Management**: DataStore Preferences
- **API Client**: Retrofit + OkHttp (ready for integration)

## Project Structure

```
app/src/main/
├── AndroidManifest.xml
└── java/com/company/attendancetracker/
    ├── AttendanceApplication.kt          # App initialization & reboot handling
    ├── MainActivity.kt                   # Main entry point & navigation
    ├── data/
    │   ├── local/
    │   │   ├── AppDatabase.kt           # Room database setup
    │   │   ├── dao/                     # Data Access Objects
    │   │   │   ├── EmployeeDao.kt
    │   │   │   ├── AttendanceDao.kt
    │   │   │   └── LocationLogDao.kt
    │   │   └── entity/                  # Room entities
    │   │       ├── Employee.kt
    │   │       ├── Attendance.kt
    │   │       └── LocationLog.kt
    │   ├── remote/
    │   │   └── ApiService.kt            # Retrofit API interface
    │   └── repository/                   # Repository pattern
    │       ├── AuthRepository.kt
    │       ├── AttendanceRepository.kt
    │       └── LocationRepository.kt
    ├── ui/
    │   ├── login/
    │   │   ├── LoginScreen.kt           # Login UI
    │   │   └── LoginViewModel.kt
    │   ├── home/
    │   │   ├── HomeScreen.kt            # Home dashboard UI
    │   │   └── HomeViewModel.kt
    │   └── theme/                        # Compose theming
    │       ├── Color.kt
    │       ├── Theme.kt
    │       └── Type.kt
    ├── workers/
    │   └── LocationTrackingWorker.kt    # Background location tracking
    └── utils/
        ├── Constants.kt                  # App constants
        ├── SessionManager.kt             # Session persistence
        └── LocationPermissionHelper.kt   # Permission utilities
```

## Database Schema

### Employee Table
| Column | Type | Description |
|--------|------|-------------|
| id | Long | Primary key |
| username | String | Login username |
| password | String | Password (plain text for demo) |
| name | String | Employee full name |
| employeeId | String | Unique employee ID (e.g., "EMP001") |
| department | String | Department name |
| phone | String | Contact number |
| email | String | Email address |
| createdAt | Long | Registration timestamp |

### Attendance Table
| Column | Type | Description |
|--------|------|-------------|
| id | Long | Primary key |
| employeeId | Long | Foreign key to Employee |
| date | String | Date (YYYY-MM-DD) |
| punchInTime | Long | Punch IN timestamp |
| punchOutTime | Long? | Punch OUT timestamp (nullable) |
| isActive | Boolean | Currently active session |
| notes | String | Optional notes |

### LocationLog Table
| Column | Type | Description |
|--------|------|-------------|
| id | Long | Primary key |
| attendanceId | Long | Foreign key to Attendance |
| timestamp | Long | Location capture time |
| latitude | Double | GPS latitude |
| longitude | Double | GPS longitude |
| accuracy | Float? | Location accuracy (meters) |
| speed | Float? | Speed (m/s) |
| synced | Boolean | Synced to server flag |
| address | String? | Reverse geocoded address |

## Setup Instructions

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17 or higher
- Android SDK with API 34
- Physical Android device or emulator with Google Play Services

### Step 1: Create New Project in Android Studio

1. Open Android Studio
2. Click **File** → **New** → **New Project**
3. Select **Empty Activity**
4. Click **Next**
5. Configure your project:
   - **Name**: AttendanceTracker
   - **Package name**: com.company.attendancetracker
   - **Save location**: Choose your preferred location
   - **Language**: Kotlin
   - **Minimum SDK**: API 26
   - **Build configuration language**: Kotlin DSL
6. Click **Finish**

### Step 2: Replace Project Files

After the project is created:

1. **Copy all files** from this repository to your project directory:
   ```bash
   cp -r android-attendance-app/* /path/to/your/project/
   ```

2. **Or manually replace** the following files:
   - `settings.gradle.kts`
   - `build.gradle.kts` (project level)
   - `app/build.gradle.kts`
   - `gradle.properties`
   - `app/src/main/AndroidManifest.xml`
   - All files in `app/src/main/java/` directory
   - All files in `app/src/main/res/` directory
   - `app/proguard-rules.pro`

### Step 3: Sync Gradle

1. Click **File** → **Sync Project with Gradle Files**
2. Wait for Gradle to download all dependencies (may take 5-10 minutes)
3. If you see any errors, click **Build** → **Clean Project**, then **Build** → **Rebuild Project**

### Step 4: Configure Location Tracking Interval

To change the location tracking frequency:

1. Open `app/src/main/java/com/company/attendancetracker/utils/Constants.kt`
2. Modify the `LOCATION_TRACKING_INTERVAL_MINUTES` constant:
   ```kotlin
   const val LOCATION_TRACKING_INTERVAL_MINUTES = 15L  // Change to desired interval
   ```
   - **10** = Track every 10 minutes
   - **15** = Track every 15 minutes (default)
   - **20** = Track every 20 minutes
   - **30** = Track every 30 minutes

> **Note**: Android WorkManager requires minimum 15 minutes for periodic work. For more frequent updates, consider using ForegroundService.

### Step 5: Run the App

1. **Connect a physical device** (recommended for location testing) or start an emulator
2. Enable **Developer Options** and **USB Debugging** on your device
3. Click the **Run** button (green play icon) in Android Studio
4. Select your device from the list
5. Wait for the app to install and launch

### Step 6: Test the App

#### Demo Credentials
Three test employees are pre-loaded:

| Username | Password | Name | Employee ID |
|----------|----------|------|-------------|
| uttam01 | 123456 | Uttam Singh | EMP001 |
| john.doe | password | John Doe | EMP002 |
| jane.smith | password | Jane Smith | EMP003 |

#### Testing Flow

1. **Login**: Use `uttam01` / `123456`
2. **Grant Permissions**:
   - When prompted, grant "Allow all the time" for location access
   - This is required for background location tracking
3. **Punch IN**:
   - Tap "Punch IN" button
   - Location tracking will start automatically
4. **View Location Logs**:
   - Wait 15 minutes (or your configured interval)
   - Location logs will appear on the home screen
5. **Punch OUT**:
   - Tap "Punch OUT" when done
   - Location tracking stops automatically

#### Testing Background Tracking

To verify background tracking works:

1. Punch IN
2. Press home button (app goes to background)
3. Wait for the tracking interval
4. Return to app - new location logs should appear
5. Check WorkManager logs: `adb logcat | grep LocationTrackingWorker`

## Location Tracking Details

### How It Works

1. **Punch IN**:
   - Creates attendance record in database
   - Schedules periodic WorkManager task
   - Requests location permissions if needed

2. **Background Tracking**:
   - WorkManager runs every 15 minutes (configurable)
   - Worker checks if attendance session is still active
   - Gets current GPS location using FusedLocationProviderClient
   - Saves location to Room database
   - Optionally syncs to server (when API is integrated)

3. **Punch OUT**:
   - Updates attendance record with end time
   - Cancels WorkManager periodic task
   - Stops location tracking

4. **Device Reboot**:
   - WorkManager automatically reschedules tasks
   - `AttendanceApplication.onCreate()` checks for active session
   - Resumes tracking if employee is still punched IN

### Battery Optimization

- Uses WorkManager's battery-friendly scheduling
- Minimum 15-minute intervals (Android constraint)
- High-accuracy GPS only during location capture
- No continuous location tracking
- Respects Android Doze mode and App Standby

### Google Play Policy Compliance

This app follows Google Play's background location guidelines:

1. ✅ Clear user consent before requesting background location
2. ✅ Explanation dialog showing why background access is needed
3. ✅ Tracking only occurs between Punch IN and OUT
4. ✅ Transparent about data usage
5. ✅ Minimal battery impact

**Important**: When submitting to Google Play, you must:
- Fill out the [Location Permissions Declaration Form](https://support.google.com/googleplay/android-developer/answer/9799150)
- Provide video demonstrating location usage
- Justify background location necessity

## API Integration Guide

The app includes complete Retrofit setup for backend integration. Follow these steps to connect to your API:

### Step 1: Update Base URL

In `app/src/main/java/com/company/attendancetracker/utils/Constants.kt`:

```kotlin
const val API_BASE_URL = "https://your-api-url.com/"  // Replace with your API
```

### Step 2: Create Retrofit Client

Create a new file `app/src/main/java/com/company/attendancetracker/data/remote/RetrofitClient.kt`:

```kotlin
object RetrofitClient {
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(Constants.API_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(Constants.API_BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
```

### Step 3: Integrate in Repositories

Example for `AuthRepository.kt`:

```kotlin
suspend fun login(username: String, password: String): Employee? {
    return try {
        // Call API
        val response = RetrofitClient.apiService.login(
            LoginRequest(username, password)
        )

        if (response.isSuccessful && response.body()?.success == true) {
            val employee = response.body()?.employee
            val token = response.body()?.token

            // Save token
            sessionManager.saveAuthToken(token ?: "")

            // Save employee locally
            if (employee != null) {
                employeeDao.insert(employee)
            }

            employee
        } else {
            null
        }
    } catch (e: Exception) {
        // Fall back to local authentication
        employeeDao.authenticate(username, password)
    }
}
```

### Step 4: Implement Token Authentication

Add interceptor for auth token:

```kotlin
private val authInterceptor = Interceptor { chain ->
    val token = runBlocking { sessionManager.getAuthToken() }
    val request = chain.request().newBuilder()
        .addHeader("Authorization", "Bearer $token")
        .build()
    chain.proceed(request)
}
```

### API Endpoints Reference

See `ApiService.kt` for complete API specification:
- `POST /auth/login` - User authentication
- `POST /attendance/punch-in` - Start work shift
- `POST /attendance/punch-out` - End work shift
- `POST /locations/upload` - Upload location log
- `POST /locations/upload-batch` - Batch upload locations
- `GET /attendance/employee/{id}` - Get attendance history
- `GET /locations/attendance/{id}` - Get location logs

## Security Considerations

### Current Implementation (Demo)

⚠️ **WARNING**: This demo app has security limitations:

1. **Plain Text Passwords**: Passwords stored in plain text in database
2. **No Encryption**: Database not encrypted
3. **Local Authentication**: Auth happens locally, not on server
4. **No Token Validation**: No JWT/OAuth implementation

### Production Recommendations

1. **Authentication**:
   - Implement server-side authentication
   - Use JWT or OAuth 2.0 tokens
   - Store tokens in `EncryptedSharedPreferences`
   - Never store passwords locally

2. **Database Security**:
   - Use SQLCipher to encrypt Room database
   - Implement secure key management
   - Use Android Keystore for sensitive data

3. **Network Security**:
   - Enforce HTTPS only (certificate pinning)
   - Validate SSL certificates
   - Implement request signing
   - Add rate limiting

4. **Location Privacy**:
   - Encrypt location data at rest
   - Use HTTPS for location uploads
   - Implement data retention policies
   - Allow users to view/delete their location data

5. **Code Obfuscation**:
   - Enable ProGuard/R8 in release builds
   - Obfuscate API keys and endpoints
   - Use NDK for sensitive operations

## Troubleshooting

### Location Not Tracking

1. **Check Permissions**:
   ```bash
   adb shell dumpsys package com.company.attendancetracker | grep permission
   ```
   Verify `ACCESS_FINE_LOCATION` and `ACCESS_BACKGROUND_LOCATION` are granted

2. **Check WorkManager**:
   ```bash
   adb shell dumpsys jobscheduler | grep LocationTrackingWorker
   ```
   Should show scheduled periodic work

3. **Check GPS**:
   - Ensure device has GPS enabled
   - Test outdoors or near window for better signal
   - Check Location Mode is set to "High accuracy"

4. **Check Battery Optimization**:
   - Go to Settings → Apps → Attendance Tracker → Battery
   - Set to "Unrestricted" or "Optimized" (not "Restricted")

### WorkManager Not Running

1. **Clear App Data**:
   ```bash
   adb shell pm clear com.company.attendancetracker
   ```

2. **Check WorkManager Logs**:
   ```bash
   adb logcat | grep WM-
   ```

3. **Verify Work Request**:
   ```bash
   adb shell dumpsys activity service WorkManagerService
   ```

### Build Errors

1. **Gradle Sync Failed**:
   - Update Android Studio to latest version
   - File → Invalidate Caches / Restart
   - Delete `.gradle` folder and sync again

2. **Dependency Conflicts**:
   - Check `build.gradle.kts` for version mismatches
   - Update to latest stable versions

3. **Room Schema Export**:
   - Create directory: `app/schemas/`
   - Add to `.gitignore` if needed

## Performance Optimization

### Database

- Indexes on frequently queried columns (`employeeId`, `date`, `isActive`)
- Efficient queries with Flow for reactive updates
- Foreign key constraints with CASCADE delete
- Transaction support for batch operations

### UI

- Jetpack Compose with efficient recomposition
- State hoisting for better performance
- LazyColumn for scrollable lists
- Remember and derivedStateOf for computed values

### Background Work

- WorkManager for battery-efficient scheduling
- Minimum execution guarantees
- Automatic retry with exponential backoff
- Constraint-based execution (battery not low)

## Testing

### Unit Tests

Add to `app/src/test/java/`:

```kotlin
class AuthRepositoryTest {
    @Test
    fun testLogin() = runTest {
        // Test authentication logic
    }
}
```

### Instrumented Tests

Add to `app/src/androidTest/java/`:

```kotlin
@RunWith(AndroidJUnit4::class)
class LocationTrackingTest {
    @Test
    fun testLocationCapture() {
        // Test location tracking
    }
}
```

### Manual Testing Checklist

- [ ] Login with valid credentials
- [ ] Login with invalid credentials
- [ ] Punch IN creates attendance record
- [ ] Location permissions requested properly
- [ ] Background location permission flow
- [ ] Location logs appear after interval
- [ ] Punch OUT stops tracking
- [ ] WorkManager survives app restart
- [ ] Tracking resumes after device reboot
- [ ] Logout clears session
- [ ] Multiple employees can use the app

## Future Enhancements

### Planned Features

1. **Offline Sync**: Queue operations when offline, sync when connected
2. **Geofencing**: Alert if employee leaves designated work area
3. **Face Recognition**: Add face verification at punch IN/OUT
4. **Reports**: Generate attendance and movement reports
5. **Push Notifications**: Remind employees to punch IN/OUT
6. **Multi-language**: Localization support
7. **Dark Mode**: Theme customization
8. **Export Data**: CSV/PDF export of attendance records
9. **Admin Panel**: Separate admin app for management
10. **Real-time Tracking**: View employee locations on map (for admins)

### Backend Requirements

To fully utilize this app, you'll need a backend with:

1. User authentication (JWT/OAuth)
2. Employee management APIs
3. Attendance record storage
4. Location log storage and retrieval
5. Real-time updates (WebSocket/FCM)
6. Admin dashboard
7. Reports generation
8. Data analytics

## License

This project is created for educational purposes. Modify as needed for your use case.

## Support

For issues or questions:
1. Check this README thoroughly
2. Review code comments (extensively documented)
3. Check Android documentation for Jetpack components
4. Review WorkManager documentation for background tasks

## Credits

Built with ❤️ using:
- Kotlin
- Jetpack Compose
- Room Database
- WorkManager
- Google Play Services Location
- Material Design 3

---

**Made for**: Uttam Singh (uttam01)
**Date**: 2024
**Version**: 1.0.0
