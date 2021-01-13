package com.example.benne.daisyapp2

import com.example.benne.daisyapp2.data.daisy202.DaisyBook
import com.example.benne.daisyapp2.data.daisy202.NCCParser.parseNCC
import org.junit.Assert.assertEquals
import org.junit.BeforeClass
import org.junit.Test

import java.io.File
import java.io.FileNotFoundException

class NCCParserUnitTests {
    companion object {
        private var book: DaisyBook? = null
        private val path = File("").absolutePath + "\\src\\test\\res"
        @BeforeClass @JvmStatic fun nccTestSetup() {
            try {
                val file = File(path + "\\nccTestFile.html")
                book = parseNCC(file)
            } catch (ffe: FileNotFoundException) {
                println(ffe.message)
            }
        }
    }

    @Test
    fun `parseNCC - Title should be '50 Management Ideas You Really Need to Know'`() =
            assertEquals("50 Management Ideas You Really Need to Know", book?.metadata?.title)

    @Test
    fun `parseNCC - Creator should be 'Edward Russell - Walling'` () =
            assertEquals("Edward Russell - Walling", book?.metadata?.creator)

    @Test
    fun `parseNCC - Year should be '2008'` () =
        assertEquals("2008", book?.metadata?.date)

    @Test
    fun `parseNCC - Publisher should be 'Vision Australia Information and Library Service'` () =
        assertEquals("Vision Australia Information and Library Service", book?.metadata?.publisher)

    @Test
    fun `parseNCC - ISBN should be '9781847241504'` () =
        assertEquals("9781847241504", book?.metadata?.isbn)

    @Test
    fun `parseNCC - navElements size should be 3` () =
        assertEquals(3, book?.navElements?.size)

    @Test
    fun `parseNCC - First NavElement id=dgaw_0001` () =
        assertEquals("dgaw_0001", book?.navElements?.get(0)?.groupId)

    @Test
    fun `parseNCC - First NavElement mlabel=50 MANAGEMENT IDEAS YOU REALLY NEED TO KNOW` () =
        assertEquals("50 MANAGEMENT IDEAS YOU REALLY NEED TO KNOW", book?.navElements?.get(0)?.label)
}