package com.example.benne.daisyapp2.ui

import android.support.v4.media.*
import com.example.benne.daisyapp2.dataBindings.*

/**
 * Created by benne on 13/01/2018.
 */
interface BookDetailsUserActionListener: UserActionListener {
    fun onPlaySection(item: MediaBrowserCompat.MediaItem)
}