# Known limitations

- Initial source has not been compiled or exercised on an emulator or device. No performance, playback or security guarantees.
- No Gradle wrapper or CI. Dependency repositories are not reachable in the current sandbox.
- No licensed online catalogue, streaming integrations, download manager, backend, accounts, or discovery/recommendations.
- No playlist management, persistent queue/position, queue editor, full player, seek, shuffle, repeat, lyrics, equalizer, artwork, folders, scanning, history, settings, light theme or adaptive tablet design.
- Local import selects individual documents, not a directory. Metadata handling and revoked or unavailable files need more robust recovery. Format support depends on device and Media3 codecs.
- Favourites and URI references use basic SharedPreferences; indexing and metadata refresh do not scale to large libraries. No deletion UI yet.
- Notification, foreground service, external-controller authorization, audio focus and Bluetooth behavior must be validated across Android versions and devices.
