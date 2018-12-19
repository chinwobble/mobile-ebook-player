package com.example.benne.daisyapp2.data

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction

@Dao
interface BookmarkDao {
    @Query("select * from book")
    @Transaction
    fun getBooksmarksByBookId() : LiveData<List<BookBookmark>>
}