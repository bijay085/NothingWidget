# Nothing Widget

[![Android](https://img.shields.io/badge/Android-Kotlin-3DDC84?logo=android&logoColor=white)](https://developer.android.com/)
[![Min SDK](https://img.shields.io/badge/API-31%2B-blue)](https://developer.android.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](./LICENSE)
[![GitHub stars](https://img.shields.io/github/stars/bijay085/NothingWidget?style=social)](https://github.com/bijay085/NothingWidget)

A customizable Android widget platform inspired by Nothing OS design.

Built for [Nothing Phone](https://nothing.tech/) first. Free, open source, $0 ongoing cost. No subscriptions, no mandatory cloud, no laptop left running after install.

**Repository:** [github.com/bijay085/NothingWidget](https://github.com/bijay085/NothingWidget)

## Features

- Round Clock Widget
- Weather Widget
- Screen Time Widget (compact + large)
- Quick Actions Widget
- Device Health Widget (battery + performance pages)
- Live widget customization (colors, fonts, backgrounds)

## Screenshots

Add device screenshots under `docs/` (see [docs/README.md](./docs/README.md)), then uncomment:

```md
![Round Clock](docs/clock.png)
![Weather](docs/weather.png)
![Screen Time](docs/screentime.png)
![Quick Actions](docs/quick_actions.png)
![Device Health](docs/device_health.png)
![Customization](docs/customization.png)
```

## Architecture

Each widget is isolated under `app/src/widgets/<name>/` (Kotlin receiver + `res/`). Shared app UI stays in `app/`.

```text
app/src/widgets/
├── round_clock/
├── weather/
├── screen_time/
├── screen_time_large/
├── quick_actions/
└── device_health/
```

## Tech stack

- Kotlin
- Android AppWidget API + RemoteViews
- Jetpack Compose (in-app UI)
- DataStore (preferences)
- Kotlin DSL Gradle
- Free public weather API when online (e.g. Open-Meteo)

## Roadmap

- [x] Round Clock Widget
- [x] Weather Widget
- [x] Screen Time Widgets
- [x] Quick Actions Widget
- [x] Device Health Widget
- [x] Widget customization system
- [ ] More Nothing-style widgets
- [ ] More customization options
- [ ] Stable release channel / signed APK on GitHub Releases

## Requirements

- Android 12+ (API 31+)
- Nothing Phone / Nothing OS preferred; works on other Android devices

## Build and install

```bash
git clone https://github.com/bijay085/NothingWidget.git
cd NothingWidget
./gradlew :app:assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

On Windows use `gradlew.bat`. After install, the APK runs on the phone without Android Studio or a PC.

## Project principles

- Native Android only (not a PWA, website, or Electron app)
- Prefer local APIs and local storage
- Free public APIs only when a feature needs the network
- Keep widgets isolated under `widgets/<name>/`

See [AGENTS.md](./AGENTS.md) for contributor and agent rules.

## Contributing

Widget requests, bugs, and PRs are welcome. See [CONTRIBUTING.md](./CONTRIBUTING.md).

1. Open an [Issue](https://github.com/bijay085/NothingWidget/issues)
2. Describe the widget or fix
3. Optional: attach a mockup or Nothing-style reference

## Changelog

See [CHANGELOG.md](./CHANGELOG.md).

## License

MIT. See [LICENSE](./LICENSE).

Nothing (the brand) is unrelated: [nothing.tech](https://nothing.tech/). This is a community / personal widget app inspired by the Nothing Phone look.
