package com.example.benne.daisyapp2.playback

import android.os.*
import android.support.v4.media.session.*
import com.example.benne.daisyapp2.*
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.*
import javax.inject.*


/**
 * Created by benne on 5/01/2018.
 */
class PlaybackManager @Inject constructor(
    private val queueManager: QueueManager,
    private val localPlayback: LocalPlayback,
    private val mediaNotificationManager: MediaNotificationManager)
    : LocalPlayback.PlaybackListener {
    val mediaSessionCallback = MediaSessionCallback()
    var listener: PlaybackServiceCallback? = null

    private val availableActions: Long
        get() {
            var actions = PlaybackStateCompat.ACTION_PLAY_PAUSE or
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID or
                PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH or
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT
            actions = if (localPlayback.isPlaying) {
                actions or PlaybackStateCompat.ACTION_PAUSE
            } else {
                actions or PlaybackStateCompat.ACTION_PLAY
            }
            return actions
        }


    init {
        localPlayback.playbackListener = this
    }

    override suspend fun onComplete() {
        val clip = queueManager.asyncNextPlayableClip()
        if (clip != null) {
            queueManager.updateMetadata()
            localPlayback.play(clip)
            updatePlaybackState()
            mediaNotificationManager.startNotification()
        }
    }

    suspend fun handlePlayRequest() {
        val clip = queueManager.asyncCurrentClip()
        if (clip != null) {
            queueManager.updateMetadata()
            localPlayback.play(clip)
            updatePlaybackState()
            listener?.onPlaybackStart()
            mediaNotificationManager.startNotification()
        }

    }

    fun handlePauseRequest() {
        if (localPlayback.isPlaying) {
            localPlayback.pause()
            updatePlaybackState()
        }
    }

    fun handleStopRequest() {

    }

    private fun updatePlaybackState() {
        val position = PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN
        // todo implement position
        val state = localPlayback.state

        val stateBuilder = PlaybackStateCompat
            .Builder()
            .setActions(availableActions)
            .setState(state, 0, 1.0f)
            //.setActiveQueueItemId()
            // todo set custom actions
        this.listener?.onPlaybackStateUpdated(stateBuilder.build())
    }

    inner class MediaSessionCallback : MediaSessionCompat.Callback() {
        override fun onPlay() {
            launch (UI) {
                handlePlayRequest()
            }
        }

        override fun onPause() {
            handlePauseRequest()
        }

        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
            queueManager.currentQueueMediaId = mediaId!!
            launch (UI) {
                handlePlayRequest()
            }
        }
    }

    interface PlaybackServiceCallback {
        fun onPlaybackStart()

        fun onNotificationRequired()

        fun onPlaybackStop()

        fun onPlaybackStateUpdated(newState: PlaybackStateCompat)
    }
}