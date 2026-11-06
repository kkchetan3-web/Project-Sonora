# Testing

No Android build, emulator test or real-device test has been run in this environment: Java, Gradle and Android SDK are unavailable. No results should be inferred from source inspection.

## Required before MVP acceptance
- Build on JDK 17 / SDK 35; add a verified Gradle wrapper and CI.
- Unit-test library persistence, sorting, filtering, playlist mutations and queue semantics.
- Instrument SAF import, retained/revoked grants, missing files, malformed metadata and offline playback for each supported format.
- On real devices test audio focus, calls, headset unplug, Bluetooth, notification and lock-screen controls, background lifecycle, process death and Android version differences.
- Test seeking, previous/next, shuffle/repeat, queue editing, favourites, playlists, screen rotation and low-memory recovery.
- Test accessibility with TalkBack, large text, contrast, touch targets and tablet sizes.
- Measure battery, memory, network failures and playback stability with reproducible methodology; record hardware, Android version and results separately for emulator and physical devices.
