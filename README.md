# Nothing Widget — Free Customizable Home Screen Widgets for Nothing Phone

**Nothing Widget** is a free, open-source Android app with customizable home-screen widgets built for [Nothing Phone](https://nothing.tech/) and Nothing OS. Add a Round Clock, Weather, and more — without subscriptions, paid widget packs, or a laptop running in the background.

> Looking for **Nothing Phone widgets**, **Nothing OS home screen widgets**, or a **free Nothing Widget app**? This project is a native Android alternative focused on the Nothing aesthetic: clean, customizable, and $0 ongoing cost.

**Repository:** [github.com/bijay085/NothingWidget](https://github.com/bijay085/NothingWidget)

---

## Why Nothing Widget?

Many Android widget apps charge money, push subscriptions, or need an always-on account. **Nothing Widget** is different:

| | Nothing Widget |
| --- | --- |
| Price | **Free** — no paid unlocks, no subscriptions |
| Ongoing cost | **$0** — no paid APIs, hosting, or SaaS required |
| Platform | Native Android (Kotlin) — not a PWA or website |
| Target | **Nothing Phone** / Nothing OS first; works on other Android devices too |
| Runtime | Works offline on-device after install (internet only when a widget needs it, e.g. weather) |

Want **more widgets** or a new style? You can **request** them the same way as any other feature — open a GitHub Issue. Want to support the project with **money** (donation) or sponsorship? You can request that setup the same way, or star the repo to help others find **Nothing Widget**.

---

## Features

### Current widgets

- **Round Clock widget** — circular clock for the Nothing Phone home screen; time, date, alarm cue; fonts and colors you can customize
- **Weather widget** — local weather card (temperature, condition, location) with free public weather data when online

### Customization

- Per-element colors, fonts, and sizes (NDot-style / digital / clean / futuristic / pixel / atmospheric options)
- Background choices per widget
- Live preview inside the app
- Open customization from the app **or** from the launcher widget settings / reconfigure entry

### App shell

- Widget library with previews
- Favorites and settings
- Theme modes tuned for a Nothing-like look

---

## Keywords & discovery (what this project is about)

Use these terms when searching or linking so people can find **Nothing Widget** in search and on GitHub:

`Nothing Widget` · `Nothing Phone widgets` · `Nothing OS widgets` · `Nothing Phone home screen` · `free Android widgets` · `customizable clock widget` · `Nothing Phone weather widget` · `open source Nothing widgets` · `Android AppWidget Nothing`

---

## Screenshots / preview

Install the debug or release APK on a **Nothing Phone** (or any Android 12+ device), then:

1. Open **Nothing Widget**
2. Browse the widget library
3. Pin **Round Clock** or **Weather** to the home screen
4. Long-press the widget → **Widget settings** (launcher label) to customize  
   or open **Customize** from the app card

---

## Requirements

- Android **12+** (API 31+)
- Primary design target: **Nothing Phone** / Nothing OS
- No Google Play subscription, no account required for core widgets

---

## Build & install

```bash
# Clone
git clone https://github.com/bijay085/NothingWidget.git
cd NothingWidget

# Build debug APK
./gradlew :app:assembleDebug

# Install (Windows example)
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

On Windows you can use `gradlew.bat` instead of `./gradlew`.

The installed APK runs on the phone by itself — no Android Studio, localhost, or PC required after install.

---

## Project principles

- Native Android only (Kotlin, Jetpack Compose for app UI, AppWidget / RemoteViews for home-screen widgets)
- Prefer local APIs and local storage
- Free public APIs only when a feature truly needs the network
- No PWA, no Electron, no mandatory cloud backend
- Keep each widget isolated under `widgets/<name>/`

See [AGENTS.md](./AGENTS.md) for contributor / agent development rules.

---

## Request more widgets (or support)

You can request new widgets, styles, or fixes the same way:

1. Open **[Issues](https://github.com/bijay085/NothingWidget/issues)**
2. Describe the widget (e.g. battery, calendar, notes, music)
3. Optional: attach a mockup or Nothing-style reference

**Money / support:** This app stays free for users. If you want to fund faster development (donation link, sponsorship, paid custom widget request), open an Issue titled something like `Support / donation request` and we can set that up similarly — without turning basic widgets into a paywall.

Star the repo if **Nothing Widget** helped your Nothing Phone home screen — stars help the project show up when people search for Nothing widgets.

---

## FAQ

### What is Nothing Widget?
**Nothing Widget** is an open-source Android app that adds free, customizable home-screen widgets designed for Nothing Phone and Nothing OS.

### Is Nothing Widget free?
Yes. No subscription, no paid widget pack, and the project targets **$0** ongoing cost.

### Does it work only on Nothing Phone?
It is designed for Nothing Phone first, but it can run on other Android devices that meet the minimum SDK.

### How do I customize a widget?
Use **Customize** in the app, or long-press the home-screen widget and open the launcher’s widget settings / reconfigure option.

### Can I request another widget?
Yes — open a GitHub Issue. Request more widgets the same way you would request a bugfix or feature.

---

## Tech stack

- Kotlin
- Jetpack Compose (app UI)
- Android AppWidget + RemoteViews (home-screen widgets)
- DataStore (preferences)
- Kotlin DSL Gradle
- Free weather API where needed (e.g. Open-Meteo)

---

## License & contributing

Contributions and widget requests are welcome via pull requests and issues.

If this README does not yet list a formal license file, treat the project as source-available for personal use until a `LICENSE` is added — open an Issue if you need clarification for redistribution.

---

## Links

- **Project:** [Nothing Widget on GitHub](https://github.com/bijay085/NothingWidget)
- **Issues / requests:** [github.com/bijay085/NothingWidget/issues](https://github.com/bijay085/NothingWidget/issues)
- **Nothing (brand):** [nothing.tech](https://nothing.tech/) — unrelated company; this is a community / personal widget app inspired by the Nothing Phone look

---

*Nothing Widget — free customizable widgets for Nothing Phone home screens.*
