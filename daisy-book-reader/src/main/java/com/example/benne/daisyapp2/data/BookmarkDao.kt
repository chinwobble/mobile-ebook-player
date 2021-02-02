package com.example.benne.daisyapp2.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface BookmarkDao {
    @Query("select * from book")
    @Transaction
    fun getBooksmarksByBookId() : LiveData<List<BookBookmark>>
}