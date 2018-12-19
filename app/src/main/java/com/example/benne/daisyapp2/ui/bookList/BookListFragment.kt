package com.example.benne.daisyapp2.ui.bookList

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.media.MediaBrowserCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.benne.daisyapp2.databinding.FragmentBookListBinding
import com.example.benne.daisyapp2.di.InjectorUtils
import com.example.benne.daisyapp2.ui.ItemClickSupport
import com.example.benne.daisyapp2.viewModels.MainActivityViewModel

/**
 * Created by benne on 6/01/2018.
 */
class BookListFragment()
    : Fragment() {
    private lateinit var _viewModel: BookListViewModel
    private lateinit var mainActivityViewModel: MainActivityViewModel
    private lateinit var _bookListAdapter: BookListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val context = activity ?: return null

        _viewModel = ViewModelProviders
            .of(this, InjectorUtils.provideBookListViewFragmentViewModel(context))
            .get(BookListViewModel::class.java)

        mainActivityViewModel = ViewModelProviders
            .of(activity!!, InjectorUtils.provideMainActivityViewModel(context))
            .get(MainActivityViewModel::class.java)

        //set the media item
        val binding = FragmentBookListBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(this)

        binding.viewModel = _viewModel
        val recyclerView = binding.mediaItemsRv

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
                    val direction = BookListFragmentDirections.actionBookListFragmentToBookDetailsFragment(selectedMediaId)
                    findNavController().navigate(direction)
                    //mainActivityViewModel.mediaItemClicked(_bookListAdapter.mediaItems[position])
                }
        })
        return binding.root
    }

    companion object {
        val TAG = BookListFragment::javaClass.name
    }
}