package com.example.benne.daisyapp2.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.*
import android.os.Bundle
import android.support.v4.app.*
import android.support.v4.widget.*
import android.support.v7.widget.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.benne.daisyapp2.R
import com.example.benne.daisyapp2.databinding.*
import com.example.benne.daisyapp2.viewModels.MediaListViewModel
import io.reactivex.internal.operators.observable.*

/**
 * Created by benne on 6/01/2018.
 */
class BookListFragment
    : Fragment()
    , BookListUserActionListener {


    private lateinit var _viewModel: MediaListViewModel
    private lateinit var _bookListAdapter: BookListAdapter

//    override fun onListItemClick(l: ListView?, v: View?, position: Int, id: Long) {
//        super.onListItemClick(l, v, position, id)
//        val item = this.listAdapter.getItem(position)
//        this.viewModel.setSelectedItem("123")
//        RecyclerView.Adapter
//    }

    override fun onBookListRefresh() {
        val a = 1+1
        _viewModel.finishRefreshing()
        // todo move to viewModel command
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _viewModel = ViewModelProviders
            .of(activity)
            .get(MediaListViewModel::class.java)

        //set the media item
        //val rootView = inflater.inflate(R.layout.fragment_list, container, false)
        val binding = DataBindingUtil
            .inflate<FragmentListBinding>(
                inflater,
                R.layout.fragment_list,
                container,
                false)

        val rootView = binding.root
        binding.viewModel = _viewModel

        binding.listener = this

        val recyclerView = rootView.findViewById(R.id.media_items_rv) as RecyclerView
        _bookListAdapter = BookListAdapter(activity, emptyList())
        recyclerView.adapter = _bookListAdapter
        recyclerView.layoutManager = LinearLayoutManager(this.context)

        recyclerView.addItemDecoration(
            DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        )

        _viewModel.children.observe(this, Observer {t ->
            if (t != null) {
                _bookListAdapter.setItems(t)
            }
        })

        ItemClickSupport.addTo(recyclerView)
            .setOnItemClickListener(object : ItemClickSupport.OnItemClickListener {
                override fun onItemClicked(recyclerView: RecyclerView, position: Int, v: View) {
                    val selectedMediaId =
                        _bookListAdapter.mediaItems[position].mediaId!!
                    _viewModel.setSelectedItem(selectedMediaId)
                }
        })
        return rootView
    }


    companion object {
        val TAG = "booklist_fragment"
    }

}