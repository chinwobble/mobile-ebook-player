package com.example.benne.daisyapp2.di

import android.content.ComponentName
import android.content.Context
import com.example.benne.daisyapp2.AudioService
import com.example.benne.daisyapp2.MediaSessionConnection
import com.example.benne.daisyapp2.viewModels.BookDetailsViewModel
import com.example.benne.daisyapp2.viewModels.MainActivityViewModel
import com.example.benne.daisyapp2.viewModels.MediaListViewModel


/**
 * Static methods used to inject classes needed for various Activities and Fragments.
 */
object InjectorUtils {
    private fun provideMediaSessionConnection(context: Context): MediaSessionConnection {
        return MediaSessionConnection.getInstance(context,
                ComponentName(context, AudioService::class.java))
    }

    fun provideMainActivityViewModel(context: Context): MainActivityViewModel.Factory {
        val applicationContext = context.applicationContext
        val mediaSessionConnection = provideMediaSessionConnection(applicationContext)
        return MainActivityViewModel.Factory(mediaSessionConnection)
    }

    fun provideBookListViewFragmentViewModel(context: Context)
            : MediaListViewModel.Factory {
        val applicationContext = context.applicationContext

        val mediaSessionConnection = provideMediaSessionConnection(applicationContext)
        return MediaListViewModel.Factory(mediaSessionConnection)
    }

    fun provideBookDetailsFragmentViewModel(context: Context, mediaId: String)
            : BookDetailsViewModel.Factory {
        val applicationContext = context.applicationContext

        val mediaSessionConnection = provideMediaSessionConnection(applicationContext)
        return BookDetailsViewModel.Factory(mediaSessionConnection, mediaId)
    }
}