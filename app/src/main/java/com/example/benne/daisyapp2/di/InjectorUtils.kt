package com.example.benne.daisyapp2.di

import android.content.ComponentName
import android.content.Context
import androidx.fragment.app.Fragment
import com.example.benne.daisyapp2.MediaSessionConnection
import com.example.benne.daisyapp2.playback.MediaService
import com.example.benne.daisyapp2.ui.bookDetails.BookDetailsFragmentArgs
import com.example.benne.daisyapp2.ui.bookDetails.BookDetailsViewModel
import com.example.benne.daisyapp2.viewModels.MainActivityViewModel
import com.example.benne.daisyapp2.ui.bookList.BookListViewModel
import com.example.benne.daisyapp2.ui.playbackControls.PlaybackControlsViewModel


/**
 * Static methods used to inject classes needed for various Activities and Fragments.
 */
object InjectorUtils {
    private fun provideMediaSessionConnection(context: Context): MediaSessionConnection {
        return MediaSessionConnection.getInstance(context,
                ComponentName(context, MediaService::class.java))
    }

    fun provideMainActivityViewModel(context: Context): MainActivityViewModel.Factory {
        val applicationContext = context.applicationContext
        val mediaSessionConnection = provideMediaSessionConnection(applicationContext)
        return MainActivityViewModel.Factory(mediaSessionConnection)
    }

    fun provideBookListViewFragmentViewModel(context: Context)
            : BookListViewModel.Factory {
        val applicationContext = context.applicationContext

        val mediaSessionConnection = provideMediaSessionConnection(applicationContext)
        return BookListViewModel.Factory(mediaSessionConnection)
    }

    fun providePlaybackControlsFragmentViewModel(context: Context)
            : PlaybackControlsViewModel.Factory {
        val applicationContext = context.applicationContext

        val mediaSessionConnection = provideMediaSessionConnection(applicationContext)
        return PlaybackControlsViewModel.Factory(mediaSessionConnection)
    }

    fun provideBookDetailsFragmentViewModel(fragment: Fragment)
            : BookDetailsViewModel.Factory {
        val applicationContext = fragment.requireContext().applicationContext
        val mediaId = BookDetailsFragmentArgs.fromBundle(fragment.requireArguments()).mediaId

        val mediaSessionConnection = provideMediaSessionConnection(applicationContext)
        return BookDetailsViewModel.Factory(mediaSessionConnection, mediaId)
    }
}