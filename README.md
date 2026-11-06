# Sonora

An independent, local-first music player for Android. Sonora does not provide a commercial music catalogue or a paid feature tier. Import only audio you are authorized to use.

## Current foundation (not a finished MVP)

The initial Android source uses the system document picker to retain read access to selected audio files, reads basic title/artist metadata, searches a local library, saves favourites, and constructs a playback queue from the visible list. Source now includes player and queue dialogs (seek, shuffle, repeat, add, reorder, remove and clear), and best-effort queue/position snapshots that restore paused when all document grants remain valid. Media3 ExoPlayer and a MediaSessionService are wired for playback, audio focus, noisy-output handling and system media controls. These paths have **not yet been built or tested on an Android device**; see [KNOWN_LIMITATIONS.md](KNOWN_LIMITATIONS.md).

## Build

Requires JDK 17, Android SDK 35, and Gradle 8.9 (or compatible with Android Gradle Plugin 8.7.3). Configure `ANDROID_HOME` or `local.properties`, then run `gradle :app:assembleDebug`. This initial checkout does not include a Gradle wrapper. Install `app/build/outputs/apk/debug/app-debug.apk` on a compatible Android 8.0+ device. Dependency resolution requires Google's and Maven Central's repositories.

See [research](SPOTIFY_FEATURE_RESEARCH.md), [architecture](ARCHITECTURE.md), [roadmap](FEATURE_ROADMAP.md), [sources](AUDIO_SOURCES.md), [testing](TESTING.md), and [limitations](KNOWN_LIMITATIONS.md).
