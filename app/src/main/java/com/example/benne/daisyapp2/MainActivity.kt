package com.example.benne.daisyapp2

import android.Manifest
import android.app.SearchManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import androidx.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.example.benne.daisyapp2.R
import com.example.benne.daisyapp2.databinding.ActivityMainBinding
import com.example.benne.daisyapp2.di.InjectorUtils
import com.example.benne.daisyapp2.playback.MediaService.Companion.MEDIA_ROOT
import com.example.benne.daisyapp2.viewModels.MainActivityViewModel
import com.google.android.material.R as AR

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainActivityViewModel

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        navController = Navigation.findNavController(this, R.id.navigation_fragment)
        //navController.navigate(R.id.fragment_book)
        setSupportActionBar(findViewById(R.id.toolbar))
        binding.navView.setupWithNavController(navController)
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

                        }

                    }
                })

        /**
         * Observe [MainActivityViewModel.navigateToMediaItem] for [Event]s indicating
         * the user has requested to browse to a different [MediaItemData].
         */
        viewModel.navigateToMediaItem.observe(this, Observer {
            it?.getContentIfNotHandled()?.let { mediaId ->
                Log.d(TAG, "unhandled get content")
                //navigateToMediaItem(mediaId)
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

    companion object {
        val TAG: String = MainActivity::class.java.simpleName
    }

}
