package com.example.benne.daisyapp2.ui.bookList

import android.databinding.*
import android.support.v4.media.*
import android.support.v7.widget.*

/**
 * Created by benne on 19/02/2018.
 */
object BookListBindings {
    @JvmStatic
    @BindingAdapter("app:items")
    fun setBookItems(view: RecyclerView, items: List<MediaBrowserCompat.MediaItem>) {
        with (view.adapter as BookListAdapter) {
            setItems(items)
        }
    }
}