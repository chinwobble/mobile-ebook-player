package com.example.benne.daisyapp2.ui.bookDetails

import android.app.SearchManager
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.benne.daisyapp2.R
import com.example.benne.daisyapp2.databinding.FragmentBookDetailsBinding
import com.example.benne.daisyapp2.di.InjectorUtils


/**
 * Created by benne on 10/01/2018.
 */
class BookDetailsFragment : Fragment() {
    private lateinit var _viewModel: BookDetailsViewModel
    private lateinit var _bookDetailsAdapter: BookDetailsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val context = activity ?: return null
        val bookMediaId = BookDetailsFragmentArgs.fromBundle(arguments!!).mediaId

        _viewModel = ViewModelProvider(this, InjectorUtils.provideBookDetailsFragmentViewModel(context, bookMediaId))
            .get(BookDetailsViewModel::class.java)

        _viewModel.bookMediaId = bookMediaId
        val binding = FragmentBookDetailsBinding.inflate(inflater, container, false)

        val recyclerView = binding.root.findViewById(R.id.book_details_rv) as androidx.recyclerview.widget.RecyclerView

        _bookDetailsAdapter = BookDetailsAdapter(_viewModel)
        recyclerView.adapter = _bookDetailsAdapter

        recyclerView.addItemDecoration(
                androidx.recyclerview.widget.DividerItemDecoration(activity, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL)
        )

        _viewModel.sections.observe(viewLifecycleOwner, Observer { items ->
            _bookDetailsAdapter.items = items!!
        })

        _viewModel.sections.observe(viewLifecycleOwner, Observer { items ->
            print(items)
        })

        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
    }
//    override fun performCreateOptionsMenu(menu: Menu, inflater: MenuInflater): Boolean {
//        return super.performCreateOptionsMenu(menu, inflater)
//    }
//    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
//        androidx.appcompat.widget.
//        layoutInflater.inflate(R.menu.main, menu)
//        val searchManager = activity!!.getSystemService(SearchManager::class.java)
//        val searchView = menu!!.findItem(R.id.search).actionView as SearchView
//        searchView.setSearchableInfo(
//                searchManager.getSearchableInfo(activity!!.componentName))
//        // searchView.setOnQueryTextListener()
//        // https://stackoverflow.com/questions/30398247/how-to-filter-a-recyclerview-with-a-searchview
//        super.onCreateOptionsMenu(menu, v, menuInfo)
//    }

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