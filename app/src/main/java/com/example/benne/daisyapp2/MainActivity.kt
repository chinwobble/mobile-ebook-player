package com.example.benne.daisyapp2

import android.Manifest
import android.app.SearchManager
import androidx.lifecycle.Observer
import android.content.Intent
import android.content.pm.PackageManager
import androidx.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.benne.daisyapp2.R
import com.example.benne.daisyapp2.databinding.ActivityMainBinding
import com.example.benne.daisyapp2.di.InjectorUtils
import com.example.benne.daisyapp2.playback.MediaService.Companion.MEDIA_ROOT
import com.example.benne.daisyapp2.viewModels.MainActivityViewModel
import com.google.android.material.R as AR

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainActivityViewModel> {
        InjectorUtils.provideMainActivityViewModel(this)
    }

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        navController = Navigation.findNavController(this, R.id.navigation_fragment)
        //navController.navigate(R.id.fragment_book)
        setSupportActionBar(findViewById(R.id.toolbar))
        drawerLayout = binding.drawerLayout
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

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
                Log.d(TAG, "unhandled get content $mediaId")
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

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
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
                if (!grantResults.any()) {
                    return;
                }

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
