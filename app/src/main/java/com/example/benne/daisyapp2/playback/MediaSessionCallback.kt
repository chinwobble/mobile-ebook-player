package com.example.benne.daisyapp2.playback

import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat

class MediaSessionCallback : MediaSessionCompat.Callback()  {

    override fun onCustomAction(action: String?, extras: Bundle?) {
        super.onCustomAction(action, extras)
    }
}