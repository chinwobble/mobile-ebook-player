package com.example.benne.daisyapp2.viewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.MediaItem
import com.example.benne.daisyapp2.service.AudioService.Companion.MEDIA_ROOT

/**
 * Created by benne on 5/01/2018.
 */
class MediaListViewModel : ViewModel() {
    val currentSelection: LiveData<String>
    val children: LiveData<List<MediaItem>>

    init {
        currentSelection = MutableLiveData<String>()
        currentSelection.value = MEDIA_ROOT
        children = MutableLiveData<List<MediaItem>>()
        children.value = emptyList()
    }

    fun setSelectedItem(mediaId: String) {
        val mutableCurrent = currentSelection as MutableLiveData<String>
        mutableCurrent.postValue(mediaId)
    }

    fun updateItems(children: Iterable<MediaItem>) {
        val mutableChildren = this.children as MutableLiveData<Iterable<MediaItem>>
        mutableChildren.postValue(children)
    }
}
