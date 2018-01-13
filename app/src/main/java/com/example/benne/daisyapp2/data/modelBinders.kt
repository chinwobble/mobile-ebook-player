package com.example.benne.daisyapp2.data.daisy202

import android.os.*
import android.support.v4.media.*
import com.example.benne.daisyapp2.service.AudioService.Companion.ELEMENT_TYPE_KEY
import com.example.benne.daisyapp2.service.AudioService.Companion.ELEMENT_TYPE_SUB_KEY

fun DaisyBook.toMediaId() = this.hashCode().toString()
fun NavElement.toMediaId() = this.hashCode().toString()


fun toMediaItem(book: DaisyBook) : MediaBrowserCompat.MediaItem {
    val flags =
        MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
    val metadata = book.metadata
    val description = MediaDescriptionCompat
        .Builder()
        .setMediaId(book.toMediaId())
        .setTitle(metadata.title)
        .setDescription(metadata.creator)
        .build()

    return MediaBrowserCompat.MediaItem(description, flags)
}

fun toMediaItem(nav: NavElement) : MediaBrowserCompat.MediaItem {
    val flags =
        MediaBrowserCompat.MediaItem.FLAG_PLAYABLE

    val bundle = Bundle()

    val subtitle =
        if (nav is NavElement.PageReference)
            nav.pageType.toString()
        else
            ""

    when (nav) {
        is NavElement.PageReference -> {
            bundle.putString(ELEMENT_TYPE_KEY, nav.javaClass.canonicalName)
            bundle.putString(ELEMENT_TYPE_SUB_KEY, nav.pageType.toString())
        }
        is NavElement.HeadingReference -> {
            bundle.putString(ELEMENT_TYPE_KEY, nav.javaClass.canonicalName)
            bundle.putString(ELEMENT_TYPE_SUB_KEY, nav.level.toString())
        }
    }

    val description = MediaDescriptionCompat
        .Builder()
        .setExtras(bundle)
        .setTitle(nav.label)
        .setMediaId(nav.toMediaId())
        .setDescription(subtitle)
        .build()

    return MediaBrowserCompat.MediaItem(description, flags)
}
