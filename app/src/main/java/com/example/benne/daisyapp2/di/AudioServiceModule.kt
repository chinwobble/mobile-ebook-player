package com.example.benne.daisyapp2.di

import android.content.*
import com.example.benne.daisyapp2.*
import com.example.benne.daisyapp2.playback.*
import dagger.*
import com.example.benne.daisyapp2.service.*
import javax.inject.*

/**
 * Created by benne on 13/01/2018.
 */
@Module
class AudioServiceModule(private val audioService: AudioService) {

    @Provides
    @Singleton
    fun provideApplicationContext(): Context =
        audioService.applicationContext

    @Provides
    @Singleton
    fun provideMediaNotificationManager(): MediaNotificationManager =
        MediaNotificationManager(audioService)

    @Provides
    @Singleton
    fun provideQueueManager(
        mediaProvider: MediaProvider
    ): QueueManager =
        QueueManager(mediaProvider)

    @Provides
    @Singleton
    fun providePlaybackManager(
        queueManager: QueueManager,
        localPlayback: LocalPlayback,
        mediaNotificationManager: MediaNotificationManager
    ): PlaybackManager =
        PlaybackManager(queueManager, localPlayback, mediaNotificationManager)

    @Provides
    @Singleton
    fun provideLocalPlayback(context: Context) =
        LocalPlayback(context)
}
