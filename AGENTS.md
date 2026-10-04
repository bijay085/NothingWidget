# Nothing Widget Development Instructions

## Typography

Never use Unicode em dashes (U+2014) or en dashes (U+2013) in UI, code, comments, XML, or docs.
Use ASCII hyphen-minus `-`, commas, periods, or colons instead.

## Project Goal

Build a native Android application named Nothing Widget containing multiple customizable Android home-screen widgets.

This is a personal application primarily designed for a Nothing Phone.

## Mandatory Technical Constraints

Use:

- Kotlin
- Native Android APIs
- Jetpack Compose
- Jetpack Glance
- Kotlin DSL

Do not convert this project into:

- React web app
- PWA
- Website
- Browser application
- Electron app
- Localhost-dependent application
- Server-hosted UI

The installed APK must run independently on the Android device.

## Runtime Rules

After installation, the app and normal widgets must work without:

- Android Studio
- VS Code
- Codex
- Laptop
- Localhost
- Development server
- Node server
- Vercel
- GitHub

Internet-dependent widgets may use the internet only when their specific feature requires it.

## Cost Rules

Required ongoing project cost:

**$0**

Do not add:

- Paid APIs
- Paid hosting
- Paid databases
- Paid subscriptions
- Mandatory SaaS services

Free services are allowed only when genuinely required.

Possible free services:

- GitHub
- GitHub Releases
- Vercel free tier
- Supabase free tier
- Firebase free tier
- Free public APIs

Prefer local Android APIs and local storage whenever possible.

## Development Philosophy

Keep implementations simple.

Before adding a:

- Library
- Service
- Database
- Server
- API
- Architecture layer
- Background service

Check whether it is actually required.

Avoid:

- Overengineering
- Unnecessary dependencies
- Premature abstractions
- Unnecessary networking
- Duplicate systems
- Unnecessary background processes

## Widget Architecture

Keep widgets separated where practical.

Suggested structure:

```text
widgets/
├── clock/
├── battery/
├── date/
├── notes/
├── launcher/
├── countdown/
└── dashboard/