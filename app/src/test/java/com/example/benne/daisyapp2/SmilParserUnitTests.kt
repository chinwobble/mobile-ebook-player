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
                println(smilElements?.get(0)?.text)
            } catch (ffe: FileNotFoundException) {
                println(ffe.message)
            }
        }
    }

    @Test
    fun `SmilParser - smilElements size = 1` () =
            assertEquals(1, smilElements?.size)

    @Test
    fun `SmilParser - texts Id = dgaw_0012` () =
            assertEquals("dgaw_0012", smilElements?.get(0)?.text?.id)

    @Test
    fun `SmilParser - texts file = 57790_50_management_ideashtml` () =
            assertEquals("57790_50_management_ideas.html", smilElements?.get(0)?.text?.file)

    @Test
    fun `SmilParser - texts Fragment= 57790_50_management_ideashtml` () =
            assertEquals("57790_50_management_ideas.html", smilElements?.get(0)?.text?.Fragment)

    @Test
    fun `SmilParser - audio = null` () =
            assertEquals(null, smilElements?.get(0)?.audio)

    @Test
    fun `SmilParser - nestedSeq size = 1` () =
            assertEquals(1, smilElements?.get(0)?.nestedSeq?.size)

    @Test
    fun `SmilParser - SmilAudioElement size = 2` () =
            assertEquals(2, smilElements?.get(0)?.nestedSeq?.get(0)?.audioReferences?.size)

    @Test
    fun `SmilParser - SmilAudioElement(0) id = qwrt_001` () =
            assertEquals("qwrt_0001", smilElements?.get(0)?.nestedSeq?.get(0)?.audioReferences?.get(0)?.id)

    @Test
    fun `SmilParser - SmilAudioElement(0) clipStart = 0` () =
            assertEquals(0L, smilElements?.get(0)?.nestedSeq?.get(0)?.audioReferences?.get(0)?.clipStart)

    @Test
    fun `SmilParser - SmilAudioElement(0) clipEnd = 539000` () =
            assertEquals(539000L, smilElements?.get(0)?.nestedSeq?.get(0)?.audioReferences?.get(0)?.clipEnd)

    @Test
    fun `SmilParser - SmilAudioElement(0) file = 50ManagementIdeas_010mp3` () =
            assertEquals("50ManagementIdeas_010.mp3", smilElements?.get(0)?.nestedSeq?.get(0)?.audioReferences?.get(1)?.file)

    @Test
    fun `SmilParser - SmilAudioElement(1) id = qwrt_002` () =
            assertEquals("qwrt_0002", smilElements?.get(0)?.nestedSeq?.get(0)?.audioReferences?.get(1)?.id)

    @Test
    fun `SmilParser - SmilAudioElement(1) clipStart = 539000` () =
            assertEquals(539000L, smilElements?.get(0)?.nestedSeq?.get(0)?.audioReferences?.get(1)?.clipStart)

    @Test
    fun `SmilParser - SmilAudioElement(1) clipEnd = 2302000` () =
            assertEquals(2302000L, smilElements?.get(0)?.nestedSeq?.get(0)?.audioReferences?.get(1)?.clipEnd)

    @Test
    fun `SmilParser - SmilAudioElement(1) file = 50ManagementIdeas_010mp3` () =
            assertEquals("50ManagementIdeas_010.mp3", smilElements?.get(0)?.nestedSeq?.get(0)?.audioReferences?.get(1)?.file)
}