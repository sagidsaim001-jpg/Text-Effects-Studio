# SAJI CapCut 2

A native Android video-editor UI foundation built with Kotlin and Jetpack Compose.

## Phase 1: UI foundation

This phase focuses on building the complete editing experience and navigation before connecting video-processing engines.

### Main app screens
- Home and recent projects
- Templates discovery and category browsing
- Media library sections for sounds, effects, fonts, stickers and brand assets
- Profile, workspace and preferences
- Media picker with Videos, Photos, Live Photos, album selection and multi-select layout

### Editor workspace
- Editor header with project title, aspect ratio, quality shortcut and export entry
- Preview monitor with portrait 9:16 canvas
- Playback controls and time readout
- Multi-track timeline layout with video, audio and text tracks
- Clip-selection state and clip tool tray
- Main tool navigation for Edit, Audio, Text, Stickers, Overlay, Effects, Filters, Captions, Format, Canvas and More

### Tool panels and sub-menus
The editor includes UI entry points and configurable panels for:
- Clip edit: replace, split, speed, volume, animation, adjust, mask, opacity, copy, reverse, freeze and delete
- Speed: normal and curve presets
- Animation: In, Out, Combo and Loop
- Audio: music, sound effects, extract audio, voiceover, text-to-speech, beats, volume, fade, voice effects and noise reduction
- Text: add text, templates, auto captions, fonts, style, effects, animation, bubbles, text-to-speech and tracking
- Stickers and overlays
- Video effects, body effects, AI effects, filters and transitions
- Caption workflows including automatic, manual and bilingual captions
- Format ratios, canvas backgrounds, blur and color
- Advanced tool entry points such as chroma key, background removal, stabilization, motion blur, enhance, retouch, relight and camera tracking
- Export settings for resolution, frame rate, code rate, HDR and optical flow

## Important phase boundary

Phase 1 is a UI/navigation milestone. Some displayed sample media, project rows and preview artwork are placeholders. Media import, editing operations, effect rendering, audio processing, caption generation, templates download and actual video export are not connected yet. These are planned for later phases and are not represented as working features.

The earlier experimental Media3 export engine was removed from this UI milestone because its 1.11.1 dependency required compile SDK 36 while this repository's Android Gradle Plugin setup targets compile SDK 35. Export will be reintroduced with a compatible, tested setup in a later feature phase.

## Build

Requirements: JDK 17 and Android SDK 35.

```bash
gradle assembleDebug
```

GitHub Actions builds a debug APK and uploads it as an artifact after successful pushes to `main`.

## Package

`com.saji.texteffects`
