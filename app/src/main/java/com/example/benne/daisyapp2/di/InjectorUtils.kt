package com.example.benne.daisyapp2.di

import android.content.ComponentName
import android.content.Context
import com.example.benne.daisyapp2.MediaSessionConnection
import com.example.benne.daisyapp2.playback.MediaService
import com.example.benne.daisyapp2.ui.bookDetails.BookDetailsViewModel
import com.example.benne.daisyapp2.viewModels.MainActivityViewModel
import com.example.benne.daisyapp2.ui.bookList.BookListViewModel


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

    fun provideBookDetailsFragmentViewModel(context: Context, mediaId: String)
            : BookDetailsViewModel.Factory {
        val applicationContext = context.applicationContext

        val mediaSessionConnection = provideMediaSessionConnection(applicationContext)
        return BookDetailsViewModel.Factory(mediaSessionConnection, mediaId)
    }
}