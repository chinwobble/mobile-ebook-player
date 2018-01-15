package com.example.benne.daisyapp2

import android.arch.lifecycle.*
import android.os.*
import android.support.v4.media.*
import com.example.benne.daisyapp2.playback.*
import com.example.benne.daisyapp2.service.AudioService.Companion.MEDIA_ROOT
import com.example.benne.daisyapp2.ui.*
import com.example.benne.daisyapp2.viewModels.*
import android.support.design.R as AR

class MainActivity
    : BaseActivity()
    {

    private lateinit var _mediaBrowserWrapper: MediaBrowserWrapper
    private lateinit var _viewModel: MediaListViewModel

    val mediaBrowserCallBack: MediaBrowserCompat.SubscriptionCallback =
        object : MediaBrowserCompat.SubscriptionCallback() {
            override fun onChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>) {
                navigateToBookDetails(children)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        _viewModel = ViewModelProviders.of(this).get(MediaListViewModel::class.java)

        super.onCreate(savedInstanceState)
        _mediaBrowserWrapper = MediaBrowserWrapper(
            this,
            _viewModel,
            lifecycle
        )

        lifecycle.addObserver(_mediaBrowserWrapper)
        if (_viewModel.currentSelection.value == MEDIA_ROOT) {
            navigateToBookList()
        }

//        _viewModel.currentSelection.observe(this,
//            Observer<String> {t ->
//                // save selected currentQueueMediaId
//                navigateToBookDetails()
//            })
    }

    fun navigateToBookDetails(children: MutableList<MediaBrowserCompat.MediaItem>) {
        val frag = supportFragmentManager.fragments.lastOrNull()
        if (frag != null && frag !is BookDetailsFragment) {

            val bookDetailsFragment = BookDetailsFragment()
            bookDetailsFragment.mediaItems = children
            bookDetailsFragment.mediaBrowserWrapper = _mediaBrowserWrapper

            supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                )
                .replace(
                    R.id.fragment_container_main,
                    bookDetailsFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    fun navigateToBookList() {
        val frag = supportFragmentManager.findFragmentByTag(BookListFragment.TAG)
        if (frag != null) {
            return
        }

//        if (fragment == null || !TextUtils.equals(fragment.currentQueueMediaId, currentQueueMediaId)) {
//            fragment = MediaBrowserFragment()
//            fragment.currentQueueMediaId = currentQueueMediaId
        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.fragment_container_main,
                BookListFragment(),
                BookListFragment.TAG)
            .commit()
        // If this is not the top level media (root), we add it to the fragment back stack,
        // so that actionbar toggle and Back will work appropriately:
//        if (currentQueueMediaId != null) {
//            transaction.addToBackStack(null)
//        }
    }

    interface MediaBrowserListener {
        val mediaBrowserCallBack: MediaBrowserCompat.SubscriptionCallback
    }

}
