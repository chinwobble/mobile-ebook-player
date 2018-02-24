package com.example.benne.daisyapp2.viewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.*
import android.support.v4.media.MediaBrowserCompat.MediaItem
import com.example.benne.daisyapp2.AudioService.Companion.MEDIA_ROOT
import com.example.benne.daisyapp2.ui.bookList.*

/**
 * Created by benne on 5/01/2018.
 */
class MediaListViewModel
    : ViewModel()
    , BookListUserActionListener {

    override fun onBookListRefresh() {
        startRefreshing()
    }

    val currentSelection: LiveData<String>
    val children: ObservableList<MediaItem> = ObservableArrayList()
    val listRefreshing: ObservableBoolean

    init {
        currentSelection = MutableLiveData<String>()
        currentSelection.value = MEDIA_ROOT
        listRefreshing = ObservableBoolean()
            .also { it.set(false) }

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
        with(this.children) {
            clear()
            addAll(children)
            //empty.set(isEmpty())
        }
    }
}
