package com.example.benne.daisyapp2.data

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query

@Dao
interface BookmarkDao {
    @Query("select * from bookmark where bookId = :bookId")
    fun getBooksmarksByBookId(bookId: Int)
}