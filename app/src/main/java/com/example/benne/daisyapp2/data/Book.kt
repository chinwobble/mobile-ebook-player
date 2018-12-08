package com.example.benne.daisyapp2.data

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.Relation

@Entity
data class Book(
    @PrimaryKey
    val id: String
    , val name: String
)

@Entity
data class Bookmark(
    @PrimaryKey
    val id: String
    , val bookId: String
)

class BookBookmark {
    @Embedded
    lateinit var book: Book

    @Relation(parentColumn = "id", entityColumn = "bookId")
    var bookmarks: List<Bookmark> = arrayListOf()
}