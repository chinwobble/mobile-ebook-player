package com.example.benne.daisyapp2.ui.bookList

import android.support.v4.media.MediaBrowserCompat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.benne.daisyapp2.databinding.ListItemBookBinding
import com.example.benne.daisyapp2.ui.DataBoundViewHolder

/**
 * Created by benne on 6/01/2018.
 */
class BookListAdapter : ListAdapter<MediaBrowserCompat.MediaItem, DataBoundViewHolder>(BookDiffCallback()) {

    override fun onBindViewHolder(holder: DataBoundViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item as Any)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBoundViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemBinding = ListItemBookBinding.inflate(layoutInflater, parent, false)
        return DataBoundViewHolder(itemBinding)
    }
}

private class BookDiffCallback : DiffUtil.ItemCallback<MediaBrowserCompat.MediaItem>() {

    override fun areItemsTheSame(oldItem: MediaBrowserCompat.MediaItem, newItem: MediaBrowserCompat.MediaItem): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }

    override fun areContentsTheSame(oldItem: MediaBrowserCompat.MediaItem, newItem: MediaBrowserCompat.MediaItem): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }
}