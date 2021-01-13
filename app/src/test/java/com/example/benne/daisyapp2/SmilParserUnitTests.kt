package com.example.benne.daisyapp2

import com.example.benne.daisyapp2.data.daisy202.SmilParElement
import com.example.benne.daisyapp2.data.daisy202.SmilParser.Companion.parseSmil
import org.junit.BeforeClass
import java.io.File
import java.io.FileNotFoundException

class SmilParserUnitTests {
    companion object {
        private val fileURL = javaClass?.classLoader?.getResource("smilParseTestFile.html")
        private var smilElements: List<SmilParElement>? = null
        @BeforeClass
        @JvmStatic fun smilTestSetup() {
            try {
                val file = File(fileURL?.toURI())
                smilElements = parseSmil(file.toString())
                // TODO: 13/01/2021 this will not work - need to read the file into string 
            } catch (ffe: FileNotFoundException) {
                println(ffe.message)
            }
        }
    }
}