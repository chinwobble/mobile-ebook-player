package com.example.benne.daisyapp2.ui.bookDetails

import android.support.v4.media.MediaBrowserCompat

data class BookSection(
        val section: MediaBrowserCompat.MediaItem,
        val pages: List<MediaBrowserCompat.MediaItem>
)