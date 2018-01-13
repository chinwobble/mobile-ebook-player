package com.example.ader.smil

import java.io.CharArrayWriter
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.logging.Logger

import javax.xml.parsers.SAXParserFactory

import org.xml.sax.Attributes
import org.xml.sax.InputSource
import org.xml.sax.SAXException
import org.xml.sax.XMLReader
import org.xml.sax.helpers.DefaultHandler

import com.example.ader.*

/**
 * Helper Class for retrieving the text for TextElement
 * Currently it only works with local files.
 */
class TextLocator(private val baseDirectory: File) : DefaultHandler() {
    private var targetId: String? = null
    private var result: String? = null
    private var depth = 0
    private val log = Logger.getAnonymousLogger()
    private val value = CharArrayWriter()

    @Throws(IOException::class)
    fun getText(src: String): String? {
        if (src.contains("#")) {
            targetId = src.substring(src.indexOf('#') + 1)
            val file = File(baseDirectory, src.substring(0, src.indexOf('#')))
            val factory = SAXParserFactory.newInstance()
            try {
                val input = InputSource(FileInputStream(file))
                val saxParser = factory.newSAXParser().xmlReader
                saxParser.entityResolver = DummyDtdResolver()
                saxParser.contentHandler = this
                saxParser.parse(input)
            } catch (e: Exception) {
                throw RuntimeException(e)
            }

        } else {
            val file = File(baseDirectory, src)
            val len = file.length()
            val fis = FileInputStream(file)
            val buf = ByteArray(len.toInt())
            fis.read(buf)
            fis.close()
            result = String(buf)
        }
        return result
    }

    @Throws(SAXException::class)
    override fun startElement(uri: String, localName: String, name: String,
                              attributes: Attributes) {

        value.reset()
        val id = attributes.getValue("id")
        log.info(name + ": " + id)
        if (id != null && id == targetId) {
            depth++
        }
    }

    @Throws(SAXException::class)
    override fun characters(ch: CharArray, start: Int, length: Int) {
        if (depth > 0) {
            value.write(ch, start, length)
        }
    }

    override fun endElement(uri: String, localName: String, name: String) {
        if (depth > 0) {
            depth--
            result = value.toString()
        }
    }

}
