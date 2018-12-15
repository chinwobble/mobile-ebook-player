package com.example.benne.daisyapp2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserServiceCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import com.example.benne.daisyapp2.data.daisy202.toMediaId
import com.example.benne.daisyapp2.data.daisy202.toMediaItem
import com.example.benne.daisyapp2.di.AudioServiceModule
import com.example.benne.daisyapp2.di.DaggerAudioServiceComponent
import com.example.benne.daisyapp2.playback.MediaProvider
import com.example.benne.daisyapp2.playback.PlaybackManager
import com.example.benne.daisyapp2.playback.QueueManager
import javax.inject.Inject

/**
 * Created by benne on 5/01/2018.
 */
class AudioService : MediaBrowserServiceCompat(),
    PlaybackManager.PlaybackServiceCallback {
    override fun onPlaybackStart() {
        becomingNoisyReceiver.register()
        _session.isActive = true

        //mDelayedStopHandler.removeCallbacksAndMessages(null)

        // The service needs to continue running even after the bound client (usually a
        // MediaController) disconnects, otherwise the music playback will stop.
        // Calling startService(Intent) will keep the service running until it is explicitly killed.
        startService(Intent(applicationContext, AudioService::class.java))
    }

    override fun onNotificationRequired() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPlaybackStop() {
        _session.isActive = false
        becomingNoisyReceiver.unregister()
        stopForeground(true)
    }

    override fun onPlaybackStateUpdated(newState: PlaybackStateCompat) {
        _session.setPlaybackState(newState)
    }

    private lateinit var _session: MediaSessionCompat
    private lateinit var becomingNoisyReceiver: BecomingNoisyReceiver

    @Inject lateinit var queueManager: QueueManager
    @Inject lateinit var mediaProvider: MediaProvider
    @Inject lateinit var playbackManager: PlaybackManager
    @Inject lateinit var mediaNotificationManager: MediaNotificationManager

    override fun onCreate() {
        super.onCreate()

        DaggerAudioServiceComponent
            .builder()
            .audioServiceModule(AudioServiceModule(this))
            .build()
            .inject(this)

        playbackManager.audioManager = this.getSystemService(AudioManager::class.java)

        _session = MediaSessionCompat(this, "audioService")
        _session.isActive = true
        sessionToken = _session.sessionToken

        _session.setCallback(playbackManager.mediaSessionCallback)
        _session.setFlags(
            MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
            MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
        )

        becomingNoisyReceiver = BecomingNoisyReceiver(this, _session.sessionToken)
        queueManager.metadataUpdateListener = object : QueueManager.MetadataUpdateListener {
            override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
                _session.setMetadata(metadata)
            }

            override fun onMetadataRetrieveError() {
//                playbackManager!!.updatePlaybackState(
//                    getString(R.string.error_no_metadata))
            }

            override fun onCurrentQueueIndexUpdated(queueIndex: Int) {
                //playbackManager.handlePlayRequest()
            }

            override fun onQueueUpdated(title: String,
                                        newQueue: List<MediaSessionCompat.QueueItem>) {
                _session.setQueue(newQueue)
                _session.setQueueTitle(title)
            }
        }

        playbackManager.listener = this
        mediaNotificationManager.updateSessionToken()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // todo handle media buttons here
        Log.d(TAG, "start command ${intent?.action}")
        return super.onStartCommand(intent, flags, startId)
        //return Service.START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        _session.release()
    }

    override fun onGetRoot(clientPackageName: String,
                           clientUid: Int,
                           rootHints: Bundle?): BrowserRoot? {
        Log.d(TAG, "get root ")
        return MediaBrowserServiceCompat.BrowserRoot(MEDIA_ROOT, null)
    }

    override fun onLoadChildren(parentId: String,
                                result: Result<List<MediaBrowserCompat.MediaItem>>) {
        Log.d(TAG, "onChildLoad $parentId")

        val books = mediaProvider.asyncGetAllBooks()

        when {
            parentId == MEDIA_ROOT -> {
                Log.d(TAG, "sending results for $parentId ${books.count()}")
                result.sendResult(
                        books
                                .map { toMediaItem(it) }
                )
            }
            // parentId is a book
            books.any { it.toMediaId() == parentId } -> {
                Log.d(TAG, "sending results for $parentId ${books.count()}")
                val book = books
                        .first { it.toMediaId() == parentId }
                result.sendResult(
                        book.navElements
                                .map { toMediaItem(it) }
                )
            }
        }
    }

    companion object {
        private val TAG: String = AudioService::class.java.simpleName
        val MEDIA_ROOT = "mediaroot"
        val ELEMENT_TYPE_KEY = "element_type_key"
        val ELEMENT_TYPE_SUB_KEY = "element_type_sub_key"
    }
}

private class BecomingNoisyReceiver (private val context: Context,
    sessionToken: MediaSessionCompat.Token)
    : BroadcastReceiver() {

        private val noisyIntentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        private val controller = MediaControllerCompat(context, sessionToken)

        private var registered = false

        fun register() {
            if (!registered) {
                context.registerReceiver(this, noisyIntentFilter)
                registered = true
            }
        }

        fun unregister() {
            if (registered) {
                context.unregisterReceiver(this)
                registered = false
            }
        }

        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
                controller.transportControls.pause()
            }
        }
    }