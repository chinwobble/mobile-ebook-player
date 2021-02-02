package com.example.benne.daisyapp2.data

import androidx.room.*

@Entity
data class Book(
    @PrimaryKey
    val id: String
    , val name: String
)

@Entity(
    foreignKeys = [
        ForeignKey(entity = Book::class, parentColumns = ["id"], childColumns = ["bookId"])
    ],
    indices = [
       Index(value = ["bookId"], unique = false)
    ]
)
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