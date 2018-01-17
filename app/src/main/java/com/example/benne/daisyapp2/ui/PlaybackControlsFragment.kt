package com.example.benne.daisyapp2.ui

import android.os.*
import android.support.v4.app.*
import android.view.*
import android.widget.*
import com.example.benne.daisyapp2.*
import com.example.benne.daisyapp2.databinding.*

/**
 * Created by benne on 17/01/2018.
 */
class PlaybackControlsFragment
    : Fragment()
    , PlaybackControlsFragmentUserActionsListener {
    override fun onPlayPressed() {
        val a = 1 +1
    }

    override fun onPausePressed() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_playback_controls, container, false)

        val bindings = FragmentPlaybackControlsBinding
            .bind(rootView)

        bindings.listener = this

        return rootView

    }
}