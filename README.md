# FamilyLocationAlert

**FamilyLocationAlert** is an Android application designed to monitor a device's location and automatically detect when it enters or leaves predefined locations.

The application can notify the user through Android notifications and, optionally, send SMS alerts to configured contacts.

> ⚠️ **Development status:** The project is currently under active development. APIs, UI, database structures and features may change between versions.

---

## Features

- 📍 Location monitoring using Android's Fused Location Provider
- 🗺️ Configurable geographical zones
- 📏 Custom radius for each zone
- 🚪 Entry and exit detection
- 🔔 Local notifications for location events
- 📱 SMS alerts through the device's SIM card
- 👥 Contact management
- 🔗 Assign contacts to individual locations
- ⚙️ Configurable SMS alert modes:
  - Entry only
  - Exit only
  - Entry and exit
- 🔋 Adaptive location update intervals
- 📦 Persistent storage using Room
- 🔄 Background monitoring through an Android Foreground Service
- 🧪 Development/test mode for faster location monitoring during testing

---

## How It Works

### 📍 Location Monitoring

- Continuous location monitoring using Android's Fused Location Provider.
- Foreground Service for reliable background location monitoring.
- Detection of entering and leaving predefined locations.
- Configurable location radius.
- Multiple locations can be configured.
- Location monitoring continues while the application is minimized.

### 🔋 Adaptive Location Updates

The application dynamically adjusts the location update frequency depending on the current state:

| State | Production interval |
|---|---:|
| Outside a zone | 1 minute |
| Just entered a zone | 15 minutes |
| Inside a zone | 1 hour |
| Inside for a long period | 3 hours |

During development and testing, shorter intervals can be enabled through the application configuration.

This approach is intended to reduce battery consumption while maintaining faster detection when the device is outside configured locations.

### 🔔 Notifications

The application generates Android notifications when:

- A configured location is entered.
- A configured location is exited.

### 📱 SMS Alerts

SMS alerts can be configured per location.

Supported modes include:

- Entry only
- Exit only
- Entry and exit

SMS messages are sent directly through the device's SIM/mobile network using Android's SMS functionality.

Example:

```text
Entrou em Casa
```

or:

```text
Saiu de Casa
```

### 👥 Contacts

Contacts can be created and managed inside the application.

A location can have multiple associated contacts.

Contacts are stored locally and can be selected when configuring a location.

### 🗺️ Location Management

Users can:

- Create locations.
- Edit locations.
- Delete locations.
- Configure coordinates.
- Configure location radius.
- Associate contacts with locations.
- Configure SMS alert behavior.

### 🧪 Development / Test Mode

The application includes a development test mode that allows location monitoring intervals to be reduced for testing.

Production:

```text
Outside:       1 minute
Just entered:  15 minutes
Inside:        1 hour
Long inside:   3 hours
```

Test mode:

```text
Outside:       10 seconds
Just entered:  20 seconds
Inside:        30 seconds
Long inside:   1 minute
```

This allows location monitoring behaviour to be tested without waiting for the production intervals.

---

## Architecture

FamilyLocationAlert is built using modern Android technologies.

### Main technologies

- Kotlin
- Jetpack Compose
- Android SDK
- AndroidX
- Room
- Kotlin Coroutines
- ViewModel
- Fused Location Provider
- Foreground Services
- Android Notifications
- Android SMS APIs

### Project structure

```text
app/
└── src/
    └── main/
        ├── java/
        │   └── com.n0rbelio.familylocationalert/
        │       ├── config/
        │       ├── data/
        │       ├── service/
        │       ├── ui/
        │       └── MainActivity.kt
        │
        ├── res/
        └── AndroidManifest.xml
```

### Data layer

The application uses **Room** for local persistence.

The database currently contains entities and relationships for:

- Locations
- Contacts
- Location/contact relationships
- SMS alert configuration

The relationship between locations and contacts is represented using a Room many-to-many relationship.

```text
Location
   │
   │
   ├── LocationContactCrossRef
   │
   │
Contact
```

---

## Location Monitoring

Location monitoring is handled by a foreground service.

The main monitoring flow is:

```text
Fused Location Provider
        │
        ▼
LocationForegroundService
        │
        ▼
LocationChecker
        │
        ▼
LocationStatus
        │
        ▼
LocationEventProcessor
        │
        ├── Notification
        │
        └── SMS
```

The application determines whether the device is inside or outside each configured location.

When a state transition is detected, an event is generated:

```text
NONE
ENTERED
EXITED
```

The event processor then handles the appropriate notification and SMS behaviour.

---

## Permissions

The application may request the following Android permissions:

### Location

```text
ACCESS_FINE_LOCATION
ACCESS_COARSE_LOCATION
ACCESS_BACKGROUND_LOCATION
```

Background location access is required for the application's core functionality because location monitoring is intended to continue while the application is not actively being used.

### SMS

```text
SEND_SMS
```

This permission is used to send configured location alerts through the device's mobile network.

### Notifications

```text
POST_NOTIFICATIONS
```

Required on supported Android versions for displaying application notifications.

### Foreground Service

```text
FOREGROUND_SERVICE
FOREGROUND_SERVICE_LOCATION
```

Required for the foreground location monitoring service on supported Android versions.

---

## Android Compatibility

The application targets modern Android versions and uses Android's foreground-service and runtime permission requirements.

Some permissions, especially background location and SMS access, are subject to Android and Google Play policies.

If distributing the application through Google Play, the application's use of these permissions must comply with the applicable Google Play policies.

---

## Installation

### Requirements

- Android Studio
- Android SDK
- JDK compatible with the project's Gradle/Android configuration
- Android device or emulator

For SMS testing, a physical Android device with SMS capability and an active SIM/mobile network is recommended.

### Clone the repository

```bash
git clone https://github.com/N0rbelio/FamilyLocationAlert.git
cd FamilyLocationAlert
```

Open the project in Android Studio and allow Gradle to synchronize the project.

Then build and run the application on a compatible Android device.

---

## Configuration

Application configuration is located under:

```text
app/src/main/java/com/n0rbelio/familylocationalert/config/
```

Development and testing values can be configured through `AppConfig`.

Example:

```kotlin
const val TEST_MODE = true
```

When test mode is enabled, shorter location update intervals are used.

Before production use, test mode should be disabled:

```kotlin
const val TEST_MODE = false
```

---

## SMS Configuration

SMS alerts are configured per location.

Each location can define whether SMS alerts should be sent for:

```text
ENTRY_ONLY
EXIT_ONLY
BOTH
```

Contacts associated with the location receive the corresponding alert.

The application sends SMS directly through Android's `SmsManager`/device SMS functionality rather than using an external HTTP SMS provider.

This means:

- No external SMS API is required.
- No external SMS service account is required.
- The device must have SMS capability.
- The SIM/mobile operator handles the actual SMS transmission.
- Normal mobile-network SMS charges may apply.

---

## Privacy

FamilyLocationAlert is designed around local device storage.

Location data, configured locations and contacts are stored locally on the Android device through Room.

The project does not require a remote server for its core location monitoring functionality.

The application may access:

- Device location
- Configured contacts
- Phone numbers configured for SMS alerts
- Mobile network/SMS functionality

These permissions are used to provide the application's location monitoring and alert functionality.

Users should review the source code and Android permission prompts before deploying the application.

---

## Security

Do not commit private credentials, signing keys, API keys or other sensitive information to the repository.

The repository's `.gitignore` excludes common sensitive files such as:

```text
*.jks
*.keystore
*.p12
.env
.env.*
secrets.properties
local.properties
```

If you introduce a new secret or credential into the project, add the corresponding file or pattern to `.gitignore` before committing it.

---

## Development

The project is developed incrementally through versioned branches.

Current development branch:

```text
version-0.0.8
```

The project uses versioned releases to separate major development stages.

Previous versions include:

```text
v0.0.1
v0.0.2
v0.0.3
v0.0.4
v0.0.5
v0.0.6
v0.0.7
v0.0.8
```

---

## Roadmap

Planned improvements include:

- Further battery optimization.
- Improved location monitoring reliability.
- Improved background execution handling.
- Better SMS delivery handling.
- Improved contact management.
- Improved location management UI.
- Improved error handling.
- Codebase cleanup and refactoring.
- Improved testing.
- Additional configuration options.
- Improved Android version compatibility.
- Better documentation.

The codebase may also undergo structural cleanup in future versions as features stabilize.

---

## Known Limitations

Because Android aggressively manages background execution and battery usage, behaviour can vary between manufacturers and Android versions.

Some manufacturers apply additional background restrictions that may affect location monitoring.

For reliable background monitoring, users may need to allow the application to run without battery optimization restrictions depending on the device.

SMS functionality also depends on:

- Device hardware
- SIM availability
- Mobile network coverage
- Android permissions
- Carrier restrictions

---

## Pull Requests

Pull Requests may be submitted for bug fixes, improvements or other changes.

All Pull Requests are reviewed at the maintainer's discretion. Submission of a Pull Request does not guarantee that the proposed changes will be accepted or merged.

The maintainer may accept, reject, request changes to, or close a Pull Request without merging it.

For substantial changes, it is recommended to open an issue first to discuss the proposed change.

---

## License

FamilyLocationAlert is licensed under the **GNU General Public License v3.0**.

This means that you are free to:

- Use the software.
- Study the source code.
- Modify the software.
- Redistribute the software.

However, when distributing modified versions of the software, the GPLv3 requires the corresponding source code to remain available under the same license.

See the [`LICENSE`](LICENSE) file for the complete license text.

---

## Third-Party Libraries

FamilyLocationAlert uses open-source libraries and frameworks from the Android ecosystem.

These dependencies remain subject to their respective licenses and terms.

Important dependencies include:

- AndroidX
- Jetpack Compose
- Room
- Kotlin
- Kotlin Coroutines
- Google Play Services Location

Their respective licenses and notices should be reviewed when redistributing the application.

---

## Disclaimer

FamilyLocationAlert is provided as an open-source project for personal and development purposes.

The application deals with location monitoring and automated notifications. It should not be relied upon as the sole mechanism for safety-critical monitoring.

Always verify that the application behaves correctly on the target Android device before relying on it for important notifications.

---

## Repository

GitHub:

https://github.com/N0rbelio/FamilyLocationAlert

---

## Author

Developed by **N0rbelio**.

---

## License Summary

```text
FamilyLocationAlert
Copyright (C) 2026 N0rbelio

Licensed under the GNU General Public License v3.0.

You may use, modify and redistribute this software,
provided that derivative works are distributed under
the same GPLv3 license and the corresponding source
code is made available.
```
