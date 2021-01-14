package com.example.benne.daisyapp2

import com.example.benne.daisyapp2.data.daisy202.DaisyBook
import com.example.benne.daisyapp2.data.daisy202.NCCParser.parseNCC
import com.example.benne.daisyapp2.data.daisy202.NavElement
import org.junit.Assert.assertEquals
import org.junit.BeforeClass
import org.junit.Test

import java.io.File
import java.io.FileNotFoundException

class NCCParserUnitTests {
    companion object {
        private var book: DaisyBook? = null
        private val fileURL = javaClass?.classLoader?.getResource("nccTestFile.html")
        @BeforeClass @JvmStatic fun nccTestSetup() {
            try {
                val file = File(fileURL?.toURI())
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
    fun `parseNCC - First NavElement groupId=dgaw_0001` () =
        assertEquals("dgaw_0001", book?.navElements?.get(0)?.groupId)

    @Test
    fun `parseNCC - First NavElement mlabel=50 MANAGEMENT IDEAS YOU REALLY NEED TO KNOW` () =
        assertEquals("50 MANAGEMENT IDEAS YOU REALLY NEED TO KNOW", book?.navElements?.get(0)?.label)

    @Test
    fun `parseNCC - Second NavElement groupId=dgaw_0001` () =
            assertEquals("dgaw_0001", book?.navElements?.get(1)?.groupId)

    @Test
    fun `parseNCC - Second NavElement mlabel=1` () =
            assertEquals("1", book?.navElements?.get(1)?.label)

    @Test
    fun `parseNCC - Third NavElement groupId=dgaw_0010` () =
            assertEquals("dgaw_0010", book?.navElements?.get(2)?.groupId)

    @Test
    fun `parseNCC - Third NavElement mlabel=Timeline` () =
            assertEquals("Timeline", book?.navElements?.get(2)?.label)
}