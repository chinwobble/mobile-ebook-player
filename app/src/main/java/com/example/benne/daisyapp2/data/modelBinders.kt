package com.example.benne.daisyapp2.data.daisy202

import android.os.*
import android.support.v4.media.*
import android.util.*
import com.example.benne.daisyapp2.data.*
import com.example.benne.daisyapp2.service.AudioService.Companion.ELEMENT_TYPE_KEY
import com.example.benne.daisyapp2.service.AudioService.Companion.ELEMENT_TYPE_SUB_KEY
import java.io.*
import java.nio.file.*

fun DaisyBook.toMediaId() = this.metadata.hashCode().toString()
fun NavElement.toMediaId() = this.hashCode().toString()

inline fun List<SmilAudioElement>.toPlayableClip(path: String): PlayableClip {
    val file = File(path, this.first().file)
    val clipStart = this.first().clipStart
    val clipEnd = this.last().clipEnd
    return PlayableClip(file, clipStart, clipEnd)
}

inline fun SmilAudioElement.toPlayableClip(path: String):PlayableClip {
    val file = File(path, this.file)
    val clipStart = this.clipStart
    val clipEnd = this.clipEnd
    return PlayableClip(file, clipStart, clipEnd)
}

fun toMediaMetadata(book: DaisyBook, nav: NavElement): MediaMetadataCompat {
    var subtitle = ""
    val isPageRef: Boolean
    when (nav) {
        is NavElement.PageReference -> {
            subtitle = nav.label
            isPageRef = true
        }
        is NavElement.HeadingReference -> {
            subtitle = nav.label
            isPageRef = false
        }
        else -> {
            isPageRef = false
        }
    }

    if (isPageRef || subtitle.toLowerCase() == "page break") {
        val indexInEntireNav = book.navElements
            .indexOfFirst { it.toMediaId() == nav.toMediaId() }

        // walk nav element until heading ref is found
        val heading = book.navElements
            .take(indexInEntireNav)
            .findLast { it is NavElement.HeadingReference } as NavElement.HeadingReference

        // get all the page references under this heading
        val pagesInSameHeading = book.navElements
            .dropWhile { it.toMediaId() != heading.toMediaId() }
            .drop(1) // drop the heading too
            .takeWhile { it is NavElement.PageReference }

        val indexInHeadingRef =
            pagesInSameHeading.indexOfFirst { it.toMediaId() == nav.toMediaId() }

        subtitle = "${heading.label} - ${indexInHeadingRef + 1} / ${pagesInSameHeading.count()}"

    }

    return MediaMetadataCompat.Builder()
        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, book.toMediaId())
        //.putString(MusicProviderSource.CUSTOM_METADATA_TRACK_SOURCE, source)
        //.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album)
        //.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
        //.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, durationMs)
        //.putString(MediaMetadataCompat.METADATA_KEY_GENRE, genre)
        //.putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, iconUrl)
        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, book.metadata.title)
        //.putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, trackNumber)
        //.putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, totalTrackCount)
        //.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, subtitle)
        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, subtitle)
        .build()
}

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
    val label: String

    when (nav) {
        is NavElement.PageReference -> {
            bundle.putString(ELEMENT_TYPE_KEY, nav.javaClass.canonicalName)
            bundle.putString(ELEMENT_TYPE_SUB_KEY, nav.pageType.toString())
            label = nav.label
        }
        is NavElement.HeadingReference -> {
            bundle.putString(ELEMENT_TYPE_KEY, nav.javaClass.canonicalName)
            bundle.putString(ELEMENT_TYPE_SUB_KEY, nav.level.toString())
            label = nav.label
        }
        else -> {
            label = ""
        }
    }

    val description = MediaDescriptionCompat
        .Builder()
        .setExtras(bundle)
        .setTitle(label)
        .setMediaId(nav.toMediaId())
        .setDescription(subtitle)
        .build()

    return MediaBrowserCompat.MediaItem(description, flags)
}
