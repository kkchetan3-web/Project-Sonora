# Known limitations

- Initial source has not been compiled or exercised on an emulator or device. No performance, playback or security guarantees.
- No Gradle wrapper or CI. Dependency repositories are not reachable in the current sandbox.
- No licensed online catalogue, streaming integrations, download manager, backend, accounts, or discovery/recommendations.
- No playlist management, play-next action, full-screen player, lyrics, equalizer, artwork, folders, scanning, history, settings, light theme or adaptive tablet design. Player and queue dialogs and playback snapshot restoration are implemented in source but untested. Snapshots checkpoint every five seconds during playback, so an abrupt termination can lose recent progress; a revoked grant prevents restoration of the entire queue. Media3 system playback resumption after process death is not implemented.
- Local import selects individual documents, not a directory. Metadata handling and revoked or unavailable files need more robust recovery. Format support depends on device and Media3 codecs.
- Favourites and URI references use basic SharedPreferences; indexing and metadata refresh do not scale to large libraries. No deletion UI yet.
- Notification, foreground service, external-controller authorization, audio focus and Bluetooth behavior must be validated across Android versions and devices.
