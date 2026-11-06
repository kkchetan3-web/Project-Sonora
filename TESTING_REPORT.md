# Testing report

2026-11-06 repository audit: no JDK, Gradle, Android SDK, emulator or physical Android device available in this environment. Google Maven is outside the allowed network hosts. Consequently no Android build, unit test, instrumentation test, emulator test, real-device test, audio-focus test or battery measurement has been run. Snapshot serialization unit tests were added but have not been run. Static review is not verification.

Reproduce with JDK 17, Android SDK platform 35, Gradle 8.9, network access to Google Maven and Maven Central: `gradle :app:testDebugUnitTest :app:assembleDebug`; then use `gradle :app:connectedDebugAndroidTest` with a connected test device where instrumentation tests exist. Install the APK and execute the playback checklist in [TESTING.md](TESTING.md), recording OS, hardware and actual results. A unit test alone does not verify background or Bluetooth behavior.

Static check performed: `git diff --check` passed for the queue work. This checks patch whitespace only, not Kotlin compilation or runtime behavior.

## Alpha pass — 2026-11-06
- `./gradlew --version`: **failed** before Gradle started: Java is not installed (`JAVA_HOME` unset). No Android build or unit tests could run. Android SDK and `adb` are also absent; Google's repositories are not accessible in this sandbox. The CI workflow has been added but has not been executed here.
- Unit tests added for snapshot serialization and missing-grant filtering; **not executed**.
- Integration tests: none executed. Physical-device tests: none executed. Alpha is not complete.

### Separate lifecycle checks required
1. **Interruption:** play, interrupt with a call or another focus owner, observe focus loss/duck/resume according to Android behavior; inspect the queue and checkpoint after returning.
2. **Process termination:** while playing, stop the process after a known position, reopen the app and verify the queue/current track/repeat/shuffle and a paused position within the checkpoint interval. Test force-stop separately from ordinary process death; system playback resumption is not implemented.
3. **Device restart:** reboot without clearing app data, launch Sonora, check retained SAF grants and paused restoration; also test removable storage and revoked/deleted files.
4. Verify notification, lock screen and Bluetooth controls on actual Android versions/devices, including controller authorization and denial of external queue changes. Record exact OS/device, steps and outcomes; no emulator result implies physical-device success.
