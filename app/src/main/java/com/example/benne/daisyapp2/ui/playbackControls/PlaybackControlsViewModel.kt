package com.example.benne.daisyapp2.ui.playbackControls

import androidx.lifecycle.*
import androidx.databinding.*
import com.example.benne.daisyapp2.MediaSessionConnection
import com.example.benne.daisyapp2.ui.bookList.BookListViewModel

/**
 * Created by benne on 19/02/2018.
 */
class PlaybackControlsViewModel(private val mediaSessionConnection: MediaSessionConnection)
    : ViewModel()
    , PlaybackControlsFragmentUserActionsListener {

    override fun onPlayPressed() {
        val a = mediaSessionConnection.nowPlaying.value
    }

    override fun onPausePressed() {

    }

    val PlayingBookText = ObservableField<String>()
    val PlayingSectionText = ObservableField<String>()

    val isPlaying = ObservableBoolean().also { it.set(true) }

    class Factory(private val mediaSessionConnection: MediaSessionConnection)
        : ViewModelProvider.NewInstanceFactory() {

        @Suppress("unchecked_cast")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return PlaybackControlsViewModel(mediaSessionConnection) as T
        }
    }
}