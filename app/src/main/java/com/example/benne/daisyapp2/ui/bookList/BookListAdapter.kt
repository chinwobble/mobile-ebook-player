package com.example.benne.daisyapp2.ui.bookList

import android.content.*
import android.support.v4.media.*
import android.support.v7.widget.*
import android.view.*
import android.view.LayoutInflater
import android.widget.*
import com.example.benne.daisyapp2.R
import com.example.benne.daisyapp2.databinding.*
import com.example.benne.daisyapp2.ui.*

/**
 * Created by benne on 6/01/2018.
 */
class BookListAdapter(
    private val context: Context,
    var mediaItems: List<MediaBrowserCompat.MediaItem>
    ) : RecyclerView.Adapter<DataBoundViewHolder>() {

    fun setItems(items: List<MediaBrowserCompat.MediaItem>) {
        mediaItems = items
        this.notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: DataBoundViewHolder, position: Int) {
        val item = mediaItems[position]
        holder.bind(item as Any)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBoundViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemBinding =

            MediaListItemBinding
            .inflate(layoutInflater, parent, false)
        return DataBoundViewHolder(itemBinding)
    }

    override fun getItemCount() = this.mediaItems.count()

}