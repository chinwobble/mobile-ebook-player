package com.example.ader.smil

interface ContainerElement : SmilElement {

    /**
     * Useful when navigating the nested data structures.
     * @return the parent SMIL element
     */
    val parent: ContainerElement?

    val allAudioElementDepthFirst: List<AudioElement>

    val allTextElementDepthFirst: List<TextElement>
}
