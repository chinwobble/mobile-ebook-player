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
class BookListAdapter : RecyclerView.Adapter<DataBoundViewHolder>() {

    var items: List<MediaBrowserCompat.MediaItem> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onBindViewHolder(holder: DataBoundViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item as Any)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBoundViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemBinding =
                ListItemBookBinding.inflate(layoutInflater, parent, false)
        return DataBoundViewHolder(itemBinding)
    }

    override fun getItemCount() = items.count()

}