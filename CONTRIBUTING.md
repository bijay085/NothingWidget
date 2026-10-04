# Contributing to Nothing Widget

Thanks for helping improve Nothing Widget.

## Ways to contribute

- Report bugs
- Request widgets or styles (open an Issue)
- Improve docs or README screenshots
- Submit pull requests for fixes and features

## Development rules

- Native Android only (Kotlin, Compose for app UI, AppWidget / RemoteViews for widgets)
- Keep ongoing cost at $0 (no paid APIs or mandatory SaaS)
- Isolate each widget under `app/src/widgets/<name>/`
- Do not convert the project into a web app, PWA, or Electron app
- Never use Unicode em dashes or en dashes in UI, code, or docs (ASCII `-` only)

See [AGENTS.md](./AGENTS.md) for full agent / contributor instructions.

## Local setup

1. Clone the repo
2. Open in Android Studio or build with Gradle
3. Install a debug APK on a device or emulator (API 31+)

```bash
./gradlew :app:assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Pull request tips

- Keep PRs focused (one widget or one fix)
- Do not commit `local.properties`, `.idea/`, or `build/` outputs
- Prefer clear commit messages, for example:
  - `feat: add device health widget pages`
  - `fix: improve widget preview rendering`
  - `docs: refresh README and roadmap`

## Code of conduct

Be respectful. This is a personal / community project inspired by Nothing Phone design, not an official Nothing product.
