package com.example.ader.smil

/**
 * Encapsulates the <text> tag.
</text> */
data class TextElement(
        val parent: SmilElement,
        val src: String?,
        val id: String) : MediaElement

