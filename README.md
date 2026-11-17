# SAJI Text Effects Studio

Native Android text-effects editor built with Kotlin and Jetpack Compose.

## Phase 2 preview
- Video import and in-app playback
- Live text overlay preview
- Neon, 3D, Chrome, Glitch, Fire, Outline, Shadow and Retro styles
- Animated Pulse, Bounce, Shake and Typewriter preview effects
- Play/pause motion preview toggle
- Clip-position scrubber and Start/Middle/End keyframe presets
- Text scale and rotation controls
- Effect intensity and text-size controls

## Build
Open in Android Studio with JDK 17 and Android SDK 35, or run:

```bash
gradle assembleDebug
```

GitHub Actions builds a debug APK and uploads it as an artifact on pushes and pull requests to `main`.

## Current limitations
The timeline and keyframe controls currently change the live preview only. They are not yet a frame-accurate video timeline. Exporting a rendered MP4 with the animated text overlay is not implemented yet, so the Export button remains disabled. The current preview is a development build, not a finished CapCut replacement.

## Package
`com.saji.texteffects`
