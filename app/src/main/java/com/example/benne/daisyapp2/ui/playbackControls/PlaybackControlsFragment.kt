package com.example.benne.daisyapp2.ui.playbackControls

import android.content.Context
import androidx.lifecycle.*
import android.os.*
import android.view.*
import com.example.benne.daisyapp2.databinding.*
import com.example.benne.daisyapp2.di.InjectorUtils

/**
 * Created by benne on 17/01/2018.
 */
class PlaybackControlsFragment
    : androidx.fragment.app.Fragment() {

    private lateinit var viewModel: PlaybackControlsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProvider(this, InjectorUtils.providePlaybackControlsFragmentViewModel(requireActivity()))
                .get(PlaybackControlsViewModel::class.java)

        val binding = FragmentPlaybackControlsBinding.inflate(inflater, container, false)

        binding.listener = viewModel
        binding.setLifecycleOwner(this)
        return binding.root
    }
}