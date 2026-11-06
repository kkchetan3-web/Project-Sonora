# Implementation status and repository audit

Audit 2026-11-06: initial branch contains a single Android app module, Compose Activity, Media3 service, manifest, Gradle configuration and research/planning documents. No pre-existing tests, assets, backend, CI or wrapper. Local SAF import and favourites are stored in SharedPreferences; tracks are re-read on library changes and the current filtered list becomes the playback queue. Media3 owns playback in a foreground-capable service, requests audio focus and handles noisy output. All of this remains unverified on Android.

Highest priorities: local persistence and resilient playback restoration; queue editing and playlists; build/test infrastructure; foreground-service and external controller security; real-device validation. Weaknesses: monolithic Activity, synchronous preference writes/snapshots, no scalable database, no artwork, limited error handling for unavailable documents, no playback restoration after system media resumption, and no production security audit. See [parity](PREMIUM_FEATURE_PARITY.md) and [limitations](KNOWN_LIMITATIONS.md) for exact scope.

Build requires JDK 17, SDK 35 and Gradle 8.9; a pinned wrapper and CI were added, but this sandbox lacks Java and SDK and cannot reach Google Maven. There are no verified builds, emulator tests or physical-device tests.

This pass adds a player dialog and queue actions, plus paused queue/position restore and JSON snapshot unit tests. These are **In development**, not verified. Playlist management remains **Not started**; the Alpha milestone is not complete. Snapshot restoration filters entries whose persisted read grants disappeared, but retained grants do not prove the files still exist. System-initiated media resumption is not supported.
