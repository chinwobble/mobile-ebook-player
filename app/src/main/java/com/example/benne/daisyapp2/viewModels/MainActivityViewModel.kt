package com.example.benne.daisyapp2.viewModels

import androidx.lifecycle.*
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import com.example.benne.daisyapp2.Event
import com.example.benne.daisyapp2.MediaSessionConnection


/**
 * Small [ViewModel] that watches a [MediaSessionConnection] to become connected
 * and provides the root/initial media ID of the underlying [MediaBrowserCompat].
 */
class MainActivityViewModel(private val mediaSessionConnection: MediaSessionConnection
) : ViewModel() {

    val rootMediaId: LiveData<String> =
            Transformations.map(mediaSessionConnection.isConnected) { isConnected ->
                if (isConnected) {
                    mediaSessionConnection.rootMediaId
                } else {
                    null
                }
            }

    /**
     * [navigateToMediaItem] acts as an "event", rather than state. [Observer]s
     * are notified of the change as usual with [LiveData], but only one [Observer]
     * will actually read the data. For more information, check the [Event] class.
     */
    val navigateToMediaItem: LiveData<Event<String>> get() = _navigateToMediaItem
    private val _navigateToMediaItem = MutableLiveData<Event<String>>()

    /**
     * This method takes a [MediaItemData] and routes it depending on whether it's
     * browsable (i.e.: it's the parent media item of a set of other media items,
     * such as an album), or not.
     *
     * If the item is browsable, handle it by sending an event to the Activity to
     * browse to it, otherwise play it.
     */
    fun mediaItemClicked(clickedItem: MediaBrowserCompat.MediaItem) {
        if (clickedItem.isBrowsable) {
            browseToItem(clickedItem)
        } else {
//            mediaSessionConnection.playMedia(clickedItem)
        }
    }

    /**
     * This posts a browse [Event] that will be handled by the
     * observer in [MainActivity].
     */
    private fun browseToItem(mediaItem: MediaBrowserCompat.MediaItem) {
        _navigateToMediaItem.value = Event(mediaItem.mediaId!!)
    }

    class Factory(private val mediaSessionConnection: MediaSessionConnection)
        : ViewModelProvider.NewInstanceFactory() {
        @Suppress("unchecked_cast")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainActivityViewModel(mediaSessionConnection) as T
        }
    }

    companion object {
        private const val TAG = "MainActivityVM"
    }
}

