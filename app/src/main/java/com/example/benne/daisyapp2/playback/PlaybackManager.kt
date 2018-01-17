package com.example.benne.daisyapp2.playback

import android.os.*
import android.support.v4.media.session.*
import android.util.*
import com.example.benne.daisyapp2.*
import com.example.benne.daisyapp2.data.*
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
        clip?.let { playClip(it) }
    }

    suspend fun handlePlayRequest() {
        val clip = queueManager.asyncCurrentClip()
        clip?.let { playClip(it) }
    }

    private fun playClip(clip: PlayableClip) {
        queueManager.updateMetadata()
        localPlayback.play(clip)
    }

    suspend fun handleSkipToNextRequest() {
        Log.w("PlayableManager", "handling next")
        val clip = queueManager.asyncNextPlayableClip()
        Log.w("PlayableManager", "handling next clip: $clip")
        clip?.let { playClip(it) }
    }

    suspend fun handleSkipToPreviousRequest() {
        Log.w("PlayableManager", "handling previous")
        val clip = queueManager.asyncPreviousPlayableClip()
        Log.w("PlayableManager", "handling previous clip: $clip")
        clip?.let { playClip(it) }
    }

    fun handlePauseRequest() {
        if (localPlayback.isPlaying) {
            localPlayback.pause()
        }
    }

    fun handleStopRequest() {

    }

    override fun onLocalPlaybackStateChanged(playbackStateCompat: Int) {
        // todo implement position
        val position = PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN

        val stateBuilder = PlaybackStateCompat
            .Builder()
            .setActions(availableActions)
            .setState(playbackStateCompat, 0, 1.0f)
            //.setActiveQueueItemId()
            // todo set custom actions
        this.listener?.onPlaybackStateUpdated(stateBuilder.build())

        when (playbackStateCompat) {
            PlaybackStateCompat.STATE_PLAYING -> {
                listener?.onPlaybackStart()
                mediaNotificationManager.startNotification()
            }
            PlaybackStateCompat.STATE_STOPPED -> {
                listener?.onPlaybackStop()
            }
            PlaybackStateCompat.STATE_PAUSED -> {
            }
        }
    }

    inner class MediaSessionCallback : MediaSessionCompat.Callback() {
        override fun onPlay() {

            launch(CommonPool)  {
                handlePlayRequest()
            }
        }

        override fun onPause() {
            launch(CommonPool) {
                handlePauseRequest()
            }
        }

        override fun onSkipToNext() {
            launch  {
                handleSkipToNextRequest()
            }
        }

        override fun onSkipToPrevious() {
            launch {
                handleSkipToPreviousRequest()
            }
        }

        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
            queueManager.currentQueueMediaId = mediaId!!
            launch(UI) {
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