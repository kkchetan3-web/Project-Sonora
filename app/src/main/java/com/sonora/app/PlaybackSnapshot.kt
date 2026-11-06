package com.sonora.app

import org.json.JSONArray
import org.json.JSONObject

/** A local snapshot only: restoring must never resume playback without user action. */
internal data class SnapshotItem(val uri: String, val title: String, val artist: String)
internal data class PlaybackSnapshot(val items: List<SnapshotItem>, val index: Int, val positionMs: Long, val repeatMode: Int, val shuffle: Boolean) {
    fun encode(): String = JSONObject().apply {
        put("items", JSONArray().apply { items.forEach { put(JSONObject().put("uri", it.uri).put("title", it.title).put("artist", it.artist)) } })
        put("index", index); put("position", positionMs); put("repeat", repeatMode); put("shuffle", shuffle)
    }.toString()

    companion object {
        fun decode(json: String): PlaybackSnapshot? = try {
            val root = JSONObject(json)
            val array = root.getJSONArray("items")
            require(array.length() in 1..10000)
            val items = (0 until array.length()).map { i ->
                val item = array.getJSONObject(i)
                SnapshotItem(item.getString("uri"), item.optString("title", "Unknown track"), item.optString("artist", "Unknown artist"))
            }
            val index = root.getInt("index")
            require(index in items.indices)
            PlaybackSnapshot(items, index, root.optLong("position", 0).coerceAtLeast(0), root.optInt("repeat", 0).coerceIn(0, 2), root.optBoolean("shuffle", false))
        } catch (_: Exception) { null }
    }
}
