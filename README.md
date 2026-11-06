# Sonora

An independent, local-first music player for Android. Sonora does not provide a commercial music catalogue or a paid feature tier. Import only audio you are authorized to use.

## Current foundation (not a finished MVP)

The initial Android source uses the system document picker to retain read access to selected audio files, reads basic title/artist metadata, searches a local library, saves favourites, and constructs a playback queue from the visible list. Source now includes player and queue dialogs (seek, shuffle, repeat, add, reorder, remove and clear), and best-effort queue/position snapshots that restore paused while dropping entries without persisted document grants. Media3 ExoPlayer and a MediaSessionService are wired for playback, audio focus, noisy-output handling and system media controls. These paths have **not yet been built or tested on an Android device**; see [KNOWN_LIMITATIONS.md](KNOWN_LIMITATIONS.md).

## Build and test

Install JDK 17 and Android SDK command-line tools. Set `JAVA_HOME` to the JDK and `ANDROID_HOME` to the SDK location, add `$ANDROID_HOME/cmdline-tools/latest/bin` and `$ANDROID_HOME/platform-tools` to your PATH, and accept SDK licences. With network access to Google's Android/Google Maven repositories, Maven Central, the Gradle plugin portal and the Gradle distribution host:

```sh
sdkmanager --licenses
sdkmanager 'platforms;android-35' 'build-tools;35.0.0' 'platform-tools'
./gradlew --no-daemon :app:testDebugUnitTest :app:assembleDebug :app:assembleRelease
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Alternatively set `sdk.dir` in an untracked `local.properties`. The checked-in wrapper pins Gradle 8.9 and verifies its distribution SHA-256. Android Gradle Plugin is 8.7.3, compile/target SDK 35, minimum SDK 26. Debug builds use the normal Android debug signing key. Release assembly is unsigned by default; production distribution requires a separately managed signing key and configuration. Never commit signing keys. CI uses the same build commands; a configured workflow is not evidence that it passed. See [TESTING_REPORT.md](TESTING_REPORT.md) for execution status and lifecycle/device test cases.

See [research](SPOTIFY_FEATURE_RESEARCH.md), [architecture](ARCHITECTURE.md), [roadmap](FEATURE_ROADMAP.md), [sources](AUDIO_SOURCES.md), [testing](TESTING.md), and [limitations](KNOWN_LIMITATIONS.md).
