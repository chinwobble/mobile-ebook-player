package com.example.benne.daisyapp2.data.daisy202

/**
 * Created by benne on 6/01/2018.
 */
data class DaisyBook(
    val navElements: List<NavElement>,
    val metadata: DaisyBookMetadata) {

    companion object {
        fun getLevels() {

        }

        fun getChildren(id: String) {

        }

        fun getNextWithAudio() {

        }

        fun getPreviousWithAudio() {

        }
    }
}


data class DaisyBookMetadata(
    val title: String,
    val date: String,
    val publisher: String,
    val creator: String
) {
}

open class NavElement
    private constructor(
        val id: String,
        val label: String,
        val smilFile: String,
        val fragment: String
) {
    class HeadingReference(
       id: String,
       label: String,
       smilFile: String,
       fragment: String,
       val level: Byte
    ) : NavElement(id, label, smilFile, fragment)

    // page reference
    class PageReference(
        id: String,
        label: String,
        smilFile: String,
        fragment: String,
        className: String?
    ) : NavElement(id, label, smilFile, fragment) {
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