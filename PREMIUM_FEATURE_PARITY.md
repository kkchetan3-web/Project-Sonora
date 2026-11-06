# Premium feature parity matrix

Status definitions: **Not started** = no implementation; **In development** = partial or unvalidated implementation; **Implemented** = complete implementation but not yet tested; **Tested** = tested but verification criteria incomplete; **Verified** = acceptance criteria actually passed on specified devices. A feature is not marked Implemented solely because UI or a dependency exists. No feature is currently verified. Public experience and regional caveats: [research](SPOTIFY_FEATURE_RESEARCH.md).

| Module | Feature | Status | Dependency / gap |
|---|---|---|---|
| A | Play/pause, previous/next, local queue playback | In development | Media3 service and controls exist; build/device validation pending |
| A | Shuffle, repeat, seek, queue editing, play next, add to queue | In development | Media3 controller UI; validation pending |
| A | Queue/position restoration | In development | Local snapshots; process death/device tests pending |
| A | Smart shuffle, autoplay, crossfade, transitions, normalization, hide/explicit, video switching | Not started | Recommendations, DSP, metadata or video and rights |
| B | MP3, FLAC, WAV, AAC, M4A, OGG, Opus, ALAC playback | In development | Media3/device decoder compatibility untested; no format guarantee |
| B | Quality profiles, adaptive streaming, bitrate/format display, lossless | Not started | Source-provided variants, actual pipeline measurement |
| B | EQ, presets, bass/treble, normalization, DAC | Not started | Device effects or tested DSP |
| B/I | Audio focus, Bluetooth, media session, background/notification/lock screen | In development | Media3 wiring exists; Android/device tests pending |
| C | Local file offline playback | In development | Retained SAF grants and device testing |
| C | Downloads, progress, retries, quality, storage, cleanup, offline backup | Not started | Provider offline rights, durable worker and storage management |
| D | Discovery, radio, personalized feeds, mixes, taste profile, exclusions | Not started | Authorized catalog and opt-in recommendation signals |
| E | AI DJ, text/voice requests, AI playlists, mood, explanations, refresh, controls | Not started | Optional transparent provider or local model, user consent; no paid service configured |
| F | Favourites and search | In development | Local preferences; validation pending |
| F | Create, rename, delete playlists; add/remove tracks | In development | Local persistence; validation pending |
| F | Album/artist collections, artwork, sorting, import/export, history, following, sharing, collaboration | Not started | Database, metadata, provider rights and/or backend |
| G | Lyrics, translation, credits, biography, album and track info | Not started | User-supplied or licensed data; attribution and offline rights |
| H | Sessions, QR invites, collaboration, profiles, follows, messaging, activity, privacy | Not started | Secure backend, identity, synchronization, consent and abuse controls |
| I | Android Auto, smartwatch, remote devices, transfer, discovery, widgets | Not started | MediaLibraryService or device integrations and testing |
| J | Authorized videos, position synchronization, artwork animation | Not started | Licensed video or user-provided content, video player |
| K | Podcasts, audiobooks, chapters, bookmarks, speed, sleep timer | Not started | Authorized feeds/catalogues, durable progress and rights |
| L | Listening statistics and reports | Not started | Explicit opt-in history and local aggregation |
| M | Local title/artist search | In development | Basic substring filtering only |
| M | Universal search, autocomplete, history, trending, filters, fuzzy matching | Not started | Local index and provider data |
| N | Registration, accounts, settings, themes, privacy, deletion/export | Not started | Local preference UI or secure backend as appropriate |

All SONORA features are intended to be available without an artificial SONORA subscription. Third-party provider conditions can still constrain access and must be disclosed.
