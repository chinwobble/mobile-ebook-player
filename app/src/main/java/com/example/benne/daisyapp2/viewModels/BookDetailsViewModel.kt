package com.example.benne.daisyapp2.viewModels

import android.arch.lifecycle.*
import android.support.v4.media.*
import com.example.benne.daisyapp2.*
import com.example.benne.daisyapp2.ui.bookList.*

/**
 * Created by benne on 10/01/2018.
 */
class BookDetailsViewModel : ViewModel() {

    val bookSections: LiveData<Iterable<MediaBrowserCompat.MediaItem>>

    val playSectionCommand = SingleLiveEvent<MediaBrowserCompat.MediaItem>()

    init {
        bookSections = MutableLiveData<Iterable<MediaBrowserCompat.MediaItem>>()
        bookSections.value = emptyList()
    }

    fun playSection(item: MediaBrowserCompat.MediaItem) {
        playSectionCommand.value = item
    }
    fun selectNavSection() {

    }
}