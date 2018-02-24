package com.example.benne.daisyapp2.playback

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.Observer
import android.arch.lifecycle.OnLifecycleEvent
import android.content.ComponentName
import android.support.v4.media.MediaBrowserCompat
import com.example.benne.daisyapp2.MainActivity
import com.example.benne.daisyapp2.AudioService
import com.example.benne.daisyapp2.viewModels.MediaListViewModel
import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.support.v4.media.session.*
import android.support.v7.app.*
import com.example.benne.daisyapp2.AudioService.Companion.MEDIA_ROOT

/**
 * Created by benne on 5/01/2018.
 */
class MediaBrowserWrapper(
        private val activity: AppCompatActivity,
        private val viewModel: MediaListViewModel,
        private val ifeCycle: Lifecycle)
    : LifecycleObserver,
    MediaBrowserCompat.ConnectionCallback()
    {
    private lateinit var _mediaController: MediaControllerCompat
    private lateinit var _mediaBrowser: MediaBrowserCompat

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        _mediaBrowser = MediaBrowserCompat(
                activity,
                ComponentName(activity, AudioService::class.java),
                this,
                null)
//        _mediaBrowser = MediaBrowserCompat(this, this,)
        //_mediaBrowser!!.serviceComponent

        // val root = _mediaBrowser.root
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        _mediaBrowser.connect()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        _mediaBrowser.disconnect()
    }

    fun playSection(mediaId: String) {
        _mediaController
            .transportControls
            .playFromMediaId(mediaId, null)
    }

    fun getPLaybackState(): PlaybackStateCompat {
        return _mediaController.playbackState
    }

    fun pauseSection() {
        _mediaController
            .transportControls
            .pause()
    }

    override fun onConnected() {
        super.onConnected()
        val token = _mediaBrowser.sessionToken

        _mediaController = MediaControllerCompat(activity, token)
        
        MediaControllerCompat.setMediaController(activity, _mediaController)

        this.viewModel.currentSelection.observe(this.activity,
            Observer<String> { t ->
                if (!_mediaBrowser.isConnected) {
                    return@Observer
                }

                try {
                    _mediaBrowser.unsubscribe(t!!)
                } catch (ex: Exception) { }

                if (t == MEDIA_ROOT) {
                    viewModel.startRefreshing()
                    _mediaBrowser.subscribe(t, object : MediaBrowserCompat.SubscriptionCallback() {
                        override fun onChildrenLoaded(parentId: String, children: MutableList<MediaItem>) {
                            viewModel.updateItems(children)
                            viewModel.finishRefreshing()
                        }
                    })
                }
                else {
                    // check if current item is browsable
                    if (activity is MainActivity) {
                        _mediaBrowser.subscribe(t!!, activity.mediaBrowserCallBack)
                        viewModel.finishRefreshing()
                    }
                }
            }
        )
    }
}

