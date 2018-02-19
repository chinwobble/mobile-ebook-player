package com.example.benne.daisyapp2.viewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.*
import android.support.v4.media.MediaBrowserCompat.MediaItem
import com.example.benne.daisyapp2.AudioService.Companion.MEDIA_ROOT

/**
 * Created by benne on 5/01/2018.
 */
class MediaListViewModel : ViewModel() {
    val currentSelection: LiveData<String>
    val children: LiveData<List<MediaItem>>
    val listRefreshing: ObservableBoolean

    init {
        currentSelection = MutableLiveData<String>()
        currentSelection.value = MEDIA_ROOT
        children = MutableLiveData<List<MediaItem>>()
        listRefreshing = ObservableBoolean()
            .also { it.set(false) }
        children.value = emptyList()
    }

    fun startRefreshing() {
        listRefreshing.set(true)
        listRefreshing.notifyChange()
    }

    fun finishRefreshing() {
        listRefreshing.set(false)
        listRefreshing.notifyChange()
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
