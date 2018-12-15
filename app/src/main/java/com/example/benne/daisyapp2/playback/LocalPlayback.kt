package com.example.benne.daisyapp2.playback

import android.content.*
import android.net.*
import android.util.*
import com.example.benne.daisyapp2.data.*
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.*
import com.google.android.exoplayer2.trackselection.*
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.util.*
import kotlinx.coroutines.*
import javax.inject.*
import android.support.v4.media.session.*
import kotlinx.coroutines.android.*

/**
 * Created by benne on 13/01/2018.
 */
class LocalPlayback
    @Inject constructor(private val applicationContext: Context) :

    Player.DefaultEventListener() {

    val isPlaying: Boolean
        get() = state == PlaybackStateCompat.STATE_PLAYING

    var currentPreparedMediaId: String? = null
    var state: Int = PlaybackStateCompat.STATE_NONE
    var playbackListener: PlaybackListener? = null


    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        Log.d(TAG, "state change new state: $playbackState")
        GlobalScope.launch (Dispatchers.Main) {

            when (playbackState) {
            // track ended play next if there is one
                Player.STATE_BUFFERING -> {

                }
                Player.STATE_ENDED -> {
                    playbackListener?.onComplete()
                    playbackListener?.onLocalPlaybackStateChanged(state)
                }
                Player.STATE_READY -> {
                    if (playWhenReady) {
                        state = PlaybackStateCompat.STATE_PLAYING
                    } else {
                        state = PlaybackStateCompat.STATE_PAUSED
                    }
                    playbackListener?.onLocalPlaybackStateChanged(state)
                }
            }
        }
    }

    private var _exoPlayer: SimpleExoPlayer?

    init {
        _exoPlayer = ExoPlayerFactory.newSimpleInstance(
            DefaultRenderersFactory(applicationContext), DefaultTrackSelector(), DefaultLoadControl())

        _exoPlayer!!.addListener(this)
    }

    fun play(playableClip: PlayableClip) {
        Log.d(TAG, "playing $playableClip")
        val source = playableClip.toClippingMediaSource(applicationContext)

        val mediaIsSame =
            currentPreparedMediaId == playableClip.hashCode().toString()

        if (!mediaIsSame) {
            currentPreparedMediaId = playableClip.hashCode().toString()
            _exoPlayer!!.prepare(source)
        }

        _exoPlayer!!.playWhenReady = true
    }

    fun pause() {
        Log.d("localplayback pause", "paused")
        _exoPlayer!!.playWhenReady = false
    }

    companion object {
        val TAG = LocalPlayback::class.java.simpleName
        private fun PlayableClip.toClippingMediaSource(applicationContext: Context): MediaSource {
            // Produces DataSource instances through which media data is loaded.
            val dataSourceFactory = DefaultDataSourceFactory(
                applicationContext, Util.getUserAgent(applicationContext, "daisyApp"), null)

            // The MediaSource represents the media to be played.
            val extractorMediaSource = ExtractorMediaSource
                .Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(this.file.path))

            return if (this.clipStart == null || this.clipEnd == null) {
                extractorMediaSource
            } else {
                ClippingMediaSource(
                    extractorMediaSource,
                    this.clipStart,
                    this.clipEnd,
                    true,
                    true,
                    false)
            }
        }
    }
    interface PlaybackListener {
        suspend fun onComplete()
        // @link PlaybackStateCompat
        fun onLocalPlaybackStateChanged(playbackStateCompat: Int)
    }
}