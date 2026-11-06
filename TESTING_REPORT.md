# Alpha build and verification report

## Build execution — 2026-11-06

The [GitHub Actions run 42202879446](https://github.com/kkchetan3-web/Project-Sonora/actions/runs/42202879446) checked out commit `f9497f50029842a580bd7bcb70dd12a735993961` on the session branch. GitHub reports success for its Java/SDK checks, SDK 35 installation, Gradle wrapper version, `:app:assembleDebug`, `:app:testDebugUnitTest`, and `:app:assembleRelease`. The runner was `ubuntu-24.04` with Temurin JDK 17, Android SDK platform 35/build tools 35.0.0, Gradle wrapper 8.9, and Android Gradle Plugin 8.7.3. Release output is unsigned; compilation is not release-signing validation. These results demonstrate compatibility for this project and the CI environment, not all environments.

The run's `android-build-logs` artifact contains the captured full stdout/stderr for each command and the unit test reports; GitHub reports the artifact uploaded successfully. The sandbox cannot download GitHub's redirected logs/artifacts because their storage host is outside its outbound allowlist, so the evidence below is the GitHub-reported step conclusions; the complete logs are available via the run page. The [previous run](https://github.com/kkchetan3-web/Project-Sonora/actions/runs/42202789925) failed at `android-actions/setup-android@v3` before Gradle. The failure cause could not be retrieved due to the same network restriction; the workflow was changed to use the hosted runner's SDK and succeeded on the subsequent run. Java, Gradle, SDK, adb and a device remain unavailable in this sandbox.

## Acceptance evidence

Each not-run row has no supporting device output; it is **not a pass**. No physical device or emulator was available. Playback and security rows require the test setup detailed below, not just source inspection.

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
