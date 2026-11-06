# Known limitations

- The source built and unit tests passed in GitHub Actions; it has not been exercised on an emulator or device. No performance, playback or security guarantees.
- A pinned wrapper and CI workflow completed a build on GitHub Actions; the sandbox itself lacks Java/SDK and cannot reach the dependency repositories.
- No licensed online catalogue, streaming integrations, download manager, backend, accounts, or discovery/recommendations.
- No playlist management, full-screen player, lyrics, equalizer, artwork, folders, scanning, history, settings, light theme or adaptive tablet design. Player and queue dialogs and playback snapshot restoration are implemented in source but untested. Snapshots checkpoint every five seconds during playback, so an abrupt termination can lose recent progress; revoked grants are filtered from restored snapshots, but deleted or corrupt documents with retained grants can still cause playback failures. Media3 system playback resumption after process death is not implemented.
- Local import selects individual documents, not a directory. Metadata handling and revoked or unavailable files need more robust recovery. Format support depends on device and Media3 codecs.
- Favourites and URI references use basic SharedPreferences; indexing and metadata refresh do not scale to large libraries. No deletion UI yet.
- Notification, foreground service, external-controller authorization (including notification listener and Bluetooth interoperability), audio focus and Bluetooth behavior must be validated across Android versions and devices.
