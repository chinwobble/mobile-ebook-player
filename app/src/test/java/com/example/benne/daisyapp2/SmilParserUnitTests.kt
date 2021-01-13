package com.example.benne.daisyapp2

import com.example.benne.daisyapp2.data.daisy202.SmilParElement
import com.example.benne.daisyapp2.data.daisy202.SmilParser.Companion.parseSmil
import org.junit.BeforeClass
import java.io.File
import java.io.FileNotFoundException

class SmilParserUnitTests {
    companion object {
        private val path = File("").absolutePath + "\\src\\test\\resources\\com\\example\\benne\\daisyapp2"
        private var smilElements: List<SmilParElement>? = null
        @BeforeClass
        @JvmStatic fun smilTestSetup() {
            try {
                val file = File(path + "\\smilParserTestFile.html")
                smilElements = parseSmil(file.toString())
            } catch (ffe: FileNotFoundException) {
                println(ffe.message)
            }
        }
    }
}