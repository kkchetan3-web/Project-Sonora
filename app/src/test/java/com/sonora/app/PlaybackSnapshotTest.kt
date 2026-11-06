package com.sonora.app

import org.junit.Assert.*
import org.junit.Test

class PlaybackSnapshotTest {
    @Test fun roundTripPreservesOrderAndState() {
        val snapshot = PlaybackSnapshot(listOf(SnapshotItem("content://one", "First", "A"), SnapshotItem("content://two", "Second", "B")), 1, 12345, 2, true)
        assertEquals(snapshot, PlaybackSnapshot.decode(snapshot.encode()))
    }

    @Test fun rejectsInvalidOrEmptySnapshots() {
        assertNull(PlaybackSnapshot.decode("not json"))
        assertNull(PlaybackSnapshot.decode("""{"items":[],"index":0}"""))
        assertNull(PlaybackSnapshot.decode("""{"items":[{"uri":"content://one"}],"index":1}"""))
    }

    @Test fun revokedGrantsAreRemovedWithoutChangingSurvivorOrder() {
        val items = listOf(SnapshotItem("a", "A", ""), SnapshotItem("b", "B", ""), SnapshotItem("c", "C", ""))
        val original = PlaybackSnapshot(items, 1, 456, 1, true)
        val result = original.withAvailableUris(setOf("a", "c"))!!
        assertEquals(listOf("a", "c"), result.items.map { it.uri })
        assertEquals(1, result.index)
        assertEquals(0L, result.positionMs)
        assertEquals(456L, original.withAvailableUris(setOf("b"))!!.positionMs)
        assertNull(original.withAvailableUris(emptySet()))
    }

    @Test fun clampsCorruptPositionAndRepeatMode() {
        val snapshot = PlaybackSnapshot.decode("""{"items":[{"uri":"content://one"}],"index":0,"position":-5,"repeat":99}""")!!
        assertEquals(0L, snapshot.positionMs)
        assertEquals(2, snapshot.repeatMode)
        assertFalse(snapshot.shuffle)
    }
}
