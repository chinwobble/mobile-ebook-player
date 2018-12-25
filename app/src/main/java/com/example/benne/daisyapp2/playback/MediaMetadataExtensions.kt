package com.example.benne.daisyapp2.playback

import android.support.v4.media.MediaBrowserCompat

const val MEDIA_DESCRIPTION_GROUP: String = "MEDIA_DESCRIPTION_GROUP"

inline val MediaBrowserCompat.MediaItem.GroupId
        get() = this.description.extras!!.getString(MEDIA_DESCRIPTION_GROUP)
