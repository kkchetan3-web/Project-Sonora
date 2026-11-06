package com.sonora.app

import android.content.ComponentName
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private data class Track(val uri: String, val title: String, val artist: String) {
    fun mediaItem() = MediaItem.Builder().setUri(uri).setMediaId(uri)
        .setMediaMetadata(MediaMetadata.Builder().setTitle(title).setArtist(artist).build()).build()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SonoraScreen() }
    }

    private fun readTrack(uri: Uri): Track {
        var name = uri.lastPathSegment ?: "Unknown track"
        try {
            contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use {
                if (it.moveToFirst()) name = it.getString(0) ?: name
            }
        } catch (_: Exception) { }
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(this, uri)
            Track(uri.toString(), retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: name,
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: "Unknown artist")
        } catch (_: Exception) { Track(uri.toString(), name, "Unknown artist") }
        finally { retriever.release() }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable private fun SonoraScreen() {
        val prefs = remember { getSharedPreferences("library", MODE_PRIVATE) }
        var uris by remember { mutableStateOf(prefs.getStringSet("uris", emptySet())!!.toSet()) }
        var favorites by remember { mutableStateOf(prefs.getStringSet("favorites", emptySet())!!.toSet()) }
        var tracks by remember { mutableStateOf<List<Track>>(emptyList()) }
        var query by remember { mutableStateOf("") }
        var onlyFavorites by remember { mutableStateOf(false) }
        var controller by remember { mutableStateOf<MediaController?>(null) }
        var revision by remember { mutableIntStateOf(0) }
        var error by remember { mutableStateOf<String?>(null) }
        var showQueue by remember { mutableStateOf(false) }
        var showPlayer by remember { mutableStateOf(false) }
        var position by remember { mutableLongStateOf(0L) }
        LaunchedEffect(uris) { tracks = withContext(Dispatchers.IO) { uris.map { readTrack(Uri.parse(it)) }.sortedBy { it.title.lowercase() } } }
        DisposableEffect(Unit) {
            val future = MediaController.Builder(this@MainActivity, SessionToken(this@MainActivity, ComponentName(this@MainActivity, PlaybackService::class.java))).buildAsync()
            var active = true
            future.addListener({
                if (active) try {
                    controller = future.get().also { player ->
                        player.addListener(object : Player.Listener {
                            override fun onEvents(player: Player, events: Player.Events) { revision++ }
                            override fun onPlayerError(playbackException: androidx.media3.common.PlaybackException) { error = "Playback failed: ${playbackException.message ?: "unable to open file"}" }
                        })
                    }
                } catch (e: Exception) { error = "Could not connect to playback service." }
            }, mainExecutor)
            onDispose { active = false; MediaController.releaseFuture(future); controller = null }
        }
        val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { selected ->
            val added = selected.mapNotNull { uri ->
                try { contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION); uri.toString() }
                catch (_: SecurityException) { error = "Could not retain access to a selected file."; null }
            }
            uris = uris + added
            prefs.edit().putStringSet("uris", uris).apply()
        }
        val p = controller
        @Suppress("UNUSED_VARIABLE") val observedRevision = revision
        LaunchedEffect(p) {
            while (p != null) { position = p.currentPosition.coerceAtLeast(0); delay(500) }
        }
        val current = p?.currentMediaItem
        val colors = darkColorScheme(primary = Color(0xFFC8F36B), background = Color(0xFF101411), surface = Color(0xFF1B221C), onSurface = Color(0xFFE8EDE4))
        MaterialTheme(colorScheme = colors) {
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(horizontal = 20.dp)) {
                    Spacer(Modifier.height(24.dp))
                    Text("SONORA", color = colors.primary, letterSpacing = 4.sp, style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.height(18.dp))
                    Text("Your music,\nyour space.", style = MaterialTheme.typography.headlineLarge, lineHeight = 40.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("A home for the music you own.", color = Color(0xFFAFB9AC))
                    Spacer(Modifier.height(20.dp))
                    Button(onClick = { picker.launch(arrayOf("audio/*")) }, shape = RoundedCornerShape(12.dp)) { Icon(Icons.Default.Add, null); Spacer(Modifier.width(8.dp)); Text("Import audio") }
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth(), singleLine = true, label = { Text("Search your library") }, leadingIcon = { Icon(Icons.Default.Search, null) })
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Text("LIBRARY · ${tracks.size}", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium, letterSpacing = 2.sp)
                        FilterChip(selected = onlyFavorites, onClick = { onlyFavorites = !onlyFavorites }, label = { Text("Favourites") })
                    }
                    error?.let { Text(it, color = colors.error); TextButton(onClick = { error = null }) { Text("Dismiss") } }
                    val visible = tracks.filter { (!onlyFavorites || it.uri in favorites) && (it.title.contains(query, true) || it.artist.contains(query, true)) }
                    if (tracks.isEmpty()) Text("Import audio files to start listening offline.", modifier = Modifier.weight(1f).padding(top = 24.dp), color = Color(0xFFAFB9AC))
                    else if (visible.isEmpty()) Text("No matching tracks.", modifier = Modifier.weight(1f).padding(top = 24.dp))
                    else LazyColumn(Modifier.weight(1f)) {
                        items(visible, key = { it.uri }) { track ->
                            Row(Modifier.fillMaxWidth().clickable(enabled = p != null) {
                                p?.setMediaItems(visible.map { it.mediaItem() }, visible.indexOf(track), 0L)
                                p?.prepare(); p?.play()
                            }.padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Surface(shape = RoundedCornerShape(10.dp), color = Color(0xFF344635), modifier = Modifier.size(48.dp)) { Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.MusicNote, contentDescription = null, tint = colors.primary) } }
                                Column(Modifier.weight(1f).padding(start = 12.dp)) { Text(track.title, maxLines = 1); Text(track.artist, color = Color(0xFFAFB9AC), style = MaterialTheme.typography.bodySmall, maxLines = 1) }
                                IconButton(enabled = p != null, onClick = {
                                    if (p != null) {
                                        if (p.mediaItemCount == 0) { p.addMediaItem(track.mediaItem()); p.prepare() }
                                        else p.addMediaItem(p.currentMediaItemIndex + 1, track.mediaItem())
                                    }
                                }) { Icon(Icons.Default.QueuePlayNext, "Play ${track.title} next") }
                                IconButton(enabled = p != null, onClick = { if (p != null) { val wasEmpty = p.mediaItemCount == 0; p.addMediaItem(track.mediaItem()); if (wasEmpty) p.prepare() } }) { Icon(Icons.Default.PlaylistAdd, "Add ${track.title} to queue") }
                                IconButton(onClick = {
                                    favorites = if (track.uri in favorites) favorites - track.uri else favorites + track.uri
                                    prefs.edit().putStringSet("favorites", favorites).apply()
                                }) { Icon(if (track.uri in favorites) Icons.Default.Favorite else Icons.Default.FavoriteBorder, if (track.uri in favorites) "Remove from favourites" else "Add to favourites", tint = colors.primary) }
                            }
                        }
                    }
                    if (current != null && p != null) {
                        HorizontalDivider(color = Color(0xFF394239))
                        Row(Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f).clickable { showPlayer = true }) { Text(current.mediaMetadata.title?.toString() ?: "Unknown track", maxLines = 1); Text(current.mediaMetadata.artist?.toString() ?: "Unknown artist", style = MaterialTheme.typography.bodySmall, color = Color(0xFFAFB9AC)) }
                            IconButton(onClick = { showQueue = true }) { Icon(Icons.Default.QueueMusic, "Open queue") }
                            IconButton(onClick = { p.seekToPreviousMediaItem() }) { Icon(Icons.Default.SkipPrevious, "Previous track") }
                            IconButton(onClick = { if (p.isPlaying) p.pause() else p.play() }) { Icon(if (p.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, if (p.isPlaying) "Pause" else "Play", tint = colors.primary) }
                            IconButton(onClick = { p.seekToNextMediaItem() }) { Icon(Icons.Default.SkipNext, "Next track") }
                        }
                    }
                }
            }
            if (p != null && current != null && showPlayer) {
                AlertDialog(onDismissRequest = { showPlayer = false }, title = { Text(current.mediaMetadata.title?.toString() ?: "Now playing") },
                    text = {
                        Column {
                            Text(current.mediaMetadata.artist?.toString() ?: "Unknown artist")
                            val duration = p.duration.takeIf { it > 0 && it != androidx.media3.common.C.TIME_UNSET } ?: 0L
                            Slider(value = if (duration > 0) (position.toFloat() / duration).coerceIn(0f, 1f) else 0f,
                                onValueChange = { if (duration > 0) { position = (it * duration).toLong(); p.seekTo(position) } }, enabled = duration > 0)
                            Text("${formatTime(position)} / ${if (duration > 0) formatTime(duration) else "--:--"}")
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { p.shuffleModeEnabled = !p.shuffleModeEnabled }) { Icon(Icons.Default.Shuffle, if (p.shuffleModeEnabled) "Shuffle on" else "Shuffle off", tint = if (p.shuffleModeEnabled) colors.primary else colors.onSurface) }
                                IconButton(onClick = { p.seekToPreviousMediaItem() }) { Icon(Icons.Default.SkipPrevious, "Previous track") }
                                IconButton(onClick = { if (p.isPlaying) p.pause() else p.play() }) { Icon(if (p.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, if (p.isPlaying) "Pause" else "Play") }
                                IconButton(onClick = { p.seekToNextMediaItem() }) { Icon(Icons.Default.SkipNext, "Next track") }
                                IconButton(onClick = { p.repeatMode = (p.repeatMode + 1) % 3 }) { Icon(if (p.repeatMode == Player.REPEAT_MODE_ONE) Icons.Default.RepeatOne else Icons.Default.Repeat, "Repeat: ${when (p.repeatMode) { Player.REPEAT_MODE_ONE -> "one"; Player.REPEAT_MODE_ALL -> "all"; else -> "off" }}", tint = if (p.repeatMode != Player.REPEAT_MODE_OFF) colors.primary else colors.onSurface) }
                            }
                        }
                    }, confirmButton = { TextButton(onClick = { showPlayer = false; showQueue = true }) { Text("Queue") } }, dismissButton = { TextButton(onClick = { showPlayer = false }) { Text("Close") } })
            }
            if (p != null && showQueue) {
                AlertDialog(onDismissRequest = { showQueue = false }, title = { Text("Playback queue") }, text = {
                    Column {
                        Text("Changes apply immediately.", style = MaterialTheme.typography.bodySmall)
                        LazyColumn(Modifier.heightIn(max = 400.dp)) {
                            items(p.mediaItemCount) { index ->
                                val item = p.getMediaItemAt(index)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(item.mediaMetadata.title?.toString() ?: "Unknown track", modifier = Modifier.weight(1f), maxLines = 1, color = if (index == p.currentMediaItemIndex) colors.primary else colors.onSurface)
                                    IconButton(enabled = index > 0, onClick = { p.moveMediaItem(index, index - 1) }) { Icon(Icons.Default.KeyboardArrowUp, "Move up") }
                                    IconButton(enabled = index < p.mediaItemCount - 1, onClick = { p.moveMediaItem(index, index + 1) }) { Icon(Icons.Default.KeyboardArrowDown, "Move down") }
                                    IconButton(onClick = { p.removeMediaItem(index) }) { Icon(Icons.Default.Close, "Remove from queue") }
                                }
                            }
                        }
                    }
                }, confirmButton = { TextButton(onClick = { showQueue = false }) { Text("Done") } }, dismissButton = { TextButton(onClick = { p.clearMediaItems(); showQueue = false }) { Text("Clear queue") } })
            }
        }
    }

    private fun formatTime(ms: Long): String {
        val seconds = ms.coerceAtLeast(0) / 1000
        return "%d:%02d".format(seconds / 60, seconds % 60)
    }
}
