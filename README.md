# Nothing Widget

Nothing Widget is a personal Android application containing a collection of standalone, customizable home-screen widgets.

The goal is to build one native Android app that provides multiple widgets without requiring paid widget applications, subscriptions, cloud hosting, or a computer/server running in the background.

## Core Principles

- Native Android application
- Kotlin
- Jetpack Compose for app UI
- Jetpack Glance for Android home-screen widgets
- Widgets run directly on the Android device
- No PWA
- No browser-based application
- No localhost runtime requirement
- No laptop/server required after APK installation
- No mandatory cloud backend
- No paid APIs
- No paid hosting
- No paid database
- No subscriptions
- Prefer local device APIs and local storage
- Free external services may be used only when genuinely necessary

## Target Device

Primary target:

- Nothing Phone
- Android
- Personal use

The application may support other Android devices, but Nothing Phone compatibility and design are the primary focus.

## Technology

- Kotlin
- Android Studio
- Jetpack Compose
- Jetpack Glance
- Kotlin DSL
- Android AppWidget system
- DataStore for preferences where appropriate
- Room only if structured local persistence is genuinely required

## Architecture

The app contains two major areas:

### Main App

Used for:

- widget gallery
- widget previews
- widget configuration
- app settings
- future widget management

### Home-Screen Widgets

Each widget should be independently configurable.

Example future widgets:

- Clock
- Date
- Battery
- Device status
- Notes
- Countdown
- App launcher
- Quick actions
- Storage information
- Calendar
- Music controls
- Custom text
- Image widget
- Combined dashboard widgets

## Development Rule

Do not build unnecessary infrastructure.

Prefer:

1. Android system APIs
2. Local storage
3. Local computation
4. Free APIs only when online information is required
5. Free hosting/database only when there is a real requirement

Do not introduce servers simply because they are convenient during development.

## Runtime Requirement

After the APK is installed:

```text
Nothing Phone
    ↓
Nothing Widget
    ↓
Home-screen widgets