# GitHub discovery checklist

Run these after pushing the docs / README update.

## Repository description

Set in GitHub **Settings → General → Description**, or with GitHub CLI:

```bash
gh repo edit bijay085/NothingWidget --description "A customizable Android widget platform with Nothing OS inspired widgets."
```

## Topics

Settings → Topics, or:

```bash
gh repo edit bijay085/NothingWidget --add-topic android --add-topic android-widget --add-topic kotlin --add-topic jetpack-compose --add-topic nothing-os --add-topic nothing-phone --add-topic widgets --add-topic android-development --add-topic appwidget --add-topic customization
```

## First release (v0.1.0)

1. Build a release or debug APK
2. Tag and publish:

```bash
git tag v0.1.0
git push origin v0.1.0
gh release create v0.1.0 app/build/outputs/apk/debug/app-debug.apk --title "v0.1.0" --notes "Initial widget release. See CHANGELOG.md."
```

## Screenshots

Add PNGs under `docs/` (see `docs/README.md`), then uncomment the image block in the root README.
