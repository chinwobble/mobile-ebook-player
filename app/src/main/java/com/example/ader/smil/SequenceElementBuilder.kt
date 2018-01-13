package com.example.ader.smil

import org.xml.sax.Attributes
import java.lang.Double.parseDouble

class SequenceElementBuilder {
    var outerSeqDuration: Double = .0

    fun setSequenceDuration(attributes: Attributes) {
        if (attributes.getValue("dur") != null) {
            outerSeqDuration = parseDouble(attributes
                                .getValue("dur")
                                .replace("s", ""))
        }
    }

    fun Build (): SequenceElement? {
        return null
    }

}