package com.example.benne.daisyapp2.ui.bookDetails

import android.arch.lifecycle.*
import android.os.Bundle
import android.support.v4.media.*
import android.util.Log
import com.example.benne.daisyapp2.*
import com.example.benne.daisyapp2.playback.GroupId

/**
 * Created by benne on 10/01/2018.
 */
class BookDetailsViewModel (
        private val mediaSessionConnection: MediaSessionConnection,
        mediaId: String)
    : ViewModel() {

    var bookMediaId: String? = null
    val bookSections: MutableLiveData<List<MediaBrowserCompat.MediaItem>>
            = MutableLiveData<List<MediaBrowserCompat.MediaItem>>()
                .also {
                    it.value = listOf()
                }

    val sections: MediatorLiveData<List<BookSection>>
            = MediatorLiveData<List<BookSection>>()
            .also {sections ->
                sections.value = listOf()
                sections.addSource(bookSections, { elements ->
                    elements?.groupBy { it.GroupId }
                            ?.map {
                                val section = elements.find { x -> x.GroupId == it.key }!!
                                BookSection(section, it.value.filter { it != section })
                            }.also { sections.value = it }
                }
                )
            }

    private val subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(parentId: String, children: List<MediaBrowserCompat.MediaItem>) {
            Log.d(TAG, "children loaded for parent $parentId items: ${children.count()}")
            bookSections.postValue(children)
        }
    }

    init {
        mediaSessionConnection.subscribe(mediaId, subscriptionCallback)
    }

    fun playSection(item: MediaBrowserCompat.MediaItem) {
        val bundle = Bundle()
        bundle.putString("bookMediaId", bookMediaId)
        mediaSessionConnection.playMedia(item, bundle)
    }

    fun selectNavSection() {

    }

    class Factory(private val mediaSessionConnection: MediaSessionConnection,
                  private val mediaId: String
    ) : ViewModelProvider.NewInstanceFactory() {

        @Suppress("unchecked_cast")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return BookDetailsViewModel(mediaSessionConnection, mediaId) as T
        }
    }

    companion object {
        val TAG: String = BookDetailsViewModel::class.java.simpleName
    }
}