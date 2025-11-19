# Quick Start Guide - Attendance Tracker App

Get the app running in 5 minutes!

## Prerequisites

✅ Android Studio installed
✅ Android device or emulator
✅ JDK 17+

## Setup Steps

### 1. Open in Android Studio

```bash
# Clone or copy the project
cd android-attendance-app

# Open in Android Studio
# File → Open → Select the android-attendance-app folder
```

### 2. Sync Gradle

Click the "Sync Now" banner that appears, or:
- **File** → **Sync Project with Gradle Files**

Wait 5-10 minutes for dependencies to download.

### 3. Run the App

1. Connect your Android device (or start emulator)
2. Click the green ▶️ **Run** button
3. Select your device
4. Wait for installation

### 4. Login

Use demo credentials:
- **Username**: `uttam01`
- **Password**: `123456`

### 5. Test Attendance

1. **Grant Location Permission** when prompted (choose "Allow all the time")
2. **Tap "Punch IN"** button
3. **Wait 15 minutes** - location will be tracked automatically
4. **View location logs** on the home screen
5. **Tap "Punch OUT"** when done

## Troubleshooting

### Gradle Sync Fails
```bash
# In Android Studio:
File → Invalidate Caches / Restart
```

### Location Not Tracking
- Ensure GPS is enabled on device
- Check app has location permission: Settings → Apps → Attendance Tracker → Permissions
- Move device or walk around (GPS needs movement to update)

### WorkManager Not Running
```bash
# Check logs in Android Studio Logcat:
# Filter by "LocationTrackingWorker"
```

## Key Configuration

### Change Tracking Interval

Edit `app/src/main/java/com/company/attendancetracker/utils/Constants.kt`:

```kotlin
// Change from 15 to your desired interval (in minutes)
const val LOCATION_TRACKING_INTERVAL_MINUTES = 15L
```

### Add More Demo Users

Edit `app/src/main/java/com/company/attendancetracker/data/local/AppDatabase.kt`:

```kotlin
// In populateDatabase() function, add:
Employee(
    username = "newuser",
    password = "password",
    name = "New User",
    employeeId = "EMP004",
    department = "Sales",
    phone = "+91-9876543213",
    email = "newuser@company.com"
)
```

## Project Structure Overview

```
app/
├── data/           # Database & Repository
│   ├── local/      # Room entities & DAOs
│   ├── remote/     # API service (for future)
│   └── repository/ # Business logic
├── ui/             # Screens & ViewModels
│   ├── login/      # Login screen
│   ├── home/       # Home dashboard
│   └── theme/      # Material 3 theme
├── workers/        # Background tasks
└── utils/          # Helpers & constants
```

## Demo Accounts

| Username | Password | Name |
|----------|----------|------|
| uttam01 | 123456 | Uttam Singh |
| john.doe | password | John Doe |
| jane.smith | password | Jane Smith |

## Next Steps

1. ✅ **Read** `README.md` for complete documentation
2. ✅ **Customize** Constants.kt for your needs
3. ✅ **Integrate** your backend API (see API Integration section in README)
4. ✅ **Test** on physical device for accurate GPS tracking

## Common Commands

```bash
# View logs
adb logcat | grep AttendanceTracker

# Check location permissions
adb shell dumpsys package com.company.attendancetracker | grep permission

# Clear app data (fresh start)
adb shell pm clear com.company.attendancetracker

# Install APK manually
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Support

Having issues? Check:
1. README.md (comprehensive guide)
2. Code comments (extensively documented)
3. Android Studio Build → Make Project (check for errors)

---

Happy coding! 🚀
