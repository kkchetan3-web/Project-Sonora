package com.sonora.app

import android.os.Handler
import android.os.Looper
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

/** Owns playback independently of the activity; Media3 manages the media notification. */
@androidx.media3.common.util.UnstableApi
class PlaybackService : MediaSessionService() {
    private var session: MediaSession? = null
    private val handler = Handler(Looper.getMainLooper())
    private val checkpoint = object : Runnable {
        override fun run() {
            session?.player?.let { if (it.isPlaying) save(it) }
            handler.postDelayed(this, 5000)
        }
    }

    override fun onCreate() {
        super.onCreate()
        val player = ExoPlayer.Builder(this).build().apply {
            setAudioAttributes(AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).setContentType(C.AUDIO_CONTENT_TYPE_MUSIC).build(), true)
            setHandleAudioBecomingNoisy(true)
        }
        restore(player)
        player.addListener(object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) { save(player) }
        })
        session = MediaSession.Builder(this, player).setCallback(object : MediaSession.Callback {
            override fun onConnect(session: MediaSession, controller: MediaSession.ControllerInfo): MediaSession.ConnectionResult {
                if (controller.uid == applicationInfo.uid || session.isMediaNotificationController(controller)) {
                    return MediaSession.ConnectionResult.AcceptedResultBuilder(session).build()
                }
                if (!controller.isTrusted) return MediaSession.ConnectionResult.reject()
                // System/approved notification listeners may control playback, but not replace the library queue.
                val commands = MediaSession.ConnectionResult.DEFAULT_PLAYER_COMMANDS.buildUpon()
                    .remove(Player.COMMAND_SET_MEDIA_ITEM).remove(Player.COMMAND_CHANGE_MEDIA_ITEMS)
                    .remove(Player.COMMAND_SET_PLAYLIST_METADATA).build()
                return MediaSession.ConnectionResult.AcceptedResultBuilder(session).setAvailablePlayerCommands(commands).build()
            }
        }).build()
        handler.postDelayed(checkpoint, 5000)
    }

    private fun save(player: Player) {
        val prefs = getSharedPreferences("playback", MODE_PRIVATE)
        if (player.mediaItemCount == 0) { prefs.edit().remove("snapshot").apply(); return }
        val items = (0 until player.mediaItemCount).map { i ->
            val item = player.getMediaItemAt(i)
            SnapshotItem(item.mediaId, item.mediaMetadata.title?.toString() ?: "Unknown track", item.mediaMetadata.artist?.toString() ?: "Unknown artist")
        }
        val snapshot = PlaybackSnapshot(items, player.currentMediaItemIndex, player.currentPosition.coerceAtLeast(0), player.repeatMode, player.shuffleModeEnabled)
        prefs.edit().putString("snapshot", snapshot.encode()).apply()
    }

    private fun restore(player: ExoPlayer) {
        val saved = getSharedPreferences("playback", MODE_PRIVATE).getString("snapshot", null) ?: return
        val decoded = PlaybackSnapshot.decode(saved) ?: return
        // Only restore files for which this app still has an explicit persisted read grant.
        val grants = contentResolver.persistedUriPermissions.filter { it.isReadPermission }.map { it.uri.toString() }.toSet()
        val snapshot = decoded.withAvailableUris(grants) ?: return
        val items = snapshot.items.map { item ->
            MediaItem.Builder().setUri(item.uri).setMediaId(item.uri).setMediaMetadata(
                MediaMetadata.Builder().setTitle(item.title).setArtist(item.artist).build()).build()
        }
        player.setMediaItems(items, snapshot.index, snapshot.positionMs)
        player.repeatMode = snapshot.repeatMode
        player.shuffleModeEnabled = snapshot.shuffle
        player.prepare() // Prepared but paused; never automatically starts sound on restart.
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = session

    override fun onDestroy() {
        handler.removeCallbacks(checkpoint)
        session?.run { save(player); player.release(); release() }
        session = null
        super.onDestroy()
    }
}
