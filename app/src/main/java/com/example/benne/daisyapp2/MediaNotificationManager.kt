package com.example.benne.daisyapp2

import android.app.*
import android.content.*
import android.graphics.*
import android.os.*
import android.support.annotation.*
import android.support.v4.app.NotificationCompat
import android.support.v4.media.*
import android.support.v4.media.app.NotificationCompat.MediaStyle
import android.support.v4.media.session.*
import android.util.*
import com.example.benne.daisyapp2.service.*
import com.example.benne.daisyapp2.R
import javax.inject.*
import android.R as AR
/**
 * Keeps track of a notification and updates it automatically for a given
 * MediaSession. Maintaining a visible notification (usually) guarantees that the music service
 * won't be killed during playback.
 */
class MediaNotificationManager
@Inject constructor(private val mService: AudioService) : BroadcastReceiver() {
    private var mSessionToken: MediaSessionCompat.Token? = null
    private var mController: MediaControllerCompat? = null
    private var mTransportControls: MediaControllerCompat.TransportControls? = null

    private var mPlaybackState: PlaybackStateCompat? = null
    private var mMetadata: MediaMetadataCompat? = null

    private val mNotificationManager: NotificationManager = mService.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val mPlayIntent: PendingIntent
    private val mPauseIntent: PendingIntent
    private val mPreviousIntent: PendingIntent
    private val mNextIntent: PendingIntent
    private val mStopIntent: PendingIntent

    private val mStopCastIntent: PendingIntent

    //private val mNotificationColor: Int

    private var mStarted = false

    private val mCb = object : MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat) {
            mPlaybackState = state
            Log.d(TAG, "Received new playback state$state")
            if (state.state == PlaybackStateCompat.STATE_STOPPED || state.state == PlaybackStateCompat.STATE_NONE) {
                stopNotification()
            } else {
                val notification = createNotification()
                if (notification != null) {
                    mNotificationManager.notify(NOTIFICATION_ID, notification)
                }
            }
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            mMetadata = metadata
            Log.d(TAG, "Received new metadata $metadata")
            val notification = createNotification()
            if (notification != null) {
                mNotificationManager.notify(NOTIFICATION_ID, notification)
            }
        }

        override fun onSessionDestroyed() {
            super.onSessionDestroyed()
            Log.d(TAG, "Session was destroyed, resetting to the new session token")
            try {
                updateSessionToken()
            } catch (e: RemoteException) {
                Log.e(TAG, "could not connect media controller", e)
            }
        }
    }

    init {
//        mNotificationColor = ResourceHelper.getThemeColor(mService, R.attr.colorPrimary,
//            Color.DKGRAY)

        val pkg = mService.packageName
        mPauseIntent = PendingIntent.getBroadcast(
            mService,
            REQUEST_CODE,
            Intent(ACTION_PAUSE).setPackage(pkg),
            PendingIntent.FLAG_CANCEL_CURRENT)
        mPlayIntent = PendingIntent.getBroadcast(
            mService,
            REQUEST_CODE,
            Intent(ACTION_PLAY).setPackage(pkg),
            PendingIntent.FLAG_CANCEL_CURRENT)
        mPreviousIntent = PendingIntent.getBroadcast(
            mService, REQUEST_CODE,
            Intent(ACTION_PREV).setPackage(pkg),
            PendingIntent.FLAG_CANCEL_CURRENT)
        mNextIntent = PendingIntent.getBroadcast(
            mService,
            REQUEST_CODE,
            Intent(ACTION_NEXT).setPackage(pkg),
            PendingIntent.FLAG_CANCEL_CURRENT)
        mStopIntent = PendingIntent.getBroadcast(
            mService,
            REQUEST_CODE,
            Intent(ACTION_STOP).setPackage(pkg),
            PendingIntent.FLAG_CANCEL_CURRENT)
        mStopCastIntent = PendingIntent.getBroadcast(
            mService, REQUEST_CODE,
            Intent(ACTION_STOP_CASTING).setPackage(pkg),
            PendingIntent.FLAG_CANCEL_CURRENT)

        // Cancel all notifications to handle the case where the Service was killed and
        // restarted by the system.
        mNotificationManager.cancelAll()
    }

    /**
     * Posts the notification and starts tracking the session to keep it
     * updated. The notification will automatically be removed if the session is
     * destroyed before [.stopNotification] is called.
     */
    fun startNotification() {
        if (!mStarted) {
            mMetadata = mController!!.metadata
            mPlaybackState = mController!!.playbackState

            // The notification must be updated after setting started to true
            val notification = createNotification()
            if (notification != null) {
                mController!!.registerCallback(mCb)
                val filter = IntentFilter()
                filter.addAction(ACTION_NEXT)
                filter.addAction(ACTION_PAUSE)
                filter.addAction(ACTION_PLAY)
                filter.addAction(ACTION_PREV)
                filter.addAction(ACTION_STOP_CASTING)
                mService.registerReceiver(this, filter)

                mService.startForeground(NOTIFICATION_ID, notification)
                mStarted = true
            }
        }
    }

    /**
     * Removes the notification and stops tracking the session. If the session
     * was destroyed this has no effect.
     */
    fun stopNotification() {
        if (mStarted) {
            mStarted = false
            mController!!.unregisterCallback(mCb)
            try {
                mNotificationManager.cancel(NOTIFICATION_ID)
                mService.unregisterReceiver(this)
            } catch (ex: IllegalArgumentException) {
                // ignore if the receiver is not registered.
            }

            mService.stopForeground(true)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        Log.d(TAG, "Received intent with action " + action!!)
        when (action) {
            ACTION_PAUSE -> mTransportControls!!.pause()
            ACTION_PLAY -> mTransportControls!!.play()
            ACTION_NEXT -> mTransportControls!!.skipToNext()
            ACTION_PREV -> mTransportControls!!.skipToPrevious()
//            ACTION_STOP_CASTING -> {
//                val i = Intent(context, MusicService::class.java)
//                i.action = MusicService.ACTION_CMD
//                i.putExtra(MusicService.CMD_NAME, MusicService.CMD_STOP_CASTING)
//                mService.startService(i)
//            }
            else -> Log.w(TAG, "Unknown intent ignored. Action=$action")
        }
    }

    /**
     * Update the state based on a change on the session token. Called either when
     * we are running for the first time or when the media session owner has destroyed the session
     * (see [android.media.session.MediaController.Callback.onSessionDestroyed])
     */
    fun updateSessionToken() {
        val freshToken = mService.sessionToken
        if (mSessionToken == null && freshToken != null || mSessionToken != null && mSessionToken != freshToken) {
            if (mController != null) {
                mController!!.unregisterCallback(mCb)
            }
            mSessionToken = freshToken
            if (mSessionToken != null) {
                mController = MediaControllerCompat(mService, mSessionToken!!)
                mTransportControls = mController!!.transportControls
                if (mStarted) {
                    mController!!.registerCallback(mCb)
                }
            }
        }
    }

    private fun createContentIntent(description: MediaDescriptionCompat?): PendingIntent {
        val openUI = Intent(mService, MainActivity::class.java)
        openUI.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        //openUI.putExtra(MusicPlayerActivity.EXTRA_START_FULLSCREEN, true)
        if (description != null) {
            //openUI.putExtra(MainActivity.EXTRA_CURRENT_MEDIA_DESCRIPTION, description)
        }
        return PendingIntent.getActivity(mService, REQUEST_CODE, openUI,
            PendingIntent.FLAG_CANCEL_CURRENT)
    }

    private fun createNotification(): Notification? {
        Log.d(TAG, "updateNotificationMetadata. mMetadata=$mMetadata")
        if (mMetadata == null || mPlaybackState == null) {
            return null
        }

        val description = mMetadata!!.description

        var fetchArtUrl: String? = null
        var art: Bitmap? = null
//        if (description.iconUri != null) {
//            // This sample assumes the iconUri will be a valid URL formatted String, but
//            // it can actually be any valid Android Uri formatted String.
//            // async fetch the album art icon
//            val artUrl = description.iconUri!!.toString()
//            art = AlbumArtCache.getInstance().getBigImage(artUrl)
//            if (art == null) {
//                fetchArtUrl = artUrl
//                // use a placeholder art while the remote art is being downloaded
//                art = BitmapFactory.decodeResource(mService.resources,
//                    R.drawable.ic_default_art)
//            }
//        }

        // Notification channels are only supported on Android O+.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        val notificationBuilder = NotificationCompat.Builder(mService, CHANNEL_ID)

        val playPauseButtonPosition = addActions(notificationBuilder)
        notificationBuilder
            .setStyle(MediaStyle()
                // show only play/pause in compact view
                .setShowActionsInCompactView(playPauseButtonPosition)
                .setShowCancelButton(true)
                .setCancelButtonIntent(mStopIntent)
                .setMediaSession(mSessionToken))
            .setDeleteIntent(mStopIntent)
            //.setColor(mNotificationColor)
            .setSmallIcon(R.drawable.ic_menu_share)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .setContentIntent(createContentIntent(description))
            .setContentTitle(description.title)
            //.setSubText(mMetadata!!.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE))
            //.setContentInfo()
            .setContentText(mMetadata!!.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE))
            .setShowWhen(false)
            //.setLargeIcon(art)

//        if (mController != null && mController!!.extras != null) {
//            val castName = mController!!.extras.getString(MusicService.EXTRA_CONNECTED_CAST)
//            if (castName != null) {
//                val castInfo = mService.resources
//                    .getString(R.string.casting_to_device, castName)
//                notificationBuilder.setSubText(castInfo)
//                notificationBuilder.addAction(R.drawable.ic_close_black_24dp,
//                    mService.getString(R.string.stop_casting), mStopCastIntent)
//            }
//        }

        setNotificationPlaybackState(notificationBuilder)
        if (fetchArtUrl != null) {
            //fetchBitmapFromURLAsync(fetchArtUrl, notificationBuilder)
        }

        return notificationBuilder.build()
    }

    private fun addActions(notificationBuilder: NotificationCompat.Builder): Int {
        Log.d(TAG, "updatePlayPauseAction")

        var playPauseButtonPosition = 0
        // If skip to previous action is enabled
        if (mPlaybackState!!.actions and PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS != 0L) {
            notificationBuilder.addAction(
                AR.drawable.ic_media_previous,
                "previous",
                //mService.getString(R.string.label_previous),
                mPreviousIntent)

            // If there is a "skip to previous" button, the play/pause button will
            // be the second one. We need to keep track of it, because the MediaStyle notification
            // requires to specify the index of the buttons (actions) that should be visible
            // when in compact view.
            playPauseButtonPosition = 1
        }

        // Play or pause button, depending on the current state.
        val label: String
        val icon: Int
        val intent: PendingIntent
        if (mPlaybackState!!.state == PlaybackStateCompat.STATE_PLAYING) {
            label = "Pause" //mService.getString(R.string.label_pause)
            icon = AR.drawable.ic_media_pause
            intent = mPauseIntent
        } else {
            label = "Play" //mService.getString(R.string.label_play)
            icon = AR.drawable.ic_media_play
            intent = mPlayIntent
        }
        notificationBuilder.addAction(NotificationCompat.Action(icon, label, intent))

        // If skip to next action is enabled
        if (mPlaybackState!!.actions and PlaybackStateCompat.ACTION_SKIP_TO_NEXT != 0L) {
            notificationBuilder.addAction(
                AR.drawable.ic_media_next,
                "next",
                //mService.getString(R.string.label_next),
                mNextIntent)
        }

        return playPauseButtonPosition
    }

    private fun setNotificationPlaybackState(builder: NotificationCompat.Builder) {
        Log.d(TAG, "updateNotificationPlaybackState. mPlaybackState=" + mPlaybackState!!)
        if (mPlaybackState == null || !mStarted) {
            Log.d(TAG, "updateNotificationPlaybackState. cancelling notification!")
            mService.stopForeground(true)
            return
        }

        // Make sure that the notification can be dismissed by the user when we are not playing:
        builder.setOngoing(mPlaybackState!!.state == PlaybackStateCompat.STATE_PLAYING)
    }

    /**
     * Creates Notification Channel. This is required in Android O+ to display notifications.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        if (mNotificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            val notificationChannel = NotificationChannel(CHANNEL_ID,
                mService.getString(R.string.notification_channel),
                NotificationManager.IMPORTANCE_LOW)

            notificationChannel.description = mService.getString(R.string.notification_channel_description)

            mNotificationManager.createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        private val TAG = MediaNotificationManager.javaClass.toString()

        private val CHANNEL_ID = "com.example.android.uamp.MUSIC_CHANNEL_ID"

        private val NOTIFICATION_ID = 412
        private val REQUEST_CODE = 100

        val ACTION_PAUSE = "com.example.android.uamp.pause"
        val ACTION_PLAY = "com.example.android.uamp.play"
        val ACTION_PREV = "com.example.android.uamp.prev"
        val ACTION_NEXT = "com.example.android.uamp.next"
        val ACTION_STOP = "com.example.android.uamp.stop"
        val ACTION_STOP_CASTING = "com.example.android.uamp.stop_cast"
    }
}