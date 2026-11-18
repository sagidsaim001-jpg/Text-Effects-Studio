# SAJI Text Effects Studio

Native Android text-effects editor built with Kotlin and Jetpack Compose.

## Features
- Import and preview a video
- Live editable text overlay preview
- Neon, 3D, Chrome, Glitch, Fire, Outline, Shadow and Retro text styles
- Animated Pulse, Bounce, Shake and Typewriter effects
- Animation preview toggle, clip-position scrubber and Start/Middle/End keyframe presets
- Text scale, rotation, intensity and size controls
- MP4 export using AndroidX Media3 Transformer
- Canvas-rendered text overlay is composited into exported video frames
- Exported videos are copied to Movies/SAJI Text Effects Studio in the device Gallery

## Build
Open in Android Studio with JDK 17 and Android SDK 35, or run:

```bash
gradle assembleDebug
```

GitHub Actions builds a debug APK and uploads it as an artifact on pushes and pull requests to `main`.

## Export notes
The export engine uses Media3 Transformer and Media3 Effect. Export performance and supported input/output formats depend on the device's hardware codecs. On older or lower-memory phones, try a shorter clip first. The preview keyframe presets currently adjust the preview transform; they are not yet stored as a full multi-keyframe animation curve.

## Package
`com.saji.texteffects`
