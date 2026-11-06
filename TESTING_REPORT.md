# Alpha build and verification report

## Build execution — 2026-11-06

The [GitHub Actions run 42202879446](https://github.com/kkchetan3-web/Project-Sonora/actions/runs/42202879446) checked out commit `f9497f50029842a580bd7bcb70dd12a735993961` on the session branch. GitHub reports success for its Java/SDK checks, SDK 35 installation, Gradle wrapper version, `:app:assembleDebug`, `:app:testDebugUnitTest`, and `:app:assembleRelease`. The runner was `ubuntu-24.04` with Temurin JDK 17, Android SDK platform 35/build tools 35.0.0, Gradle wrapper 8.9, and Android Gradle Plugin 8.7.3. Release output is unsigned; compilation is not release-signing validation. These results demonstrate compatibility for this project and the CI environment, not all environments.

The run's `android-build-logs` artifact contains the captured full stdout/stderr for each command and the unit test reports; GitHub reports the artifact uploaded successfully. The sandbox cannot download GitHub's redirected logs/artifacts because their storage host is outside its outbound allowlist, so the evidence below is the GitHub-reported step conclusions; the complete logs are available via the run page. The [previous run](https://github.com/kkchetan3-web/Project-Sonora/actions/runs/42202789925) failed at `android-actions/setup-android@v3` before Gradle. The failure cause could not be retrieved due to the same network restriction; the workflow was changed to use the hosted runner's SDK and succeeded on the subsequent run. Java, Gradle, SDK, adb and a device remain unavailable in this sandbox.

## Acceptance evidence

The table below describes the earlier build-only run; see Runtime validation below for later emulator evidence. Each not-run row is not a pass. No physical device was available.

| Criterion / test performed | Environment | Expected | Actual | Status / evidence |
|---|---|---|---|---|
| Debug compile: `:app:assembleDebug` | GitHub Actions Ubuntu/JDK 17/SDK 35 | Debug compiles | GitHub step succeeded | PASS — [run](https://github.com/kkchetan3-web/Project-Sonora/actions/runs/42202879446) |
| Unit tests: `:app:testDebugUnitTest` | Same CI runner | All configured tests pass | GitHub step succeeded; report in artifact | PASS — [run](https://github.com/kkchetan3-web/Project-Sonora/actions/runs/42202879446). Snapshot serialization and missing-grant filtering tests are not device tests. |
| Release compile: `:app:assembleRelease` | Same CI runner | Release compiles | GitHub step succeeded | PASS — [run](https://github.com/kkchetan3-web/Project-Sonora/actions/runs/42202879446); unsigned |
| SAF import and persistence | None | File imports and remains accessible after restart | Not run | NOT TESTED; no device output |
| SAF revoked permission | Snapshot filtering unit test on CI | Snapshot excludes URI absent from supplied grant set | Unit-test task passed | PARTIAL — simulated grant set only, not OS revocation; [run](https://github.com/kkchetan3-web/Project-Sonora/actions/runs/42202879446) |
| Missing/deleted file with grant retained | None | Error surfaced; library and service remain usable | Not run | NOT TESTED; no device output |
| Unsupported/corrupt audio and invalid metadata | None | No crash; actionable error | Not run | NOT TESTED; no device output |
| Inaccessible or malformed URI references | None | No crash, recovery available | Not run | NOT TESTED; no device output |
| Queue order/add/remove/clear/play-next | None | UI and Media3 timeline agree | Not run | NOT TESTED; no device output |
| Queue/current track/position restore | Snapshot codec unit tests on CI | Persistence and restoration across process death | Serialization tested only; actual process death not run | PARTIAL — [run](https://github.com/kkchetan3-web/Project-Sonora/actions/runs/42202879446) |
| Shuffle/repeat | Snapshot round-trip unit test on CI | Settings and player behavior survive restart | Serialization tested only; actual player behavior not run | PARTIAL — [run](https://github.com/kkchetan3-web/Project-Sonora/actions/runs/42202879446) |
| Playback interruption/audio focus | None | Correct pause/duck/resume | Not run | NOT TESTED; no device output |
| Notification and lock-screen controls | None | Play/pause/skip function with correct metadata | Not run | NOT TESTED; no device output |
| Bluetooth controls | None | Supported controls operate against current session | Not run | NOT TESTED; no device output |
| Normal activity recreation | None | Playback continues, UI reconnects | Not run | NOT TESTED; no device output |
| Process death/force-stop | None | Paused restoration on app relaunch, with bounded checkpoint loss; distinguish force-stop | Not run | NOT TESTED; no device output |
| Device restart | None | After app launch, retained grants and paused snapshot restore | Not run | NOT TESTED; no device output |
| External controller authorization/intent validation | Static source review only | Untrusted controllers denied; allowed controllers cannot inject media; system controls work | Callback rejects nontrusted external controllers and removes playlist mutation commands for other trusted controllers; runtime identity and command enforcement not tested | NOT VERIFIED; no security test output |
| Persistent playlists | None; not implemented | Create and persist Room playlists | Not implemented | FAIL / Alpha blocker |

## Security review notes

The launcher Activity is exported and has a MAIN/LAUNCHER filter. The exported `MediaSessionService` has the Media3 service action and declares media-playback foreground service permissions/type. SAF imports retain read permission only. The session accepts the app UID and Media3 notification controller, rejects untrusted controllers, and strips set/change media items and playlist-metadata commands from other trusted controllers. Android's trusted-controller category includes enabled notification listeners; this is not equivalent to exclusively system apps. Validate actual controller identity on API 26–27 (UID can be unknown), media notification, headset/Bluetooth dispatch, a malicious third-party controller, and allowed command enforcement on each supported API. This static review is not a security audit or proof of access control. The app has no internet permission, but malicious or inaccessible content providers and large/corrupt metadata still need instrumentation and stress tests.

## Required next tests

Use a real device with several known-good user-owned local files and malformed/unsupported files; document model and OS. Test interruption by another focus owner separately from process death and device reboot. Force-stop is distinct from ordinary process death and can suppress system callbacks. Verify restored position within the five-second checkpoint interval, queue mutations and shuffle/repeat against the live player, SAF grant revocation and deletion separately, notification/lock-screen/headset/Bluetooth controls, and unauthorized external controller attempts. Test on emulator first where suitable, but record real-device results separately. Alpha cannot pass until all mandatory checks, persistent playlists and major failure handling are complete.

# Runtime validation — 2026-11-06

The first end-to-end emulator pass is [run 42206144571](https://github.com/kkchetan3-web/Project-Sonora/actions/runs/42206144571), commit `8989de6f1bf6c365f253ce1ebad5ed4cea20d104`: GitHub Actions `ubuntu-24.04`, hardware-accelerated Android API 35 Google APIs x86_64 emulator. Model and release properties, screenshot, logcat, instrumentation XML/HTML and full task output are in its `emulator-evidence-8989de6f1bf6c365f253ce1ebad5ed4cea20d104` artifact. The debug APK is in `sonora-debug-apk-8989de6f1bf6c365f253ce1ebad5ed4cea20d104` (debug signed, artifact expires under GitHub retention policy). The sandbox cannot download the artifacts from GitHub's storage host; they are available from the run page. Commands: `./gradlew :app:assembleDebug :app:assembleDebugAndroidTest`, then, after emulator boot, `./gradlew :app:connectedDebugAndroidTest`. The build job separately passed `:app:assembleDebug`, `:app:testDebugUnitTest` and `:app:assembleRelease`. Reports below refer to this precise run, not subsequent changes.

| Test | Environment | Expected result | Actual result | Status |
|---|---|---|---|---|
| Generated five-second silent PCM WAV, start/pause, seek, next/previous, add/move/remove/clear, shuffle and repeat-all through MediaController | API 35 emulator, instrumentation | Media3 session reflects changes and plays | `playbackAndQueueControlsOperateOnRealMedia3Session` passed | PASS for tested controller operations only; no SAF/UI/audible-output proof |
| Activity launch and recreation during playback | Same emulator | Session continues playback across recreation | `activityRecreationKeepsServicePlayback` passed | PASS for this recreation scenario only |
| Corrupt WAV | Same emulator, private cache file | Player reports error, app survives | `corruptAudioReportsPlayerErrorInsteadOfCrashing` passed | PASS for one malformed fixture only |
| Revoked-grant filtering, serialization, position and repeat/shuffle round-trip | JVM unit tests, CI | Snapshot model filters invalid grants and preserves state | Unit-test task passed | PARTIAL: simulated grants/model only, not OS lifecycle |
| Import audio with SAF, grant persistence/revocation | None | Import and restart/revoke safely | Not run | BLOCKED — no SAF instrumentation harness or accessible device |
| Missing/deleted/unavailable URI, unsupported format, invalid metadata | None | No crash, visible error, other items usable | Not run | BLOCKED — no device test and no provider harness; corrupt fixture above is narrower |
| Queue and position restore, repeat/shuffle persistence after process termination and reboot | None | Restore current item and approximate position while paused | Not run | BLOCKED — no lifecycle instrumentation or physical device |
| App background, interruption, audio focus | None | Background playback continues; focus behavior correct | Not run | BLOCKED — no interruption harness/device |
| Notification and lock-screen controls, foreground-service lifetime | None | Controls operate and playback remains active | Not run | BLOCKED — no interaction test or physical device |
| Bluetooth media and manufacturer-specific behavior | None | Hardware routing and controls work | Not run | BLOCKED — no physical Bluetooth device |
| Untrusted/trusted external controller and exported service security | Static review only | Reject unauthorized connections and queue mutation | Source restricts controllers; no adversarial runtime verification | BLOCKED — no separate test app/device security tests |
| Persistent Room playlists and full-screen player | None | Functionality present | Not implemented | BLOCKED — outstanding Alpha implementation |

Earlier [run 42205286009](https://github.com/kkchetan3-web/Project-Sonora/actions/runs/42205286009) ran the emulator: corrupt-file test passed, but the two playback tests timed out before playback began. Those initial tests started playback without first bringing the activity to the foreground; Android 15 can reject audio focus in that state. The revised tests launch the activity first and passed. This is consistent with an audio-focus eligibility issue, but the earlier logs did not capture a decisive focus error, so the precise root cause is **not confirmed**, and this does not establish behavior for background starts.

## Manual validation procedure for blocked cases

1. From the CI run page download the `sonora-debug-apk-*` artifact, unzip and install with `adb install -r app-debug.apk`. Record device model, Android version, app revision, output and logs (`adb logcat -c` before each case, `adb logcat -d > case.log` afterwards). Use user-owned MP3/FLAC and other supported-format samples.
2. In Sonora choose **Import audio**, pick multiple files, play one, force-stop with `adb shell am force-stop com.sonora.app`, relaunch and verify the files and read permissions still work. Revoke a selected document's grant using a test DocumentsProvider or system grant management, then relaunch and verify other items work. Separately delete a selected file while retaining the grant and test the library and queue; repeat with inaccessible/removable storage and malformed, corrupt and unsupported files. Capture failures and verify neither activity nor service crashes.
3. Import several tracks. Add one as play-next, move/remove/clear entries, compare each operation with what actually plays, seek and toggle repeat-one/repeat-all/shuffle. At a known timestamp, allow at least one five-second checkpoint, kill the app process with `adb shell am kill com.sonora.app` when Android permits, relaunch and compare track, queue, position, shuffle and repeat. Test `force-stop` separately and then reboot separately; never assume these are equivalent. Note the actual loss window.
4. Start playback in foreground, background the app, lock the screen, exercise notification and lock-screen pause/skip and start another audio app or call to test focus loss. Restore activity and inspect state. Repeat with a real Bluetooth headset and media keys; record manufacturer and Android version.
5. From a separately installed untrusted test app, attempt MediaSession connection and commands, then test a user-approved notification listener and real system media controls. Verify the former cannot connect and the trusted external client cannot replace or mutate the queue, while normal controls function. Review API 26–27 identity edge cases. Do not infer security from the source review alone.

Alpha remains incomplete: the above blocked cases, Room playlists, full-screen player and major-failure remediation need actual evidence. A successful emulator test is not physical-device verification.
