package com.example.benne.daisyapp2.ui.bookList

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.databinding.*
import android.os.Bundle
import android.support.v4.app.*
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.widget.*
import android.support.v7.widget.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.benne.daisyapp2.MediaSessionConnection
import com.example.benne.daisyapp2.R
import com.example.benne.daisyapp2.databinding.*
import com.example.benne.daisyapp2.di.InjectorUtils
import com.example.benne.daisyapp2.ui.*
import com.example.benne.daisyapp2.viewModels.MainActivityViewModel
import com.example.benne.daisyapp2.viewModels.MediaListViewModel
import io.reactivex.internal.operators.observable.*

/**
 * Created by benne on 6/01/2018.
 */
class BookListFragment()
    : Fragment() {
    private lateinit var _viewModel: MediaListViewModel
    private lateinit var mainActivityViewModel: MainActivityViewModel
    private lateinit var _bookListAdapter: BookListAdapter

//    override fun onListItemClick(l: ListView?, v: View?, position: Int, id: Long) {
//        super.onListItemClick(l, v, position, id)
//        val item = this.listAdapter.getItem(position)
//        this.viewModel.setSelectedItem("123")
//        RecyclerView.Adapter
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val context = activity ?: return null
        _viewModel = ViewModelProviders
            .of(this, InjectorUtils.provideBookListViewFragmentViewModel(context))
            .get(MediaListViewModel::class.java)
        mainActivityViewModel = ViewModelProviders
            .of(activity!!, InjectorUtils.provideMainActivityViewModel(context))
            .get(MainActivityViewModel::class.java)

        //set the media item
        //val rootView = inflater.inflate(R.layout.fragment_list, container, false)
        val binding = DataBindingUtil
            .inflate<FragmentListBinding>(
                inflater,
                R.layout.fragment_list,
                container,
                false)
        binding.viewModel = _viewModel
        binding.listener = _viewModel

        val rootView = binding.root

        val recyclerView = rootView.findViewById(R.id.media_items_rv) as RecyclerView

        _bookListAdapter = BookListAdapter(activity!!.baseContext, _viewModel.children.value!!)

        recyclerView.adapter = _bookListAdapter
        recyclerView.layoutManager = LinearLayoutManager(this.context)

        recyclerView.addItemDecoration(
            DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        )

        _viewModel.children.observe(this, Observer<List<MediaBrowserCompat.MediaItem>> { items ->
            Log.d(TAG, "observed changes to children items: ${items!!.count()}")
            _bookListAdapter.setItems(items)
        })

        ItemClickSupport.addTo(recyclerView)
            .setOnItemClickListener(object : ItemClickSupport.OnItemClickListener {
                override fun onItemClicked(recyclerView: RecyclerView, position: Int, v: View) {
                    val selectedMediaId =
                        _bookListAdapter.mediaItems[position].mediaId!!
                    _viewModel.setSelectedItem(selectedMediaId)
                    mainActivityViewModel.mediaItemClicked(_bookListAdapter.mediaItems[position])
                }
        })
        return rootView
    }


    companion object {
        val TAG = "booklist_fragment"
    }
}