package com.example.ader.smil

import java.util.ArrayList

/**
 * Encapsulates the <seq> tag.
</seq> */
class SequenceElement(
    override val parent: ContainerElement? = null,
    val duration: Double,
    var elements: ArrayList<SmilElement> = ArrayList<SmilElement>()
    ) : ContainerElement {

    val isEmpty: Boolean
        get() = elements.isEmpty()

    override val allAudioElementDepthFirst: List<AudioElement>
        get() {

            val ret = ArrayList<AudioElement>()
            //elements.filter {  }
            for (elem in elements) {
                if (elem is ContainerElement) {
                    ret.addAll((elem as ContainerElement).allAudioElementDepthFirst)
                } else if (elem is AudioElement) {
                    ret.add(elem as AudioElement)
                }
            }
            return ret
        }

    override val allTextElementDepthFirst: List<TextElement>
        get() {
            val ret = ArrayList<TextElement>()
            for (elem in elements) {
                if (elem is ContainerElement) {
                    ret.addAll((elem as ContainerElement).allTextElementDepthFirst)
                } else if (elem is TextElement) {
                    ret.add(elem as TextElement)
                }
            }
            return ret
        }

    operator fun get(i: Int): SmilElement {
        return elements[i]
    }

    fun size(): Int {
        return elements.size
    }
}
