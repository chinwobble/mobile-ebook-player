package com.example.benne.daisyapp2

import com.example.benne.daisyapp2.data.daisy202.SmilParElement
import com.example.benne.daisyapp2.data.daisy202.SmilParser.Companion.parseSmil
import junit.framework.Assert.assertEquals
import org.junit.BeforeClass
import org.junit.Test
import java.io.File
import java.io.FileNotFoundException
import java.util.*

class SmilParserUnitTests {
    companion object {
        private val fileURL = javaClass?.classLoader?.getResource("smilParserTestFile.smil")
        val file = File(fileURL?.toURI())
        private var smilElements: List<SmilParElement>? = null
        @BeforeClass
        @JvmStatic fun smilTestSetup() {
            try {
                var data: String = ""
                Scanner(file).use {
                    while(it.hasNextLine()) {
                        data += it.nextLine()
                    }
                }

                smilElements = parseSmil(data)
                println(smilElements.toString())
                println(smilElements?.get(0)?.id)
                println("text " + smilElements?.get(0)?.text)
            } catch (ffe: FileNotFoundException) {
                println(ffe.message)
            }
        }
    }

    @Test
    fun `test test` () =
            assertEquals(1, 1)
}