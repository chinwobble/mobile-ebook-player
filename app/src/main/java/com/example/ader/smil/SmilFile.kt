package com.example.ader.smil

import java.io.BufferedInputStream
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.io.Serializable

import javax.xml.parsers.ParserConfigurationException

import org.xml.sax.SAXException

import com.example.ader.io.ExtractXMLEncoding

class SmilFile : Serializable {

    private var elements: SequenceElement? = null

    /**
     * @return all the audio segments extracted from a Smil File.
     *
     * Note: This approach works adequately when we want the audio in
     * isolation from any other synchronised content. It's not sufficient when
     * we want to synchronise content.
     */
    val audioSegments: List<AudioElement>
        @Deprecated("")
        get() = elements!!.allAudioElementDepthFirst

    /**
     * @return all the text segments from a Smil File.
     *
     * Note: This approach works adequately when we want the text in
     * isolation from any other synchronised content. It's not sufficient when
     * we want to synchronise content.
     */
    val textSegments: List<TextElement>
        @Deprecated("")
        get() = elements!!.allTextElementDepthFirst

    /**
     * @return all the elements for this SMIL file.
     */
    val segments: List<SmilElement>
        get() = elements!!.elements

    /**
     * Opens a SMIL file.
     *
     * Notes:
     * - Currently a NPE can be thrown e.g. if the file has no content. This
     * is ugly. Should we convert/wrap these exceptions into an application
     * specific Exception?
     * - Also, how about adding some basic validation for the content? e.g.
     * length, structure, etc.
     * TODO(jharty): ruminate on the above notes... Address at some point.
     * @param filename
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    @Throws(FileNotFoundException::class, IOException::class, SAXException::class, ParserConfigurationException::class)
    fun load(filename: String) {
        // TODO(jharty): Add validation here?
        val encoding = ExtractXMLEncoding.obtainEncodingStringFromFile(filename)
        val alternateEncoding = ExtractXMLEncoding.mapUnsupportedEncoding(encoding)
        try {
            tryToExtractElements(filename, encoding)
        } catch (saxe: SAXException) {
            tryToExtractElements(filename, alternateEncoding)
        }

        return
    }

    /**
     * Parse the contents of the InputStream.
     *
     * The contents are expected to represent a valid SMIL file.
     * @param contents representing a valid SMIL file.
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    @Throws(IOException::class, SAXException::class, ParserConfigurationException::class)
    fun parse(contents: InputStream) {
        // TODO 20110827 (jharty): Should we check for empty content and throw an Exception?
        parseContents("UTF-8", contents)
    }

    @Throws(IOException::class, SAXException::class, ParserConfigurationException::class)
    private fun tryToExtractElements(filename: String, encoding: String) {

        val fis = FileInputStream(filename)
        val bis = BufferedInputStream(fis)

        try {
            parseContents(encoding, bis)
        } finally {
            fis.close()
            bis.close()
        }
    }

    /**
     * Parse the contents of an InputStream.
     *
     * The parsed content is stored internally in this class.
     * @param encoding the file encoding e.g. UTF-8.
     * @param bis the InputStream to parse.
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Throws(IOException::class, SAXException::class, ParserConfigurationException::class)
    private fun parseContents(encoding: String, bis: InputStream) {
        elements = SmilParser().parse(bis, encoding)
    }
}
