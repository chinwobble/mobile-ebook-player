package com.example.benne.daisyapp2.playback

import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import com.example.benne.daisyapp2.data.PlayableClip
import com.example.benne.daisyapp2.data.daisy202.toMediaMetadata
import com.google.android.exoplayer2.ControlDispatcher
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.source.ClippingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DaisyPlaybackPreparer(
        private val queueManager: QueueManager,
        private val exoPlayer: ExoPlayer,
        private val dataSourceFactory: DataSource.Factory
    ) : MediaSessionConnector.PlaybackPreparer
      , Player.EventListener {

    private var currentPreparedMediaId: String? = ""

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        Log.d(TAG, "state change new state: $playbackState")
        GlobalScope.launch (Dispatchers.Main) {
            when (playbackState) {
                // track ended play next if there is one
                Player.STATE_BUFFERING -> {

                }
                Player.STATE_ENDED -> {
                    val playableClip = queueManager.nextPlayableClip()
                    playableClip?.let {
                        exoPlayer.setMediaSource(it.toClippingMediaSource(dataSourceFactory))
                        exoPlayer.prepare()
                    }
                }
                Player.STATE_READY -> {
                    // todo handle next
                }
            }
        }
    }

    override fun onCommand(player: Player, controlDispatcher: ControlDispatcher, command: String, extras: Bundle?, cb: ResultReceiver?): Boolean {
        TODO("Not yet implemented")
    }

    override fun getSupportedPrepareActions(): Long =
            PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID or
                    PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID or
                    PlaybackStateCompat.ACTION_PREPARE_FROM_SEARCH or
                    PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH

    override fun onPrepare(playWhenReady: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onPrepareFromMediaId(mediaId: String, playWhenReady: Boolean, extras: Bundle?) {
        queueManager.setCurrentBook(extras!!.getString("bookMediaId")!!)
        queueManager.currentQueueMediaId = mediaId
        val playableClip = queueManager.currentClip()
        playableClip?.let {

            Log.d(TAG, "playing $playableClip")
            val source = playableClip.toClippingMediaSource(dataSourceFactory)

            val mediaIsSame =
                    currentPreparedMediaId == playableClip.hashCode().toString()

            if (!mediaIsSame) {
                currentPreparedMediaId = playableClip.hashCode().toString()
                exoPlayer.setMediaSource(source)
                exoPlayer.prepare()
            }

            exoPlayer.playWhenReady = true
        }

    }

    override fun onPrepareFromSearch(query: String, playWhenReady: Boolean, extras: Bundle?) {
        TODO("Not yet implemented")
    }

    override fun onPrepareFromUri(uri: Uri, playWhenReady: Boolean, extras: Bundle?) {
        TODO("Not yet implemented")
    }

    companion object {
        private val TAG: String = DaisyPlaybackPreparer::class.java.simpleName
        private fun Pair<PlayableClip, MediaBrowserCompat.MediaItem>.toClippingMediaSource(dataSourceFactory: DataSource.Factory): MediaSource {
            // The MediaSource represents the media to be played.
            val extractorMediaSource = ProgressiveMediaSource
                    .Factory(dataSourceFactory)
                    .setTag(this.second.description)
                    .createMediaSource(Uri.parse(this.first.file.path))

            return if (this.first.clipStart == null || this.first.clipEnd == null) {
                extractorMediaSource
            } else {
                ClippingMediaSource(
                        extractorMediaSource,
                        this.first.clipStart!!,
                        this.first.clipEnd!!,
                        true,
                        true,
                        false)
            }
        }
    }
}