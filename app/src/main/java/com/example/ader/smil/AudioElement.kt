package com.example.ader.smil

/**
 * Encapsulates the <audio> tag.
</audio> */
data class AudioElement(
        val parent: SmilElement,
        val src: String?,
        val clipBegin: Double,
        val clipEnd: Double,
        val id: String?) : MediaElement {

    /**
     * Gets the text element in the same <par> section
     *
     * @return
    </par> */
    val companionTextElement: TextElement?
        get() =
            if (parent is ParallelElement) {
                parent.textElement
            } else {
                null
            }

    /**
     * Whether the given time is in the clip of this audio element
     *
     * @param time
     * @return
     */
    fun inClip(time: Double): Boolean {
        return time < clipEnd && time >= clipBegin
    }

}
