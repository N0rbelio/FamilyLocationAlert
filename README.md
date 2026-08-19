# FamilyLocationAlert

An Android application for location monitoring, geofencing, and configurable
family alerts.

FamilyLocationAlert allows you to define geographical zones, associate
contacts with those zones, and receive notifications or SMS alerts when the
device enters or leaves a configured location.

> ⚠️ **Development status:** FamilyLocationAlert is currently under active
> development. Version `0.0.8` is a development release and should not yet be
> considered production-ready.

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

## How it works

The application continuously monitors the device's location while monitoring
is enabled.

When the device enters or leaves a configured zone, FamilyLocationAlert
generates a location event:

    Location
        │
        ▼
    LocationForegroundService
        │
        ▼
    LocationChecker
        │
        ▼
    LocationEventProcessor
        │
        ├──► Local notification
        │
        └──► SMS alert
                  │
                  ▼
             Associated contacts

Each location can have its own radius and associated contacts.

## Location Monitoring

To reduce battery consumption, the application uses different update
intervals depending on the device's current state.

| State | Production interval |
|---|---:|
| Outside a zone | 1 minute |
| Just entered | 15 minutes |
| Inside a zone | 1 hour |
| Inside for a long period | 3 hours |

During development, shorter intervals are available through `AppConfig` to
make testing easier.

## Technology

- **Language:** Kotlin
- **UI:** Jetpack Compose
- **Architecture:** Android ViewModel + Service-based location monitoring
- **Database:** Room
- **Location:** Google Play Services Fused Location Provider
- **Notifications:** Android Notification API
- **SMS:** Android `SmsManager`
- **Build system:** Gradle
- **Minimum Android version:** API 27
- **Target SDK:** API 37

## Project structure

```text
app/
└── src/main/java/com/n0rbelio/familylocationalert/
    ├── config/
    ├── data/
    ├── service/
    ├── ui/
    └── MainActivity.kt
