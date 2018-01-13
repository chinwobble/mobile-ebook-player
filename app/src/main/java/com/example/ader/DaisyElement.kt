package com.example.ader

import org.xml.sax.Attributes
import org.xml.sax.helpers.AttributesImpl

class DaisyElement {
    private var attributes: AttributesImpl? = null
    var name = ""
    var text = ""
    fun setAttributes(attributes: Attributes) {
        this.attributes = AttributesImpl(attributes)
    }

    fun getAttributes(): Attributes? {
        return attributes
    }
}
