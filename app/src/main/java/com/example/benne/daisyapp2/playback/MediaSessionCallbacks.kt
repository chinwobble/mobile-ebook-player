package com.example.benne.daisyapp2.playback

import android.os.*
import android.support.v4.media.session.*

/**
 * Created by benne on 13/01/2018.
 */
class MediaSessionCallbacks : MediaSessionCompat.Callback() {

    override fun onPlay() {

        super.onPlay()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
        super.onPlayFromMediaId(mediaId, extras)
    }
}