package com.example.benne.daisyapp2.ui.bookDetails

import android.support.v4.media.MediaBrowserCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import com.example.benne.daisyapp2.R
import com.example.benne.daisyapp2.databinding.ListItemPageNumberBinding
import com.example.benne.daisyapp2.ui.DataBoundViewHolder
import kotlinx.android.synthetic.main.list_item_page_number.view.*
import kotlinx.android.synthetic.main.nav_header_main.view.*

class PageNumbersGridAdapter() : BaseAdapter() {
    var items: List<MediaBrowserCompat.MediaItem> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = LayoutInflater.from(parent!!.context)
        val view = if (convertView !is Button) {
            //Button(parent.context)
            TextView(parent.context)
        } else {
            convertView
        }

        view.text = items[position].description.title
        return view
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return items[position].hashCode().toLong()
    }

    override fun getCount(): Int {
        return items.count()
    }
}

class PageNumbersAdapter()
    : RecyclerView.Adapter<PageNumbersAdapter.ViewHolder>() {

    override fun getItemCount() = items.count()

    var items: List<MediaBrowserCompat.MediaItem> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): PageNumbersAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemPageNumberBinding.inflate(inflater, parent, false)
        return PageNumbersAdapter.ViewHolder(binding)
    }

    override fun onBindViewHolder(vh: PageNumbersAdapter.ViewHolder, index: Int) {
        vh.bind(items[index])
    }

    class ViewHolder(binding: ListItemPageNumberBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MediaBrowserCompat.MediaItem) {

            //binding.item = item
        }


    }
}