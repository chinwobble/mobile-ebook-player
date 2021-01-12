package com.example.benne.daisyapp2

import com.example.benne.daisyapp2.data.daisy202.SmilParser.Companion.parseSmil
import org.junit.BeforeClass
import java.io.File

class SmilParserUnitTests {
    companion object {
        private val path = File("").absolutePath + "\\src\\test\\java\\com\\example\\benne\\daisyapp2"
        @BeforeClass
        @JvmStatic fun smilTestSetup() {
            try {
                val file = File(path + "\\smilParserTestFile.html")
                book = parseSmil(file)
            } catch (ffe: FileNotFoundException) {
                println(ffe.message)
            }
        }
    }
}