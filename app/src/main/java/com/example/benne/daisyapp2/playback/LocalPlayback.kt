package com.example.benne.daisyapp2.playback

import android.content.*
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.*
import com.google.android.exoplayer2.trackselection.*

/**
 * Created by benne on 13/01/2018.
 */
class LocalPlayback(
    private val applicationContext: Context) :
    Player.EventListener {

    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {

    }

    override fun onSeekProcessed() {

    }

    override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {

    }

    override fun onPlayerError(error: ExoPlaybackException?) {

    }

    override fun onLoadingChanged(isLoading: Boolean) {

    }

    override fun onPositionDiscontinuity(reason: Int) {

    }

    override fun onRepeatModeChanged(repeatMode: Int) {

    }

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {

    }

    override fun onTimelineChanged(timeline: Timeline?, manifest: Any?) {

    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        when(playbackState) {
            MediaStatus.PLAYER_STATE_IDLE
            if (idleReason == MediaStatus.IDLE_REASON_FINISHED) {
    }

    private var _exoPlayer: SimpleExoPlayer?
    init {
        _exoPlayer = ExoPlayerFactory.newSimpleInstance(
            DefaultRenderersFactory(applicationContext), DefaultTrackSelector(), DefaultLoadControl())
        _exoPlayer!!.addListener(this)
    }

    fun play() {

    }
}