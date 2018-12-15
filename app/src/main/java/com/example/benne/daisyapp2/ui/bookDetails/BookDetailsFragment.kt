package com.example.benne.daisyapp2.ui.bookDetails

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.media.MediaBrowserCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import com.example.benne.daisyapp2.R
import com.example.benne.daisyapp2.di.InjectorUtils
import com.example.benne.daisyapp2.ui.bookList.BookListFragment
import com.example.benne.daisyapp2.viewModels.BookDetailsViewModel
import com.example.benne.daisyapp2.viewModels.MainActivityViewModel
import android.support.v4.view.MenuItemCompat.getActionView
import android.content.Context.SEARCH_SERVICE
import android.app.SearchManager
import android.content.Context
import android.widget.SearchView
import com.example.benne.daisyapp2.databinding.FragmentBookDetailsBinding


/**
 * Created by benne on 10/01/2018.
 */
class BookDetailsFragment() : Fragment() {
    private lateinit var _viewModel: BookDetailsViewModel
    private lateinit var _bookDetailsAdapter: BookDetailsAdapter
    var mediaItems: List<MediaBrowserCompat.MediaItem> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val context = activity ?: return null
        val mediaId = BookDetailsFragmentArgs.fromBundle(arguments).mediaId

        _viewModel = ViewModelProviders
            .of(this, InjectorUtils.provideBookDetailsFragmentViewModel(context, mediaId))
            .get(BookDetailsViewModel::class.java)

        val binding = FragmentBookDetailsBinding.inflate(inflater, container, false)

        val recyclerView = binding.root.findViewById(R.id.book_details_rv) as RecyclerView

        _bookDetailsAdapter = BookDetailsAdapter(mediaItems, _viewModel)
        recyclerView.adapter = _bookDetailsAdapter
        recyclerView.layoutManager = LinearLayoutManager(this.context)

        recyclerView.addItemDecoration(
            DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        )

        _viewModel.bookSections.observe(this, Observer { items ->
            _bookDetailsAdapter.setItems(items!!)
        })

        binding.setLifecycleOwner(this)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {

        inflater!!.inflate(R.menu.main, menu)
        val searchManager = activity!!.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu!!.findItem(R.id.search).actionView as SearchView
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(activity!!.componentName))
        // searchView.setOnQueryTextListener()
        // https://stackoverflow.com/questions/30398247/how-to-filter-a-recyclerview-with-a-searchview
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        this.setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    companion object {
        val TAG: String = BookDetailsFragment::class.java.canonicalName!!
        private val MEDIA_ID_ARG = "MEDIA_ID_ARG"
        fun newInstance(mediaId: String): BookDetailsFragment {
            return BookDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(MEDIA_ID_ARG, mediaId)
                }
            }
        }
    }
}