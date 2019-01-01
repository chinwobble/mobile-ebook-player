package com.example.benne.daisyapp2.playback

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import android.support.v4.media.MediaMetadataCompat
import androidx.media.session.MediaButtonReceiver
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.example.benne.daisyapp2.R

const val NOW_PLAYING_CHANNEL: String = "com.example.android.uamp.media.NOW_PLAYING"
const val NOW_PLAYING_NOTIFICATION: Int = 0xb339

/**
 * Helper class to encapsulate code for building notifications.
 */
class NotificationBuilder(private val context: Context) {
    private val platformNotificationManager: NotificationManager =
            context.getSystemService(NotificationManager::class.java)

    private val skipToPreviousAction = NotificationCompat.Action(
            R.drawable.exo_controls_previous,
            context.getString(R.string.exo_controls_previous_description),
            androidx.media.session.MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS))
    private val playAction = NotificationCompat.Action(
            R.drawable.exo_controls_play,
            context.getString(R.string.play_item),
            androidx.media.session.MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_PLAY))
    private val pauseAction = NotificationCompat.Action(
            R.drawable.exo_controls_pause,
            context.getString(R.string.play_pause),
            androidx.media.session.MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_PAUSE))
    private val skipToNextAction = NotificationCompat.Action(
            R.drawable.exo_controls_next,
            context.getString(R.string.skip_next),
            androidx.media.session.MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_SKIP_TO_NEXT))
    private val stopPendingIntent =
            androidx.media.session.MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_STOP)

    fun buildNotification(sessionToken: MediaSessionCompat.Token): Notification {
        if (shouldCreateNowPlayingChannel()) {
            createNowPlayingChannel()
        }

        val controller = MediaControllerCompat(context, sessionToken)
        val description = controller.metadata.description
        val playbackState = controller.playbackState

        val builder = NotificationCompat.Builder(context, NOW_PLAYING_CHANNEL)

        // Only add actions for skip back, play/pause, skip forward, based on what's enabled.
        var playPauseIndex = 0
        if (playbackState.isSkipToPreviousEnabled) {
            builder.addAction(skipToPreviousAction)
            ++playPauseIndex
        }
        if (playbackState.isPlaying) {
            builder.addAction(pauseAction)
        } else if (playbackState.isPlayEnabled) {
            builder.addAction(playAction)
        }
        if (playbackState.isSkipToNextEnabled) {
            builder.addAction(skipToNextAction)
        }

        val mediaStyle = androidx.media.app.NotificationCompat.MediaStyle()
                .setCancelButtonIntent(stopPendingIntent)
                .setMediaSession(sessionToken)
                .setShowActionsInCompactView(playPauseIndex)
                .setShowCancelButton(true)

        val metadata = controller.metadata
        val subtitle = metadata?.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE)
        return builder.setContentIntent(controller.sessionActivity)
                //.setContentText(description.subtitle)
                .setContentText(metadata?.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE))
                .setContentTitle(description.title)
                .setDeleteIntent(stopPendingIntent)
                .setLargeIcon(description.iconBitmap)
                .setOnlyAlertOnce(true)
                .setSmallIcon(R.drawable.ic_book)
                .setStyle(mediaStyle)
                .setShowWhen(false)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build()
    }
    
    private fun shouldCreateNowPlayingChannel() =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !nowPlayingChannelExists()

    @RequiresApi(Build.VERSION_CODES.O)
    private fun nowPlayingChannelExists() =
            platformNotificationManager.getNotificationChannel(NOW_PLAYING_CHANNEL) != null

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNowPlayingChannel() {
        val notificationChannel = NotificationChannel(NOW_PLAYING_CHANNEL,
                context.getString(R.string.notification_channel),
                NotificationManager.IMPORTANCE_LOW)
                .apply {
                    description = context.getString(R.string.notification_channel_description)
                }

        platformNotificationManager.createNotificationChannel(notificationChannel)
    }
}