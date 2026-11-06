package com.sonora.app

import android.content.ComponentName
import android.net.Uri
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class PlaybackInstrumentedTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private lateinit var controller: MediaController
    private lateinit var file: File
    private lateinit var scenario: ActivityScenario<MainActivity>

    private fun main(action: () -> Unit) = instrumentation.runOnMainSync(action)
    private fun await(condition: () -> Boolean) {
        val until = System.currentTimeMillis() + 15000
        while (System.currentTimeMillis() < until) {
            var done = false
            main { done = condition() }
            if (done) return
            Thread.sleep(100)
        }
        var detail = ""
        if (::controller.isInitialized) main {
            detail = "state=${controller.playbackState}, isPlaying=${controller.isPlaying}, playWhenReady=${controller.playWhenReady}, suppression=${controller.playbackSuppressionReason}, error=${controller.playerError?.stackTraceToString()}"
        }
        fail("Timed out waiting for player state: $detail")
    }

    @Before fun setup() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
        file = File(context.cacheDir, "test-tone.wav")
        val samples = 44100 * 5
        val b = ByteBuffer.allocate(44 + samples * 2).order(ByteOrder.LITTLE_ENDIAN)
        b.put("RIFF".toByteArray()); b.putInt(36 + samples * 2); b.put("WAVEfmt ".toByteArray())
        b.putInt(16); b.putShort(1); b.putShort(1); b.putInt(44100); b.putInt(88200); b.putShort(2); b.putShort(16)
        b.put("data".toByteArray()); b.putInt(samples * 2)
        file.writeBytes(b.array())
        val future = MediaController.Builder(context, SessionToken(context, ComponentName(context, PlaybackService::class.java))).buildAsync()
        controller = future.get(20, TimeUnit.SECONDS)
        main { controller.clearMediaItems(); controller.repeatMode = Player.REPEAT_MODE_OFF; controller.shuffleModeEnabled = false }
    }

    @After fun teardown() {
        if (::controller.isInitialized) main { controller.clearMediaItems(); controller.release() }
        if (::scenario.isInitialized) scenario.close()
        if (::file.isInitialized) file.delete()
    }

    private fun item(id: String) = MediaItem.Builder().setMediaId(id).setUri(Uri.fromFile(file)).build()

    @Test fun playbackAndQueueControlsOperateOnRealMedia3Session() {
        main {
            controller.setMediaItems(listOf(item("a"), item("b")))
            controller.addMediaItem(1, item("next"))
            controller.moveMediaItem(2, 1)
            assertEquals(listOf("a", "b", "next"), (0 until controller.mediaItemCount).map { controller.getMediaItemAt(it).mediaId })
            controller.removeMediaItem(2)
            controller.shuffleModeEnabled = true
            controller.repeatMode = Player.REPEAT_MODE_ALL
            controller.prepare()
            controller.play()
        }
        await { controller.playbackState == Player.STATE_READY && controller.isPlaying }
        main { controller.seekTo(2000) }
        await { controller.currentPosition >= 2000 }
        main {
            assertTrue(controller.shuffleModeEnabled)
            assertEquals(Player.REPEAT_MODE_ALL, controller.repeatMode)
            controller.pause()
        }
        await { !controller.isPlaying }
        main { controller.seekToNextMediaItem() }
        await { controller.currentMediaItemIndex == 1 }
        main { controller.seekToPreviousMediaItem() }
        await { controller.currentMediaItemIndex == 0 }
        main { controller.clearMediaItems() }
        await { controller.mediaItemCount == 0 }
    }

    @Test fun corruptAudioReportsPlayerErrorInsteadOfCrashing() {
        val bad = File(context.cacheDir, "bad-audio.wav")
        bad.writeText("not an audio file")
        try {
            main { controller.setMediaItem(MediaItem.fromUri(Uri.fromFile(bad))); controller.prepare(); controller.play() }
            await { controller.playerError != null }
        } finally { bad.delete() }
    }

    @Test fun activityRecreationKeepsServicePlayback() {
        main { controller.setMediaItem(item("recreate")); controller.prepare(); controller.play() }
        await { controller.isPlaying }
        scenario.recreate()
        await { controller.isPlaying && controller.currentMediaItem?.mediaId == "recreate" }
    }
}
