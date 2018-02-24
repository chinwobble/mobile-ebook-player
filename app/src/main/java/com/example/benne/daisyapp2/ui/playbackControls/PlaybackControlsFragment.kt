package com.example.benne.daisyapp2.ui.playbackControls

import android.arch.lifecycle.*
import android.os.*
import android.support.v4.app.*
import android.view.*
import com.example.benne.daisyapp2.*
import com.example.benne.daisyapp2.R
import com.example.benne.daisyapp2.databinding.*
import com.example.benne.daisyapp2.playback.*
import com.example.benne.daisyapp2.viewModels.*

/**
 * Created by benne on 17/01/2018.
 */
class PlaybackControlsFragment
    : Fragment() {

    lateinit var mediaBrowserWrapper: MediaBrowserWrapper
    lateinit var _viewModel: PlaybackControlsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _viewModel = ViewModelProviders
            .of(activity)
            .get(PlaybackControlsViewModel::class.java)

        val rootView = inflater.inflate(R.layout.fragment_playback_controls, container, false)

        val bindings = FragmentPlaybackControlsBinding
            .bind(rootView)

        bindings.listener = _viewModel
        mediaBrowserWrapper = (super.getActivity() as MainActivity).mediaBrowserWrapper
        return rootView
    }
}