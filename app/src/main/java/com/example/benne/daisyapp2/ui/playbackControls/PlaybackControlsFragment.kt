package com.example.benne.daisyapp2.ui.playbackControls

import android.arch.lifecycle.*
import android.os.*
import android.support.v4.app.*
import android.view.*
import com.example.benne.daisyapp2.databinding.*

/**
 * Created by benne on 17/01/2018.
 */
class PlaybackControlsFragment
    : Fragment() {

    private lateinit var viewModel: PlaybackControlsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProviders
            .of(activity!!)
            .get(PlaybackControlsViewModel::class.java)

        val binding = FragmentPlaybackControlsBinding.inflate(inflater, container, false)

        binding.listener = viewModel
        binding.setLifecycleOwner(this)
        return binding.root
    }
}