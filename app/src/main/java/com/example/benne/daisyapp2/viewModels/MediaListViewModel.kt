package com.example.benne.daisyapp2.viewModels

import android.app.*
import android.arch.lifecycle.*
import android.content.*
import android.databinding.*
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.util.Log
import com.example.benne.daisyapp2.AudioService.Companion.MEDIA_ROOT
import com.example.benne.daisyapp2.MediaSessionConnection
import com.example.benne.daisyapp2.playback.*
import com.example.benne.daisyapp2.ui.bookList.*
import java.lang.Exception

/**
 * Created by benne on 5/01/2018.
 */
class MediaListViewModel(mediaSessionConnection: MediaSessionConnection)
    : ViewModel()
    , BookListUserActionListener {

    override fun onBookListRefresh() {
        listRefreshing.postValue(false)
        try {
            mediaSessionConnection.unsubscribe(MEDIA_ROOT, this.subscriptionCallback)
        } catch (e: Exception) {

        }
        mediaSessionConnection.subscribe(MEDIA_ROOT, this.subscriptionCallback)
        //startRefreshing()
    }

    private val subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(parentId: String, children: List<MediaItem>) {
            this@MediaListViewModel.children.postValue(children)

            Log.d(TAG, "children loaded for parent $parentId items: ${children.count()}")

//            val itemsList = children.map { child ->
//                MediaItemData(child.mediaId!!,
//                        child.description.title.toString(),
//                        child.description.subtitle.toString(),
//                        child.description.iconUri!!,
//                        child.isBrowsable,
//                        getResourceForMediaId(child.mediaId!!))
//            }
//            _mediaItems.postValue(itemsList)
        }
    }

    private val mediaSessionConnection = mediaSessionConnection.also {
        it.subscribe(MEDIA_ROOT, subscriptionCallback)
        // todo
        // it.playbackState.observeForever(playbackStateObserver)
        // it.nowPlaying.observeForever(mediaMetadataObserver)
    }

    val currentSelection: MutableLiveData<String>
    val children: MutableLiveData<List<MediaItem>>
    val listRefreshing: MutableLiveData<Boolean>

    init {
        currentSelection = MutableLiveData<String>()
        children = MutableLiveData<List<MediaItem>>().also {
            it.value = listOf()
        }
        listRefreshing = MutableLiveData<Boolean>().also {
            it.value = false
        }
        currentSelection.value = MEDIA_ROOT

    }

    fun setSelectedItem(mediaId: String) {
        val mutableCurrent = currentSelection as MutableLiveData<String>
        mutableCurrent.postValue(mediaId)
    }

    fun updateItems(children: Iterable<MediaItem>) {
        with(this.children) {
//            clear()
//            addAll(children)
            //empty.set(isEmpty())
        }
    }

    class Factory(private val mediaSessionConnection: MediaSessionConnection
    ) : ViewModelProvider.NewInstanceFactory() {

        @Suppress("unchecked_cast")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MediaListViewModel(mediaSessionConnection) as T
        }
    }
    companion object {
        val TAG: String = "BookListViewModel"
    }
}
