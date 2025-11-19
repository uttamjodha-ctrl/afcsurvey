# 🎓 COMPLETE BEGINNER'S GUIDE TO RUNNING THE APP

## ⏰ TIME NEEDED
- **First time setup**: 45-60 minutes
- **Next time**: 5 minutes

## 📋 BEFORE YOU START

✅ **Check you have:**
- [ ] Computer with at least 8GB RAM (16GB better)
- [ ] 20GB free disk space
- [ ] Internet connection (for downloading tools)
- [ ] Android Studio installed (or installing now)

---

## 🚀 STEP-BY-STEP GUIDE

### PHASE 1: INSTALL ANDROID STUDIO ⏳ (30-45 minutes)

#### Step 1.1: Download Android Studio

1. **Open your web browser** (Chrome, Firefox, etc.)
2. **Go to**: https://developer.android.com/studio
3. **Click** the big green button: **"Download Android Studio"**
4. **Accept** the terms and conditions
5. **Click Download** - file is about 1-2 GB (large!)
6. **Wait** for download to complete

#### Step 1.2: Install Android Studio

**ON WINDOWS:**
1. Find the downloaded file (usually in "Downloads" folder)
2. **Double-click** the `.exe` file (like `android-studio-2023.x.x.x-windows.exe`)
3. Click **"Next"** → **"Next"** → **"Install"**
4. Wait for installation (5-10 minutes)
5. Click **"Finish"**
6. Android Studio will open

**ON MAC:**
1. Find the downloaded `.dmg` file in Downloads
2. **Double-click** to open it
3. **Drag** the Android Studio icon to the Applications folder
4. Open **Applications** folder
5. **Double-click** Android Studio
6. If you see "App from unidentified developer", right-click → Open → Open

**ON LINUX:**
1. Extract the downloaded `.tar.gz` file
2. Open terminal in the extracted folder
3. Run: `./bin/studio.sh`

#### Step 1.3: First Time Setup Wizard (VERY IMPORTANT!)

When Android Studio opens for the **first time**:

```
[Screen 1: Welcome]
→ Click "Next"

[Screen 2: Install Type]
→ Select "Standard" (DO NOT choose Custom unless you know what you're doing)
→ Click "Next"

[Screen 3: Select UI Theme]
→ Choose "Light" or "Darcula" (dark theme) - your preference!
→ Click "Next"

[Screen 4: Verify Settings]
→ Just click "Next" (don't change anything)

[Screen 5: License Agreement]
→ Click each item in the list
→ Click "Accept" for each one
→ Click "Finish"

[Screen 6: Downloading Components]
⏰ NOW WAIT 20-40 MINUTES ⏰
→ You'll see it downloading:
   - Android SDK
   - Android SDK Platform
   - Android Emulator
   - Other tools
→ This is NORMAL! Go get coffee ☕
→ DO NOT CLOSE the window!
→ Internet must stay connected
```

**✅ You're done when you see:**
- A window titled "Welcome to Android Studio"
- Buttons: "New Project", "Open", "Get from VCS"

---

### PHASE 2: OPEN THE PROJECT ⏳ (5 minutes)

#### Step 2.1: Locate the Project Folder

The project is located at:
```
/home/user/afcsurvey/android-attendance-app
```

**IMPORTANT:** The folder you need to open is called **`android-attendance-app`**

Inside it, you should see:
- `app` folder
- `build.gradle.kts` file
- `settings.gradle.kts` file
- `README.md` file
- Other files and folders

#### Step 2.2: Open in Android Studio

1. **In Android Studio**, click: **File** → **Open**

   OR on the Welcome screen, click: **"Open"**

2. **Navigate** to the project:
   - Find the folder: `/home/user/afcsurvey/android-attendance-app`
   - **MAKE SURE** you select the `android-attendance-app` folder (not the parent folder!)

3. **Click** the `android-attendance-app` folder to select it

4. **Click** "OK" or "Open"

5. **WAIT** - Android Studio will:
   - Load the project (30 seconds - 2 minutes)
   - You might see a popup asking "Trust Project" → Click **"Trust Project"**
   - Start "Gradle Sync" automatically (see next step)

#### Step 2.3: Gradle Sync (CRITICAL STEP!)

**What is Gradle?** It's a tool that downloads all the libraries your app needs. Like downloading ingredients before cooking.

**What you'll see:**

1. A notification banner at the top saying:
   ```
   "Gradle project sync in progress..."
   ```

2. At the bottom of the screen, a progress bar:
   ```
   "Syncing... 5%... 10%... 50%..."
   ```

3. **FIRST TIME: This takes 10-20 minutes!**
   - It's downloading libraries (about 500MB-1GB)
   - Be patient! This is normal!
   - Your internet must stay connected
   - You'll see files downloading in the bottom panel

**✅ You're done when:**
- The progress bar disappears
- Bottom bar shows: **"Gradle sync finished"** or **"BUILD SUCCESSFUL"**
- No error messages in red

**❌ If you see errors:**
- Look for a banner saying "Gradle Sync Failed"
- Click "Sync Now" or "Retry"
- If still failing, skip to "Troubleshooting" section below

---

### PHASE 3: SET UP A DEVICE ⏳ (10 minutes)

You need an Android device to run the app. Two options:

#### OPTION A: Use Your Physical Android Phone (RECOMMENDED - EASIEST)

**Step 3A.1: Enable Developer Options on Phone**

1. **On your Android phone**, go to: **Settings** → **About Phone**
2. Find **"Build Number"** (might be under "Software Information")
3. **Tap "Build Number" 7 times rapidly**
4. You'll see: "You are now a developer!"

**Step 3A.2: Enable USB Debugging**

1. Go back to main **Settings**
2. Find **"Developer Options"** (or "Developer Settings")
   - Usually under "System" → "Advanced"
3. Turn ON: **"USB Debugging"**
4. Confirm "OK" when prompted

**Step 3A.3: Connect Phone to Computer**

1. **Connect** your phone to computer with USB cable
2. **On your phone**, you'll see a popup: "Allow USB debugging?"
3. Check "Always allow from this computer"
4. Tap **"OK"**

**Step 3A.4: Verify Connection**

In Android Studio, look at the top toolbar:
- You should see a dropdown showing your phone's name
- Example: "Samsung Galaxy S21" or "Pixel 6"

✅ **If you see your phone name, you're ready!**

❌ **If you don't see your phone:**
- Try a different USB cable
- Try a different USB port
- Install phone drivers (Google your phone model + "USB drivers")

---

#### OPTION B: Use Android Emulator (Virtual Phone on Computer)

**Step 3B.1: Open Device Manager**

1. In Android Studio, click: **Tools** → **Device Manager**

   OR look for an icon in the right sidebar that looks like a phone

2. You'll see "Device Manager" panel open

**Step 3B.2: Create Virtual Device**

1. Click **"Create Device"** or the **"+"** button

2. **Select Hardware:**
   ```
   Category: Phone
   Device: Pixel 6 (or any phone you like)
   → Click "Next"
   ```

3. **Select System Image:**
   ```
   Release Name: Tiramisu (API 33) or UpsideDownCake (API 34)
   → If you see "Download" next to it, click it and wait (5-10 min)
   → After download, select it
   → Click "Next"
   ```

4. **Verify Configuration:**
   ```
   AVD Name: Pixel_6_API_33 (or whatever)
   → Click "Finish"
   ```

**Step 3B.3: Start Emulator**

1. In Device Manager, find your newly created device
2. Click the **▶️ Play button** next to it
3. **WAIT 2-5 minutes** - emulator is starting
4. You'll see an Android phone appear on screen
5. Wait until you see the Android home screen

✅ **You're ready when the emulator shows the Android home screen!**

---

### PHASE 4: RUN THE APP! 🎉 ⏳ (2 minutes)

#### Step 4.1: Select Your Device

At the top of Android Studio, find the toolbar with:
```
[Device Dropdown] ▼   [app] ▼   ▶️
```

1. Click the **Device Dropdown**
2. Select your device:
   - Physical phone: "Samsung Galaxy..." or "Pixel..."
   - Emulator: "Pixel_6_API_33" or similar

#### Step 4.2: Click RUN!

1. Click the big green **▶️ Play button** (called "Run")

   OR press: **Shift + F10** (Windows/Linux) or **Control + R** (Mac)

2. **WAIT** - First run takes 2-5 minutes:
   ```
   Bottom panel shows:
   "Building..." → "Installing..." → "Launching..."
   ```

3. Watch your phone/emulator:
   - App will install
   - App will automatically open
   - You'll see the LOGIN SCREEN! 🎉

---

### PHASE 5: TEST THE APP! 🧪 ⏳ (5 minutes)

#### Step 5.1: Login

On the app screen, you'll see:
- **Username** field
- **Password** field
- A card showing demo credentials

**Type:**
```
Username: uttam01
Password: 123456
```

Click **"LOGIN"**

✅ **You should now see the HOME screen!**

#### Step 5.2: Grant Location Permission (IMPORTANT!)

When you tap "Punch IN", you'll see permission dialogs:

**Dialog 1: "Allow Attendance Tracker to access this device's location?"**
→ Choose: **"While using the app"**

**Dialog 2: "Allow all the time?"**
→ Choose: **"Allow all the time"** (required for background tracking!)

#### Step 5.3: Punch IN

1. Tap the blue **"Punch IN"** button
2. You'll see: "Punched IN successfully"
3. Button changes to red **"Punch OUT"**

#### Step 5.4: Wait for Location Tracking

**The app tracks location every 15 minutes.**

To test faster:
1. Keep the app open
2. If using emulator:
   - Click the **"..."** (more) button on emulator sidebar
   - Click **"Location"**
   - Set a GPS coordinate
3. If using phone:
   - Go outside or near a window
   - Walk around a bit

**After 15 minutes**, you'll see location logs appear:
- Time
- Latitude/Longitude
- Accuracy

#### Step 5.5: Punch OUT

When done testing:
1. Tap the red **"Punch OUT"** button
2. Location tracking stops
3. You can log out using the logout icon (top right)

---

## 🎉 CONGRATULATIONS!

You've successfully:
✅ Installed Android Studio
✅ Opened the project
✅ Ran the app
✅ Tested attendance tracking

---

## 🆘 TROUBLESHOOTING

### Problem 1: "Gradle Sync Failed"

**Solution:**
1. Click: **File** → **Invalidate Caches** → **Invalidate and Restart**
2. Wait for restart
3. Click: **File** → **Sync Project with Gradle Files**

**Still failing?**
1. Check internet connection
2. Try again tomorrow (sometimes Gradle servers are down)
3. Open: **View** → **Tool Windows** → **Build** to see error details

---

### Problem 2: "Cannot find device" / "No connected devices"

**For Physical Phone:**
- Disconnect and reconnect USB cable
- Try different USB port
- Restart phone
- Restart computer
- Install USB drivers for your phone

**For Emulator:**
- Close emulator and start again
- Delete and recreate virtual device
- Check: **Tools** → **SDK Manager** → **SDK Tools** → Ensure "Android Emulator" is installed

---

### Problem 3: "App crashes immediately"

**Solution:**
1. Check if you granted location permissions
2. Try uninstalling and running again
3. Check phone/emulator has GPS enabled
4. Look at **Logcat** (bottom panel) for error messages

---

### Problem 4: "Location not tracking"

**Checklist:**
- [ ] Did you grant "Allow all the time" permission?
- [ ] Did you Punch IN?
- [ ] Did you wait 15 minutes?
- [ ] Is GPS/Location enabled on phone/emulator?
- [ ] If emulator, did you set a GPS location?

**For Emulator:**
1. Click **"..."** on emulator toolbar
2. Click **"Location"**
3. Enter coordinates (e.g., 37.4220, -122.0841)
4. Click "Send"

---

### Problem 5: "Build errors" with red text

**Common fixes:**
1. **File** → **Sync Project with Gradle Files**
2. **Build** → **Clean Project**
3. **Build** → **Rebuild Project**
4. Restart Android Studio

---

## 📞 NEED MORE HELP?

### Check Logs
1. Open: **View** → **Tool Windows** → **Logcat**
2. Look for errors in red
3. Filter by "AttendanceTracker" to see app logs

### Useful Commands (in Android Studio Terminal)
```bash
# See connected devices
adb devices

# Uninstall app
adb uninstall com.company.attendancetracker

# View app logs
adb logcat | grep AttendanceTracker
```

---

## 🎓 LEARNING RESOURCES

**Understanding the Code:**
- Read `README.md` in the project folder (comprehensive guide)
- Each file has detailed comments explaining what it does

**Android Development Basics:**
- Official Android Codelabs: https://developer.android.com/courses
- Kotlin Basics: https://kotlinlang.org/docs/kotlin-tour-welcome.html

**Next Steps:**
1. Try logging in with different users (john.doe / password)
2. Explore the code files
3. Try changing the tracking interval in `Constants.kt`
4. Read about MVVM architecture
5. Learn about Jetpack Compose

---

## ⚡ QUICK REFERENCE CARD

### Every Time You Want to Run the App:

1. Open Android Studio
2. Open project: **File** → **Open** → Select `android-attendance-app`
3. Wait for Gradle sync (green progress bar at bottom)
4. Select device (dropdown at top)
5. Click green ▶️ Run button
6. Wait 30-60 seconds
7. App opens on your device!

### Demo Login:
```
Username: uttam01
Password: 123456
```

### Other Demo Accounts:
```
john.doe / password
jane.smith / password
```

### Important Files to Know:
- `Constants.kt` - Change settings here
- `MainActivity.kt` - App entry point
- `LoginScreen.kt` - Login UI
- `HomeScreen.kt` - Main screen UI

---

## 📊 VISUAL CHECKLIST

```
PHASE 1: INSTALL ANDROID STUDIO
├── ✅ Download Android Studio
├── ✅ Run installer
├── ✅ Complete setup wizard
└── ✅ See "Welcome" screen

PHASE 2: OPEN PROJECT
├── ✅ Click "Open" in Android Studio
├── ✅ Select android-attendance-app folder
├── ✅ Wait for Gradle sync
└── ✅ See project files in left panel

PHASE 3: SETUP DEVICE
├── ✅ Option A: Connect phone + USB debugging
└── ✅ Option B: Create emulator

PHASE 4: RUN APP
├── ✅ Select device from dropdown
├── ✅ Click green Play button
└── ✅ See app on device

PHASE 5: TEST
├── ✅ Login (uttam01 / 123456)
├── ✅ Grant location permissions
├── ✅ Punch IN
├── ✅ Wait for location logs
└── ✅ Punch OUT
```

---

**Good luck! You've got this! 🚀**

If you get stuck on any step, re-read that section carefully. Most issues are solved by:
1. Waiting longer (Gradle sync takes time!)
2. Restarting Android Studio
3. Checking internet connection
4. Granting all permissions
