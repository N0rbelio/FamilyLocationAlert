# FamilyLocationAlert

### Android location monitoring and family location alerts

FamilyLocationAlert is an Android application for monitoring a device's location, detecting entry and exit events from configurable geographical zones, and optionally notifying selected contacts through SMS.

> ⚠️ **Development status:** FamilyLocationAlert is currently under active development. Version `0.0.8` is not considered a final release, and application behaviour, UI, database structures and internal architecture may change in future versions.

[![Build](https://github.com/N0rbelio/FamilyLocationAlert/actions/workflows/build.yml/badge.svg)](https://github.com/N0rbelio/FamilyLocationAlert/actions/workflows/build.yml)
[![License](https://img.shields.io/github/license/N0rbelio/FamilyLocationAlert)](LICENSE)

---

## Overview

FamilyLocationAlert allows you to define geographical locations and monitor whether the device enters or leaves those areas.

When a location event occurs, the application can:

- Display a local notification
- Send an SMS alert to selected contacts
- Apply different alert rules depending on the configured location

The application is designed to perform the core monitoring functionality directly on the Android device without requiring a remote backend.

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

The application continuously monitors the device location through Android's location services while the monitoring service is active.

Location updates are processed by the application and compared against the configured geographical zones.

~~~text
                 ┌─────────────────────┐
                 │   Android Device    │
                 └──────────┬──────────┘
                            │
                            ▼
                 ┌─────────────────────┐
                 │ Fused Location      │
                 │ Provider            │
                 └──────────┬──────────┘
                            │
                            ▼
                 ┌─────────────────────┐
                 │ Location Monitoring │
                 │ Foreground Service  │
                 └──────────┬──────────┘
                            │
                            ▼
                 ┌─────────────────────┐
                 │ Zone Detection      │
                 └──────────┬──────────┘
                            │
                    ┌───────┴───────┐
                    ▼               ▼
               ┌─────────┐     ┌─────────┐
               │ Entered │     │ Exited  │
               └────┬────┘     └────┬────┘
                    │               │
                    └───────┬───────┘
                            ▼
                 ┌─────────────────────┐
                 │ Event Processor     │
                 └──────────┬──────────┘
                            │
                  ┌─────────┴─────────┐
                  ▼                   ▼
          ┌──────────────┐     ┌──────────────┐
          │ Notification │     │ SMS Alert    │
          └──────────────┘     └──────────────┘
~~~

---

## Zones

Each location can be configured with:

| Property | Description |
|---|---|
| Name | Name of the location |
| Latitude | Geographic latitude |
| Longitude | Geographic longitude |
| Radius | Detection radius in meters |
| Contacts | Contacts associated with the location |
| SMS mode | Entry, exit, or both |

Example:

~~~text
Home
├── Radius: 150 m
├── Entry: enabled
├── Exit: enabled
└── Contacts:
    ├── Contact 1
    └── Contact 2
~~~

---

## SMS Alerts

SMS messages are sent directly through the device's SIM card using Android's SMS functionality.

No external SMS API is required for sending alerts.

Available alert modes:

| Mode | Entry SMS | Exit SMS |
|---|---:|---:|
| Entry only | ✓ | |
| Exit only | | ✓ |
| Entry & Exit | ✓ | ✓ |

Example entry notification:

~~~text
Entrou em Casa
~~~

Example exit notification:

~~~text
Saiu de Casa
~~~

SMS alerts are only sent to contacts associated with the specific location that generated the event.

---

## Adaptive Location Monitoring

The application dynamically adjusts location update intervals depending on the current monitoring state.

### Production intervals

| State | Update interval |
|---|---:|
| Outside all zones | 1 minute |
| Just entered a zone | 15 minutes |
| Inside a zone | 1 hour |
| Inside for an extended period | 3 hours |

When the device exits a zone, monitoring returns to the more frequent outside-zone interval.

This approach is intended to reduce battery consumption while maintaining useful location monitoring.

### Development/Test Mode

During development, shorter intervals can be enabled to make testing easier.

Current test intervals:

| State | Test interval |
|---|---:|
| Outside all zones | 10 seconds |
| Just entered | 20 seconds |
| Inside | 30 seconds |
| Long inside | 1 minute |

The test configuration is intended for development and testing and is not the final production configuration.

---

## Local Notifications

Location events generate local Android notifications.

For example:

~~~text
FamilyLocationAlert

Entrou em Casa
~~~

or:

~~~text
FamilyLocationAlert

Saiu de Casa
~~~

Notifications allow the device user to immediately see when a zone event has occurred.

---

## Contact Management

Contacts are stored locally in the application's Room database.

Each contact can contain:

- Name
- Phone number

Contacts can then be associated with individual geographical zones.

This allows different locations to notify different people.

For example:

~~~text
Casa
├── João
└── Maria

Escola
└── João

Trabalho
└── Maria
~~~

---

## Data Storage

The application uses **Room** for persistent local storage.

The database contains information related to:

- Locations
- Contacts
- Location/contact relationships
- SMS alert configuration

The relationship between locations and contacts is represented through a many-to-many association.

~~~text
Location
   │
   │
   ├──── LocationContactCrossRef ──── Contact
   │
   └──── SMS Alert Configuration
~~~

All of this data is stored locally on the Android device.

---

## Technology Stack

- Kotlin
- Jetpack Compose
- Android Jetpack
- Room
- Kotlin Coroutines
- Google Play Services Location
- Fused Location Provider
- Android Foreground Services
- Android SMS APIs

---

## Requirements

- Android 8.1 / API 27 or newer
- Android device with location services
- Location permissions
- Background location permission
- Notification permission on supported Android versions
- SIM card and SMS capability for SMS alerts

The application is primarily intended to run on a physical Android device.

---

## Permissions

Depending on the features being used, FamilyLocationAlert requires Android permissions related to:

- Precise location
- Approximate location
- Background location
- SMS
- Notifications
- Network state

Some Android versions and manufacturers may apply additional restrictions to background location and foreground services.

Battery optimization settings may also affect long-term background monitoring.

---

## Installation

Clone the repository:

~~~bash
git clone https://github.com/N0rbelio/FamilyLocationAlert.git
~~~

Open the project with Android Studio.

Allow Gradle to synchronize the project and run the `app` configuration on a physical Android device.

The required Android permissions must be granted before location monitoring can operate correctly.

---

## Project Structure

~~~text
app/
└── src/
    └── main/
        ├── java/
        │   └── com.n0rbelio.familylocationalert/
        │       ├── config/
        │       ├── data/
        │       ├── service/
        │       ├── ui/
        │       └── ...
        │
        └── res/
~~~

### Main Components

| Package | Purpose |
|---|---|
| `config` | Application configuration |
| `data` | Room database, entities, converters and DAOs |
| `service` | Location monitoring, event processing and SMS handling |
| `ui` | Compose screens, dialogs and ViewModels |

---

## Location Monitoring Architecture

The location monitoring system is built around an Android Foreground Service.

The main flow is:

~~~text
FusedLocationProviderClient
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
          /   \
         /     \
        ▼       ▼
Notification   SMS
Manager        Sender
~~~

This separates location acquisition, zone detection and alert handling into different components.

---

## Event Detection

The application currently handles three location event states:

| Event | Description |
|---|---|
| `NONE` | No change in zone state |
| `ENTERED` | Device entered a configured zone |
| `EXITED` | Device left a configured zone |

When an event is detected, the event processor determines which actions should be performed based on the location configuration.

---

## Version 0.0.8

Version `0.0.8` represents a significant expansion of the project.

### Added

- Contact management
- Contact database entities
- Contact DAO
- Location/contact relationships
- Location/contact cross-reference database
- Configurable SMS alert modes
- Contact selection when configuring locations
- Location creation screen
- Location editing screen
- Location management screen
- Contacts management screen
- Administrative screen
- SMS alert selection interface
- Location event processing
- Adaptive location monitoring configuration
- Development/test monitoring intervals

### Changed

- Expanded Room database architecture
- Updated location data model
- Updated location DAOs
- Updated foreground location service
- Improved location checking
- Improved SMS handling
- Expanded Home screen
- Expanded HomeViewModel
- Reworked application navigation and UI structure
- Removed the previous location monitoring service architecture
- Consolidated functionality into the current foreground-service based monitoring system

### Removed

- Legacy `LocationMonitoringService`
- Legacy `LocationService`
- Previous location UI components that were replaced by the new screen architecture
- Previous `PermissionManager` implementation
- Hardcoded configuration previously used by older versions

---

## Development Status

FamilyLocationAlert is still an experimental project.

The application is functional, but the project architecture is still evolving.

Future versions may include:

- Further battery optimization
- Improved location accuracy handling
- Improved Android background execution compatibility
- Better error handling
- UI improvements
- Configuration improvements
- Additional testing
- Code cleanup and refactoring
- Improved documentation

The codebase may therefore contain temporary development code, test configuration and implementation details that are expected to change in future versions.

---

## Pull Requests

Pull Requests may be submitted for discussion, bug fixes or proposed improvements.

However, submitting a Pull Request does **not** guarantee that the proposed changes will be accepted or merged.

The project maintainer retains full discretion over whether a Pull Request is reviewed, modified, accepted, rejected or closed.

---

## Issues

Bug reports and feature requests can be submitted through GitHub Issues.

When reporting a problem, please provide:

- Android version
- Device model
- FamilyLocationAlert version
- Steps to reproduce
- Expected behaviour
- Actual behaviour
- Relevant logs when applicable

Please do not publish private location information, phone numbers or other sensitive personal information in public issues.

---

## Privacy

FamilyLocationAlert is designed around local device functionality.

The core application does not require a remote backend or cloud database.

Location data, configured zones, contacts and alert configuration are stored locally on the device.

SMS alerts are sent through the device's cellular network.

Because location and contact information can be sensitive, users should ensure that the application is used appropriately and that required permissions are granted knowingly.

---

## Security

Do not commit sensitive information to the repository.

This includes:

- Phone numbers belonging to real users
- API keys
- Passwords
- Authentication tokens
- Private certificates
- Signing keys
- Private configuration files
- Personal location information

Development configuration should use placeholder values where appropriate.

---

## License

FamilyLocationAlert is licensed under the **GNU General Public License v3.0**.

This means that modified and redistributed versions of the project must comply with the GPLv3 license requirements, including the corresponding source-code requirements when the license applies.

See the [`LICENSE`](LICENSE) file for the complete license text.

---

## Disclaimer

FamilyLocationAlert is an experimental project under active development.

Location accuracy and monitoring reliability depend on:

- GPS availability
- Android location services
- Device hardware
- Battery optimization
- Manufacturer-specific Android restrictions
- Network conditions
- Mobile carrier behaviour

SMS delivery also depends on the device, SIM card, mobile network and carrier.

FamilyLocationAlert should **not** be relied upon as the sole solution for safety-critical, emergency or life-critical situations.

---

## Author

**N0rbelio**

GitHub:

https://github.com/N0rbelio/FamilyLocationAlert

---

## Current Version

**0.0.8**

---

## Project Status

🚧 **Active development**

The current `0.0.8` version is a development milestone and should not be considered a stable final release.
