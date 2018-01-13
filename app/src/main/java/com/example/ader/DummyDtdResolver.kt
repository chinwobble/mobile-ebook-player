package com.example.ader

import java.io.ByteArrayInputStream

import org.xml.sax.EntityResolver
import org.xml.sax.InputSource
import org.xml.sax.SAXException

// The EntityResolver prevents the SAX parser from trying to
// download external entities e.g. xhtml1-strict.dtd from
// the referenced URI. Having our own entity resolver makes
// the tests both faster, as they don't need to visit the web;
// and more reliable, as the w3c site returns a HTTP 503 to
// requests for this file from the SAX parser (it loads OK in
// a web browser).
// Thanks to: http://forums.sun.com/thread.jspa?threadID=413937
// for the following code and fix.
class DummyDtdResolver : EntityResolver {

    @Throws(SAXException::class, java.io.IOException::class)
    override fun resolveEntity(publicId: String, systemId: String): InputSource? {
        return if (systemId.endsWith(".dtd")) {
            InputSource(ByteArrayInputStream(
                    "<?xml version='1.0' encoding='UTF-8'?>".toByteArray()))
        } else {
            null
        }
    }
}
