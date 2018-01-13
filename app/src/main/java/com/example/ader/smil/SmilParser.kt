/**
 * Parser for version 1 Smil files.
 *
 * Extracts the contents of the files, including the sequence of SEQ
 * (sequential) and PAR (parallel) elements. These often contain additional
 * elements e.g. text and audio contents.
 *
 * TODO(jharty):
 * We need to determine:
 * 1. Can PAR elements be nested
 * 2. Can SEQ elements be nested
 *
 * I also need to determine whether the current state machine is the
 * appropriate model to represent the contents of SMIL files. It's certainly
 * broken currently.
 */
package com.example.ader.smil

import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.util.logging.Logger

import javax.xml.parsers.ParserConfigurationException
import javax.xml.parsers.SAXParserFactory

import org.xml.sax.Attributes
import org.xml.sax.InputSource
import org.xml.sax.SAXException
import org.xml.sax.XMLReader
import org.xml.sax.helpers.DefaultHandler

import com.example.ader.DummyDtdResolver
import java.lang.Double.parseDouble
import com.example.ader.smil.SequenceElementBuilder

/**
 * Parser for SMIL files.
 *
 * TODO(jharty): Think carefully how to test the parsing for more complex
 * contents. The state transitions may be error-prone and the code in
 * startElement in particular is of concern.
 *
 * Note: Malformed or invalid content of a SMIL file stops elements from being
 * created. At the moment no error is reported in such cases. This was
 * evidenced when I tested with a simple file containing a single audio
 * element, when no elements were generated until I had added all the
 * attributes correctly. We could add informational messages to the create
 * methods.
 *
 * @author jharty
 */
class SmilParser : DefaultHandler() {
    private val log = Logger.getLogger(SmilParser::class.java.simpleName)

    private var currentContainer: ContainerElement? = null
    private var currentElement: SmilElement? = null
    private var state: State? = null

    private enum class State {
        INIT,
        SEQ,
        PARA
    }

    /**
     * Parse contents that should contain the Smil File structure.
     *
     * Useful for testing the parsing.
     *
     * @param content the content to parse
     * @return Sequence of Elements.
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Throws(IOException::class, SAXException::class, ParserConfigurationException::class)
    fun parse(content: String): SequenceElement? {
        return this.parse(ByteArrayInputStream(content.toByteArray()))
    }

    // Many thanks to the following link which provided the final piece for me,
    // http://stackoverflow.com/questions/293728/e-is-not-correctly-parsed
    @Throws(IOException::class, SAXException::class, ParserConfigurationException::class)
    @JvmOverloads
    fun parse(stream: InputStream, encoding: String = "UTF-8"): SequenceElement? {
        state = State.INIT
        val factory = SAXParserFactory.newInstance()
        val parser = factory.newSAXParser().xmlReader
        parser.entityResolver = DummyDtdResolver()
        parser.contentHandler = this
        val input = InputSource(stream)
        input.encoding = encoding

        parser.parse(input)
        throw Exception()
        //return rootSequence.Build()
    }

    /**
     * Called by the SAX Parser for the start of every element discovered.
     *
     * TODO(jharty): Rework, test, and simplify.
     */
    @Throws(SAXException::class)
    override fun startElement(uri: String,
                              localName: String,
                              qName: String,
                              attributes: Attributes) {

        super.startElement(uri, localName, qName, attributes)

        val name = if (localName.length != 0) localName
                    else qName

        when (state) {
            SmilParser.State.INIT -> {
                log.info("init " + name)
                if ("seq".equals(name, ignoreCase = true)) {
                    state = State.SEQ
                    currentElement = createSequenceElement(attributes)
                }
            }
            SmilParser.State.SEQ -> {
                log.info("seq " + name)
                val seq = currentElement as SequenceElement
                when {
                    "par".equals(name, ignoreCase = true) -> {
                        state = State.PARA
                        val par = ParallelElement(seq)
                        seq.elements.add(par)
                        currentElement = par
                    }
                    "text".equals(name, ignoreCase = true) -> {
                        val textElement = createTextElement(attributes)
                        seq.elements.add(createTextElement(attributes))
                        currentElement = textElement
                    }
                    "audio".equals(name, ignoreCase = true) -> {
                        val audioElement = createAudioElement(attributes)
                        seq.elements.add(audioElement)
                        currentElement = audioElement
                    }
                }
            }
            SmilParser.State.PARA -> {
                log.info("para " + name)
                val par = currentElement as ParallelElement?
                if ("text".equals(name, ignoreCase = true)) {
                    par!!.textElement = createTextElement(attributes)
                } else if ("audio".equals(name, ignoreCase = true)) {
                    val seq = SequenceElement(currentElement!! as ContainerElement, .1)
                    par!!.audioSequence = seq
                    seq.elements.add(createAudioElement(attributes))
                } else if ("seq".equals(name, ignoreCase = true)) {
                    val seq = createSequenceElement(attributes)
                    par!!.audioSequence = seq
                    currentElement = seq
                    state = State.SEQ
                }
            }
        }
    }

    /**
     * Called by the SAX Parser for the end of every element.
     */
    @Throws(SAXException::class)
    override fun endElement(uri: String, localName: String?, qName: String) {
        // TODO(jharty): determine when to restore the state to the previous
        // value. Currently we overwrite the contents of a nested parallel
        // element because the current implementation is flawed.

        val elementName = localName ?: qName

        // OK, let's see if we can determine the parent's type (SEQ/PAR)
        if (elementName.equals("seq", ignoreCase = true) || elementName.equals("par", ignoreCase = true)) {
            val parent = (currentElement!! as ContainerElement).parent
            if (parent is ParallelElement) {
                log.info("End: $elementName, parent element type: PAR")
                currentElement = parent
                state = State.PARA
            } else if (parent is SequenceElement) {
                log.info("End: $elementName, parent element type: SEQ")
                currentElement = parent
                state = State.SEQ
            } else {
                log.info("End: " + elementName)
            }
        }
        super.endElement(uri, localName, qName)
    }

    private fun createSequenceElement(attributes: Attributes): SequenceElement {
        var duration = 0.0
        if (attributes.getValue("dur") != null) {
            duration = parseDouble(attributes
                .getValue("dur").replace("s", ""))
        }
        return SequenceElement(currentElement as ContainerElement, duration)
    }

    private fun createAudioElement(attributes: Attributes): AudioElement {
        return AudioElement(currentElement!!,
                attributes.getValue("src"),
                // TODO: support more time formats
                parseDouble(attributes.getValue("clip-begin").replace("npt=", "").replace("s", "")),
                parseDouble(attributes.getValue("clip-end").replace("npt=", "").replace("s", "")),
                attributes.getValue("id"))
    }

    private fun createTextElement(attributes: Attributes): TextElement {
        //TODO: handle inline text
        return TextElement(currentElement!!,
                attributes.getValue("src"),
                attributes.getValue("id"))
    }
}
