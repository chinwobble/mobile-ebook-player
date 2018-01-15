package com.example.benne.daisyapp2.service

import android.app.*
import android.content.*
import android.os.Bundle
import android.support.v4.media.*
import android.support.v4.media.session.*
import com.example.benne.daisyapp2.*
import com.example.benne.daisyapp2.data.daisy202.*
import com.example.benne.daisyapp2.di.*
import com.example.benne.daisyapp2.playback.*
import kotlinx.coroutines.experimental.*
import javax.inject.*

/**
 * Created by benne on 5/01/2018.
 */
class AudioService : MediaBrowserServiceCompat(),
    PlaybackManager.PlaybackServiceCallback {
    override fun onPlaybackStart() {
        _session.isActive = true

        //mDelayedStopHandler.removeCallbacksAndMessages(null)

        // The service needs to continue running even after the bound client (usually a
        // MediaController) disconnects, otherwise the music playback will stop.
        // Calling startService(Intent) will keep the service running until it is explicitly killed.
        startService(Intent(applicationContext, AudioService::class.java))
    }

    override fun onNotificationRequired() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPlaybackStop() {
        _session.isActive = false
        stopForeground(true)
    }

    override fun onPlaybackStateUpdated(newState: PlaybackStateCompat) {
        _session.setPlaybackState(newState)
    }

    private lateinit var _session: MediaSessionCompat

    @Inject lateinit var localPlayback: LocalPlayback
    @Inject lateinit var queueManager: QueueManager
    @Inject lateinit var mediaProvider: MediaProvider
    @Inject lateinit var playbackManager: PlaybackManager
    @Inject lateinit var mediaNotificationManager: MediaNotificationManager

    override fun onCreate() {
        super.onCreate()

        DaggerAudioServiceComponent
            .builder()
            .audioServiceModule(AudioServiceModule(this))
            .build()
            .inject(this)

        _session = MediaSessionCompat(this, "audioService")
        sessionToken = _session.sessionToken

        _session.setCallback(playbackManager.mediaSessionCallback)
        _session.setFlags(
            MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
            MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
        )

        queueManager.metadataUpdateListener = object : QueueManager.MetadataUpdateListener {
            override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
                _session.setMetadata(metadata)
            }

            override fun onMetadataRetrieveError() {
//                playbackManager!!.updatePlaybackState(
//                    getString(R.string.error_no_metadata))
            }

            override fun onCurrentQueueIndexUpdated(queueIndex: Int) {
                //playbackManager.handlePlayRequest()
            }

            override fun onQueueUpdated(title: String,
                                        newQueue: List<MediaSessionCompat.QueueItem>) {
                _session.setQueue(newQueue)
                _session.setQueueTitle(title)
            }
        }

        playbackManager.listener = this
        mediaNotificationManager.updateSessionToken()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // todo handle media buttons here
        return Service.START_STICKY
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

        launch(CommonPool) {
            val books = mediaProvider.asyncGetAllBooks()

            when {
                parentId == MEDIA_ROOT ->
                    result.sendResult(
                        books
                            .map { toMediaItem(it) }
                    )
            // parentId is a book
                books.any { it.toMediaId() == parentId } -> {
                    val book = books
                        .first { it.toMediaId() == parentId }
                    result.sendResult(
                        book.navElements
                            .map { toMediaItem(it) }
                    )
                }
            }
        }
        result.detach()

    }

    companion object {
        val MEDIA_ROOT = "mediaroot"
        val ELEMENT_TYPE_KEY = "element_type_key"
        val ELEMENT_TYPE_SUB_KEY = "element_type_sub_key"
    }
}