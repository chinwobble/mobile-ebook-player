package com.example.benne.daisyapp2.playback

import com.example.benne.daisyapp2.data.daisy202.*

/**
 * Created by benne on 13/01/2018.
 */
class QueueManager(val books: List<DaisyBook>) {
    private var currentQueuedBook: String = ""
    private var currentIndex: Int = 0

    fun isSameBook(): Boolean {
        return false
    }

    fun setPlayQueue(bookId: String) {
        currentQueuedBook = bookId
    }

    fun getNextMediaItem(mediaId: String) {
        val book = books.single {
            it.toMediaId() == mediaId
            || it.navElements.any { it.toMediaId() == mediaId }
        }

        val index = book.navElements.indexOfFirst { it.toMediaId() == mediaId }

        // a next element is available
        if (index >= book.navElements.count() - 1) {

        }

    }
}