# Public feature research

Research date: 2026-11-06. This is a public-documentation review, not a hands-on audit or an exhaustive study of every market, account tier, device, release or experiment. Spotify can change its UI and terms. No proprietary code, assets, endpoints or protected media were used.

## Version and experience
Third-party [Uptodown](https://spotify.en.uptodown.com/android/download) reported Android 9.1.94.1929 on 2026-11-03, while [APKMirror](https://www.apkmirror.com/apk/spotify-ab/) lists other builds including 9.2.0.26. These are not proof of the latest universally available stable Google Play build: rollouts, regions and beta channels differ. Confirm the Play Store listing on target devices before a parity study.

[Now Playing](https://support.spotify.com/us/article/now-playing/) documents save, shuffle, repeat, queue and Connect; the desktop view can show artist information and upcoming tracks. [Local files](https://support.spotify.com/us/article/local-files/) documents Android and desktop enablement and constraints on local-file crossfade. [Lyrics](https://support.spotify.com/kr-en/article/lyrics/) documents scrolling to lyrics on mobile/tablet, fullscreen and sharing, with rights/device/market variability. [Equalizer](https://support.spotify.com/us/article/equalizer/) notes Android device effects may affect other apps. [Crossfade](https://support.spotify.com/us/article/tracks-transitions/) is documented as Premium. [Recent activity](https://support.spotify.com/us/article/recent-activity/) notes the desktop queue shows the last 50 songs. [Keyboard shortcuts](https://support.spotify.com/ca-en/article/keyboard-shortcuts/) illustrate desktop-specific interaction. Do not extrapolate mobile screens from desktop or assume availability everywhere. Visual layouts, animations, battery usage and tablet behaviour need direct, authorized device study; no device measurements were performed.

## Feature-by-feature implementation matrix

Each row specifies observed or documented experience, Sonora requirements/dependencies, independent feasibility, rights and planned approach. “Rights” refers to third-party rights beyond the user's own files; metadata does not grant audio rights. All rows are plans unless identified otherwise in the roadmap.

| Feature / public experience | Technical requirements and dependencies | Independent? / rights | Sonora strategy |
|---|---|---|---|
| Home / personalised shelves | Library and preference aggregation; responsive UI | Yes; catalogue rights for streamed items | Local recent/favourite shelves, source-backed shelves later |
| Search / autocomplete | Indexed metadata, debouncing; optional provider search | Yes; provider API terms | Local search first, suggestions and provider adapters later |
| Library / sort / filters | Persistent database and indexing | Yes; no third-party rights for owned files | Room entities and sortable collections |
| Playlist / folders / editing | Ordered persistent track references | Yes; provider export/import subject to terms | Local Room playlists and folders; validate imports |
| Album / artist pages | Normalized metadata, artwork, navigation | Yes; artwork/metadata licences matter | Group local files; optional licensed metadata |
| Player / artwork / animation | Playback state, metadata, UI; responsive Compose | Yes; artwork must be authorized | Original full-screen view; no copied artwork |
| Queue / shuffle / repeat / seek | Player timeline, state, persistence | Yes | Media3 queue and explicit editing controls |
| Background / lock screen / notification / headphones | MediaSessionService, foreground service, audio focus | Yes; Android OS support | Media3 service and controller; device-test lifecycle |
| Gapless / crossfade / speed | Codec and timeline support; crossfade needs overlapping audio pipelines | Yes; source conditions may restrict processing | Media3 gapless where supported; independently implement and test crossfade and speed |
| Output / Bluetooth / handoff / casting | Android routing; remote receiver protocol and network | Bluetooth yes; casting SDK and receiver terms; handoff needs service | Android routing first; no claim of Spotify Connect interoperability |
| Audio settings / equalizer / bass / balance | Device audio effects, session IDs, DSP | Yes; device support varies | System equalizer first, own DSP only after testing |
| ReplayGain / quality / visualization | Tags or analysis, gain DSP, audio session/data | Yes; source format and licences constrain | Apply verified gain; display actual source quality, not invented lossless |
| Lyrics | Timed text display and matching | User text yes; provider licence for commercial lyrics | User-provided or properly licensed only |
| Recommendations / similar / mood / weekly mixes | Local features, history, scoring, optional licensed metadata | Yes; metadata terms apply | Opt-in local ranking, explain signals, no silent telemetry |
| New / trending / genre discovery | Fresh source catalog and ranking | Yes with authorized source; API/licence constraints | Per-provider adapter; never fabricate charts |
| Downloads / offline | Durable storage, source policy, expiry handling | Owned files yes; external sources require offline rights | Local files first, download only when explicitly authorized |
| Local import / formats / metadata / folders | SAF, document grants, codecs and tag parser | Yes for authorized user files | Persist grants; test MP3, FLAC, AAC, WAV, OGG, Opus and M4A on devices |
| History / listening summaries | Durable events and aggregation | Yes; explicit consent for behavioural tracking | Opt-in history with deletion and clear controls |
| Profile / settings / themes | Local preferences and accessible adaptive layout | Yes | Local-only settings; dark/light and accessibility testing |
| Accessibility / tablet / desktop / performance | Semantics, focus, contrast, adaptive breakpoints, instrumentation | Yes; platform-specific work | Compose adaptive UI; benchmark battery/memory and test assistive tech |

## Engineering evidence
[Media3](https://developer.android.com/media/media3) describes ExoPlayer, MediaSessionService and MediaController. Android documents [background audio hardening](https://developer.android.com/about/versions/17/changes/bg-audio), making real-device lifecycle testing important. Spotify's [2026 developer access update](https://developer.spotify.com/blog/2026-02-06-update-on-developer-access-and-platform-security) limits Development Mode and notes the postponed endpoint changes for existing integrations. Its [migration guide](https://developer.spotify.com/documentation/web-api/tutorials/february-2026-migration-guide) details endpoint changes. Spotify's [policy notes](https://developer.spotify.com/documentation/web-api/reference/get-the-users-currently-playing-track) disallow commercial streaming integrations; public metadata access is not a recording licence.
