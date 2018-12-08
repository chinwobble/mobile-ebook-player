package com.example.benne.daisyapp2

import android.Manifest
import android.app.SearchManager
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import com.example.benne.daisyapp2.AudioService.Companion.MEDIA_ROOT
import com.example.benne.daisyapp2.di.InjectorUtils
import com.example.benne.daisyapp2.ui.bookDetails.BookDetailsFragment
import com.example.benne.daisyapp2.ui.bookList.BookListFragment
import com.example.benne.daisyapp2.viewModels.MainActivityViewModel
import javax.net.ssl.ManagerFactoryParameters
import android.support.design.R as AR
import com.example.benne.daisyapp2.R

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        viewModel = ViewModelProviders
                .of(this, InjectorUtils.provideMainActivityViewModel(this))
                .get(MainActivityViewModel::class.java)

        /**
         * Observe changes to the [MainActivityViewModel.rootMediaId]. When the app starts,
         * and the UI connects to [MusicService], this will be updated and the app will show
         * the initial list of media items.
         */
        viewModel.rootMediaId.observe(this,
                Observer<String> { rootMediaId ->
                    if (rootMediaId != null) {
                        if (rootMediaId == MEDIA_ROOT) {
                            navigateToBookList()
                        }
                        //Log.d(TAG, "root media switched")
                        //navigateToMediaItem(rootMediaId)
                    }
                })

        /**
         * Observe [MainActivityViewModel.navigateToMediaItem] for [Event]s indicating
         * the user has requested to browse to a different [MediaItemData].
         */
        viewModel.navigateToMediaItem.observe(this, Observer {
            it?.getContentIfNotHandled()?.let { mediaId ->
                Log.d(TAG, "unhandled get content")
                navigateToMediaItem(mediaId)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        val readStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (readStoragePermission == PackageManager.PERMISSION_GRANTED) {
            // todo
        }
        if (readStoragePermission == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= 23) {
                requestPermissions(arrayOf(
                        Manifest.permission.WAKE_LOCK,
                        Manifest.permission.READ_EXTERNAL_STORAGE), 0)
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        if (Intent.ACTION_SEARCH == intent?.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            Log.d(TAG, "search query received $query")

        }
        super.onNewIntent(intent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            0 -> {
                val readStorageResult = grantResults.first()
                if (readStorageResult == PackageManager.PERMISSION_GRANTED) {
                    // todo handle
                } else {
                    // todo(handle deny case)
                }
            }
        }
    }

    fun navigateToMediaItem(mediaId: String) {
        Log.d(TAG, "navigate to media item $mediaId")
        val frag = supportFragmentManager.fragments.lastOrNull()
        //if (frag != null && frag !is BookDetailsFragment) {
            val bookDetailsFragment = BookDetailsFragment.newInstance(mediaId)
            //bookDetailsFragment.mediaItems = children
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
        //}
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

    companion object {
        val TAG: String = MainActivity::class.java.simpleName
    }

}
