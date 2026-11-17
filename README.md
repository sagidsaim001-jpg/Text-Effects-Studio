# SAJI Text Effects Studio

A native Android (Kotlin + Jetpack Compose) starter for a CapCut-inspired text-effects editor.

## Current MVP
- Dark, mobile-first editor layout
- 9:16 preview stage
- Video picker and in-app video preview
- Editable text overlay
- Selectable Neon, 3D, Chrome, Glitch, Fire, Outline, Shadow and Retro preview styles
- Effect intensity and text-size controls
- Reset controls

## Build
Open in Android Studio with JDK 17 and Android SDK 35, or run:

```bash
gradle assembleDebug
```

GitHub Actions builds a debug APK and uploads it as an artifact on pushes and pull requests to `main`.

## Current limitations
This is the first MVP foundation. Rendering/exporting the edited video, timeline editing, keyframe animation, and persistent effect presets are not implemented yet. The Export button is intentionally disabled until the rendering pipeline is added.

## Package
`com.saji.texteffects`
