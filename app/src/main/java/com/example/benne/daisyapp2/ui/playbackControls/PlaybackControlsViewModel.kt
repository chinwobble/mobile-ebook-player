package com.example.benne.daisyapp2.ui.playbackControls

import android.arch.lifecycle.*
import android.databinding.*

/**
 * Created by benne on 19/02/2018.
 */
class PlaybackControlsViewModel
    : ViewModel()
    , PlaybackControlsFragmentUserActionsListener {

    override fun onPlayPressed() {

    }

    override fun onPausePressed() {

    }

    val PlayingBookText = ObservableField<String>()
    val PlayingSectionText = ObservableField<String>()

    val isPlaying = ObservableBoolean().also { it.set(true) }
}