package com.example.benne.daisyapp2.ui.playbackControls

import androidx.lifecycle.*
import android.os.*
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.benne.daisyapp2.databinding.*
import com.example.benne.daisyapp2.di.InjectorUtils

/**
 * Created by benne on 17/01/2018.
 */
class PlaybackControlsFragment : Fragment() {
    private val viewModel by viewModels<PlaybackControlsViewModel> {
        InjectorUtils.providePlaybackControlsFragmentViewModel(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentPlaybackControlsBinding.inflate(inflater, container, true)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.executePendingBindings()
        return binding.root
    }
}