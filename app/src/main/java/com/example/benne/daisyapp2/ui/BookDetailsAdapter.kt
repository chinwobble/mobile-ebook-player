package com.example.benne.daisyapp2.ui

import android.support.v4.media.*
import android.support.v7.widget.*
import android.view.*
import android.widget.*
import com.example.benne.daisyapp2.*
import com.example.benne.daisyapp2.data.daisy202.*
import com.example.benne.daisyapp2.service.AudioService.Companion.ELEMENT_TYPE_KEY
import android.view.LayoutInflater
import com.example.benne.daisyapp2.databinding.*
import com.example.benne.daisyapp2.viewModels.*


/**
 * Created by benne on 11/01/2018.
 */
class BookDetailsAdapter(
    items: List<MediaBrowserCompat.MediaItem>,
    private val vm: BookDetailsViewModel)
    : RecyclerView.Adapter<DataBoundViewHolder>() {

    private val _items: List<MediaBrowserCompat.MediaItem>
    init {
        val heading = NavElement.HeadingReference::class.java.canonicalName
        _items = items
            .filter { it
                .description
                .extras!!
                .getString(ELEMENT_TYPE_KEY) == heading }
    }

    override fun getItemCount(): Int {
        return _items.count()
    }

    override fun onBindViewHolder(holder: DataBoundViewHolder, position: Int) {
        val item = _items[position]
        val listener = object : BookDetailsUserActionListener {
            override fun onPlaySection(item: MediaBrowserCompat.MediaItem) {
                vm.playSection(item)
            }
        }

        holder.bind(item as Any, listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBoundViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemBinding = PlayableMediaListItemBinding
            .inflate(layoutInflater, parent, false)
        return DataBoundViewHolder(itemBinding)
    }
}