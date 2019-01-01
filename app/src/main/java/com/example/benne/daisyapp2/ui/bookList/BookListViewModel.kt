package com.example.benne.daisyapp2.ui.bookList

import androidx.lifecycle.*
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.util.Log
import com.example.benne.daisyapp2.MediaSessionConnection
import com.example.benne.daisyapp2.playback.MediaService.Companion.MEDIA_ROOT
import java.lang.Exception

/**
 * Created by benne on 5/01/2018.
 */
class BookListViewModel(mediaSessionConnection: MediaSessionConnection)
    : ViewModel() {

    fun onBookListRefresh() {
        try {
            mediaSessionConnection.unsubscribe(MEDIA_ROOT, this.subscriptionCallback)
        } catch (e: Exception) {

        }
        mediaSessionConnection.subscribe(MEDIA_ROOT, this.subscriptionCallback)
        //startRefreshing()
    }

    private val subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(parentId: String, children: List<MediaItem>) {
            this@BookListViewModel.children.postValue(children)
            this@BookListViewModel.listRefreshing.postValue(false)
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

    val currentSelection: MutableLiveData<String> = MutableLiveData()
    val children: MutableLiveData<List<MediaItem>> = MutableLiveData<List<MediaItem>>().also {
        it.value = listOf()
    }
    val listRefreshing: MutableLiveData<Boolean> = MutableLiveData<Boolean>().also {
        it.value = false
    }

    init {
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

    class Factory(private val mediaSessionConnection: MediaSessionConnection)
        : ViewModelProvider.NewInstanceFactory() {

        @Suppress("unchecked_cast")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return BookListViewModel(mediaSessionConnection) as T
        }
    }
    companion object {
        val TAG: String = BookListViewModel::class.java.simpleName
    }
}
