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
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.source.ClippingMediaSource
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
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
                        exoPlayer.prepare(it.toClippingMediaSource(dataSourceFactory))
                    }
                }
                Player.STATE_READY -> {
                    // todo handle next
                }
            }
        }
    }

    override fun onPrepareFromSearch(query: String?, extras: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCommand(player: Player?, command: String?, extras: Bundle?, cb: ResultReceiver?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getSupportedPrepareActions(): Long =
            PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID or
                    PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID or
                    PlaybackStateCompat.ACTION_PREPARE_FROM_SEARCH or
                    PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH

    override fun getCommands(): Array<String>? = null

    override fun onPrepareFromMediaId(mediaId: String?, extras: Bundle?) {
        queueManager.setCurrentBook(extras!!.getString("bookMediaId")!!)
        queueManager.currentQueueMediaId = mediaId!!
        val playableClip = queueManager.currentClip()
        playableClip?.let {

            Log.d(TAG, "playing $playableClip")
            val source = playableClip.toClippingMediaSource(dataSourceFactory)

            val mediaIsSame =
                    currentPreparedMediaId == playableClip.hashCode().toString()

            if (!mediaIsSame) {
                currentPreparedMediaId = playableClip.hashCode().toString()
                exoPlayer.prepare(source)
            }

            exoPlayer.playWhenReady = true
        }

    }

    override fun onPrepareFromUri(uri: Uri?, extras: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPrepare() = Unit

    companion object {
        private val TAG: String = DaisyPlaybackPreparer::class.java.simpleName
        private fun Pair<PlayableClip, MediaBrowserCompat.MediaItem>.toClippingMediaSource(dataSourceFactory: DataSource.Factory): MediaSource {
            // The MediaSource represents the media to be played.
            val extractorMediaSource = ExtractorMediaSource
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