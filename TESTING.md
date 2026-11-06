# Testing

Debug and release builds and unit tests passed in GitHub Actions; see TESTING_REPORT.md. Limited Media3 instrumentation passed on an emulator; no physical-device tests have been run. Java, Gradle and Android SDK are unavailable in the sandbox. No runtime results should be inferred from source inspection.

## Required before MVP acceptance
- Build using the pinned Gradle wrapper on JDK 17 / SDK 35 and verify CI execution.
- Unit-test library persistence, sorting, filtering, playlist mutations and queue semantics.
- Instrument SAF import, retained/revoked grants, missing files, malformed metadata and offline playback for each supported format.
- On real devices test audio focus, calls, headset unplug, Bluetooth, notification and lock-screen controls, background lifecycle, process death and Android version differences.
- Test seeking, previous/next, shuffle/repeat, queue editing, favourites, playlists, screen rotation and low-memory recovery.
- Test accessibility with TalkBack, large text, contrast, touch targets and tablet sizes.
- Measure battery, memory, network failures and playback stability with reproducible methodology; record hardware, Android version and results separately for emulator and physical devices.
