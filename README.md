# Sound Timer

A modern Android app that silences your phone for a set duration and automatically restores sounds when the timer ends.

## Features

- ⏱️ **Custom Timer** - Set hours and minutes for how long to silence your phone
- 🔊 **Sound Category Control** - Independently mute/unmute:
  - Ringer (phone calls)
  - Notifications  
  - Media (music, videos)
  - Alarm
- ⚡ **Quick Presets** - One-tap buttons for 15min, 30min, 1hr, 2hr
- 🔔 **Notifications** - Get notified when sounds are restored
- 💾 **Persistence** - Timer survives app closure and device restarts
- 🌙 **Dark Mode** - Automatic dark/light theme based on system settings
- ➕ **Extend Timer** - Add 15 minutes while timer is running

## Screenshots

The app features:
1. **Onboarding Screen** - Guides users through granting Do Not Disturb access
2. **Main Timer Screen** - Clean interface with time picker, sound toggles, and countdown

## Building the App

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17
- Android SDK 34

### Build Steps

1. Open the project in Android Studio
2. Sync Gradle files
3. Build the project: `Build > Make Project`
4. Run on device/emulator: `Run > Run 'app'`

### Command Line Build

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease
```

The APK will be generated at:
- Debug: `app/build/outputs/apk/debug/app-debug.apk`
- Release: `app/build/outputs/apk/release/app-release.apk`

## Required Permissions

| Permission | Purpose |
|------------|---------|
| `ACCESS_NOTIFICATION_POLICY` | Control Do Not Disturb / volume levels |
| `POST_NOTIFICATIONS` | Show timer and completion notifications |
| `RECEIVE_BOOT_COMPLETED` | Restore timer after device restart |
| `SCHEDULE_EXACT_ALARM` | Precise timer completion |
| `FOREGROUND_SERVICE` | Keep timer running in background |

## Project Structure

```
app/src/main/java/com/soundtimer/
├── MainActivity.kt              # Entry point, permission handling
├── data/
│   ├── TimerModels.kt          # Data classes (TimerState, VolumeState)
│   └── PreferencesManager.kt   # SharedPreferences persistence
├── service/
│   ├── TimerService.kt         # Foreground service for countdown
│   ├── BootReceiver.kt         # Restore timer on device boot
│   └── AlarmReceiver.kt        # Handle timer completion alarm
├── util/
│   ├── VolumeManager.kt        # AudioManager wrapper
│   ├── AlarmHelper.kt          # AlarmManager utilities
│   └── NotificationHelper.kt   # Notification management
└── ui/
    ├── theme/
    │   ├── Color.kt
    │   ├── Theme.kt
    │   └── Type.kt
    ├── components/
    │   └── Components.kt       # Reusable UI components
    └── screens/
        ├── OnboardingScreen.kt # Permission setup
        └── TimerScreen.kt      # Main timer interface
```

## How It Works

1. **Starting Timer**
   - User sets duration and selects sound categories
   - App saves current volume levels
   - Mutes selected categories
   - Starts foreground service with countdown notification
   - Schedules exact alarm for timer end

2. **During Timer**
   - Foreground service updates notification every second
   - UI shows real-time countdown
   - User can extend or stop timer

3. **Timer Completion**
   - Alarm triggers volume restoration
   - Shows completion notification
   - Clears timer state

4. **Persistence**
   - Timer state saved to SharedPreferences
   - BootReceiver restores timer after device restart
   - Original volume levels preserved for exact restoration

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose with Material 3
- **Architecture**: Service-based with SharedPreferences
- **Minimum SDK**: 26 (Android 8.0)
- **Target SDK**: 34 (Android 14)

## License

MIT License
