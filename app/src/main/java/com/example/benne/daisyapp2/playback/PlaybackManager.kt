package com.example.benne.daisyapp2.playback

import android.os.*
import android.support.v4.media.session.MediaSessionCompat

/**
 * Created by benne on 5/01/2018.
 */
class PlaybackManager(
    val queueManager: QueueManager) {
    val mediaSessionCallback = MediaSessionCallback()

    fun onComplete() {

    }

    inner class MediaSessionCallback : MediaSessionCompat.Callback() {
        override fun onPlay() {
            super.onPlay()

        }



        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
            queueManager.setPlayQueue()

        }
    }
}