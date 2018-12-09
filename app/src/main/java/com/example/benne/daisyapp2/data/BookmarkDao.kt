package com.example.benne.daisyapp2.data

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query

@Dao
interface BookmarkDao {
    @Query("select * from book")
    fun getBooksmarksByBookId() : LiveData<List<BookBookmark>>
}