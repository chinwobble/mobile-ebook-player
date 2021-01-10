package com.example.ader.smil

import java.util.ArrayList
import java.util.Arrays

/**
 * Encapsulates the <par> tag.
 * Limitations:
 * Support only one sequence of audio elements,
 * i.e. concurrent play of multiple data source is not supported
</par> */
data class ParallelElement(
    override val parent: ContainerElement,
    var textElement: TextElement? = null,
    var audioSequence: SequenceElement? = null) : ContainerElement {

    override val allAudioElementDepthFirst: List<AudioElement>
        get() =
            audioSequence?.allAudioElementDepthFirst
                    .orEmpty()


    override val allTextElementDepthFirst: List<TextElement>
        get() = Arrays.asList(textElement!!)

}
