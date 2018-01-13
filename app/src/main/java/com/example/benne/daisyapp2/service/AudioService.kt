package com.example.benne.daisyapp2.service

import android.os.Bundle
import android.support.v4.media.*
import android.support.v4.media.session.MediaSessionCompat
import com.example.benne.daisyapp2.data.daisy202.*
import com.example.benne.daisyapp2.io.*
import com.example.benne.daisyapp2.playback.*

/**
 * Created by benne on 5/01/2018.
 */
class AudioService : MediaBrowserServiceCompat() {
    private lateinit var _session: MediaSessionCompat
    private lateinit var _books: List<DaisyBook>


    override fun onCreate() {
        super.onCreate()
        _session = MediaSessionCompat(this, "audioService")
        sessionToken = _session.sessionToken

        val queueManager = QueueManager()
        val localPlayback = LocalPlayback(applicationContext)
        _session.setCallback(MediaSessionCallbacks())
        _session.setFlags(
            MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
            MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
        )

        val fs = FileService()
        _books = fs.getDaisyBooks()
    }

    override fun onDestroy() {
        super.onDestroy()
        _session.release()
    }

    override fun onGetRoot(clientPackageName: String,
                           clientUid: Int,
                           rootHints: Bundle?): BrowserRoot? {
        return MediaBrowserServiceCompat.BrowserRoot("100", null)
    }

    override fun onLoadChildren(parentId: String,
                                result: Result<List<MediaBrowserCompat.MediaItem>>) {
        when {
            parentId == MEDIA_ROOT ->
                result.sendResult(
                    _books
                        .map { toMediaItem(it) }
                )
            // parentId is a book
            _books.firstOrNull { it.toMediaId() == parentId } != null -> {
                val book = _books
                    .first { it.toMediaId() == parentId }
                result.sendResult(
                    book.navElements
                        .map { toMediaItem(it) }
                )
            }
        }
    }

    companion object {
        val MEDIA_ROOT = "mediaroot"
        val ELEMENT_TYPE_KEY = "element_type_key"
        val ELEMENT_TYPE_SUB_KEY = "element_type_sub_key"
    }
}