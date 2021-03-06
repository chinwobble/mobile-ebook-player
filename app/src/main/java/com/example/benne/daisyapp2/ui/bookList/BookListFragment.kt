package com.example.benne.daisyapp2.ui.bookList

import androidx.lifecycle.Observer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.support.v4.media.MediaBrowserCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.benne.daisyapp2.databinding.FragmentBookListBinding
import com.example.benne.daisyapp2.di.InjectorUtils
import com.example.benne.daisyapp2.ui.ItemClickSupport
import com.example.benne.daisyapp2.viewModels.MainActivityViewModel

/**
 * Created by benne on 6/01/2018.
 */
class BookListFragment : Fragment() {
    private val viewModel by viewModels<BookListViewModel> {
        InjectorUtils.provideBookListViewFragmentViewModel(requireContext())
    }
    private val mainActivityViewModel by activityViewModels<MainActivityViewModel> {
        InjectorUtils.provideMainActivityViewModel(requireContext())
    }
    private lateinit var _bookListAdapter: BookListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //set the media item
        val binding = FragmentBookListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel
        val recyclerView = binding.mediaItemsRv

        _bookListAdapter = BookListAdapter()

        recyclerView.adapter = _bookListAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        recyclerView.addItemDecoration(
            DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        )

        viewModel.children.observe(viewLifecycleOwner, Observer { items ->
            Log.d(TAG, "observed changes to children items: ${items!!.count()}")
            _bookListAdapter.submitList(items)
        })

        ItemClickSupport.addTo(recyclerView)
            .setOnItemClickListener(object : ItemClickSupport.OnItemClickListener {
                override fun onItemClicked(recyclerView: RecyclerView, position: Int, v: View) {
                    val selectedMediaId =
                        _bookListAdapter.currentList[position].mediaId!!
                    viewModel.setSelectedItem(selectedMediaId)
                    val direction = BookListFragmentDirections
                            .actionBookListFragmentToBookDetailsFragment(selectedMediaId)
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