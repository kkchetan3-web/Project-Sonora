# Testing report

2026-11-06 repository audit: no JDK, Gradle, Android SDK, emulator or physical Android device available in this environment. Google Maven is outside the allowed network hosts. Consequently no Android build, unit test, instrumentation test, emulator test, real-device test, audio-focus test or battery measurement has been run. Static review is not verification.

Reproduce with JDK 17, Android SDK platform 35, Gradle 8.9, network access to Google Maven and Maven Central: `gradle :app:testDebugUnitTest :app:assembleDebug`; then use `gradle :app:connectedDebugAndroidTest` with a connected test device where instrumentation tests exist. Install the APK and execute the playback checklist in [TESTING.md](TESTING.md), recording OS, hardware and actual results. A unit test alone does not verify background or Bluetooth behavior.
