package com.example.benne.daisyapp2.data.daisy202

import android.os.*

/**
 * Created by benne on 6/01/2018.
 */

// h2
// -- h3
// -- -- smil
// -- -- page
data class DaisyBook(
    val navElements: List<NavElement>,
    val metadata: DaisyBookMetadata,
    val location: String) {
    companion object {
        fun getChildren(book: DaisyBook, id: String): Iterable<NavElement> {
            val index = book.navElements.indexOfFirst { it.toMediaId() == id }
            val item = book.navElements[index]
            return when (item) {
                is NavElement.HeadingReference -> {
                    val nextElement = book.navElements[index + 1]
                    return when (nextElement) {
                        is NavElement.HeadingReference -> {
                            book.navElements
                                    .drop(index + 2)
                                    .takeWhile { it is NavElement.HeadingReference
                                            && it.level == nextElement.level }
                        }
                        else -> {
                            book.navElements
                                    .drop(index + 2)
                                    .takeWhile { it is NavElement.PageReference }
                        }
                    }
                }
                else -> {
                    emptyList()
                }
            }
        }
    }
}


data class DaisyBookMetadata(
    val title: String,
    val date: String,
    val publisher: String,
    val creator: String,
    val isbn: String?
)

open class NavElement {
    data class HeadingReference(
       val id: String,
       val label: String,
       val smilFile: String,
       val fragment: String,
       val level: Byte
    ) : NavElement()

    // page reference
    data class PageReference(
        val id: String,
        val label: String,
        val smilFile: String,
        val fragment: String,
        private val className: String?
    ) : NavElement() {
        val pageType: PageType =
            when (className?.toLowerCase()) {
                "page-front" -> PageType.PAGE_FRONT
                "page-normal" -> PageType.PAGE_NORMAL
                "page-special" -> PageType.PAGE_SPECIAL
                else -> PageType.UNKNOWN
            }

        enum class PageType {
            PAGE_FRONT,
            PAGE_NORMAL,
            PAGE_SPECIAL,
            UNKNOWN
        }
    }

    // div element also possible but not implemented
    // http://www.daisy.org/z3986/specifications/daisy_202.html?q=publications/specifications/daisy_202.html#h3span
    // section
}