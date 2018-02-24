package com.example.benne.daisyapp2.ui.bookDetails

import android.annotation.*
import android.arch.lifecycle.*
import android.os.*
import android.support.v4.app.*
import android.support.v4.media.*
import android.support.v4.media.session.*
import android.support.v7.app.*

import android.support.v7.widget.*
import android.view.*
import com.example.benne.daisyapp2.R
import com.example.benne.daisyapp2.playback.*
import com.example.benne.daisyapp2.viewModels.*

/**
 * Created by benne on 10/01/2018.
 */
class BookDetailsFragment() : Fragment() {
    private lateinit var _viewModel: BookDetailsViewModel
    private lateinit var _bookDetailsAdapter: BookDetailsAdapter
    var mediaItems: List<MediaBrowserCompat.MediaItem> = emptyList()
    var mediaBrowserWrapper: MediaBrowserWrapper? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _viewModel = ViewModelProviders
            .of(this)
            .get(BookDetailsViewModel::class.java)

        val rootView = inflater.inflate(R.layout.fragment_book_details, container, false)
        val recyclerView = rootView.findViewById(R.id.book_details_rv) as RecyclerView

        subscribeToPlayableItemsCommands()

        _bookDetailsAdapter = BookDetailsAdapter(mediaItems, _viewModel)

        recyclerView.adapter = _bookDetailsAdapter

        recyclerView.layoutManager = LinearLayoutManager(this.context)

        recyclerView.addItemDecoration(
            DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        )

        return rootView
    }

    private fun subscribeToPlayableItemsCommands() {
        _viewModel.run {
            playSectionCommand.observe(
                this@BookDetailsFragment,
                Observer { onPlaySectionCommand(it!!) }
            )
        }
    }


    private fun onPlaySectionCommand(mediaItem: MediaBrowserCompat.MediaItem) {
        mediaBrowserWrapper!!.playSection(mediaItem.mediaId!!)
    }


    companion object {
        val TAG: String = BookDetailsFragment::class.java.canonicalName
    }
}