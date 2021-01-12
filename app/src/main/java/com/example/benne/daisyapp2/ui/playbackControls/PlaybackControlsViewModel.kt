package com.example.benne.daisyapp2.ui.playbackControls

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE
import androidx.lifecycle.*
import androidx.databinding.*
import com.example.benne.daisyapp2.MediaSessionConnection
import androidx.lifecycle.Transformations
import com.example.benne.daisyapp2.ui.bookList.BookListViewModel

/**
 * Created by benne on 19/02/2018.
 */
class PlaybackControlsViewModel(val mediaSessionConnection: MediaSessionConnection)
    : ViewModel()
    , PlaybackControlsFragmentUserActionsListener {

    override fun onPlayPressed() {
        val title = mediaSessionConnection.nowPlaying.value?.getString(MediaMetadataCompat.METADATA_KEY_TITLE)
        val subtitle = mediaSessionConnection.nowPlaying.value?.getString(METADATA_KEY_DISPLAY_SUBTITLE)

        val a = mediaSessionConnection.nowPlaying.value
    }

    override fun onPausePressed() {

    }

    //    val playing: LiveData<String> get() =

    val playingBookText = ObservableField<String>()

    val playingSectionText: LiveData<String> = Transformations.map(mediaSessionConnection.nowPlaying) {
        it?.getString(MediaMetadataCompat.METADATA_KEY_TITLE) ?: ""
    }


    val isPlaying = ObservableBoolean(true)

    class Factory(private val mediaSessionConnection: MediaSessionConnection)
        : ViewModelProvider.NewInstanceFactory() {

        @Suppress("unchecked_cast")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return PlaybackControlsViewModel(mediaSessionConnection) as T
        }
    }
}