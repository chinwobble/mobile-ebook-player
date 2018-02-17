package com.example.benne.daisyapp2.data.daisy202

import org.jsoup.*
import org.jsoup.nodes.*
import java.io.*

/**
 * Created by benne on 7/01/2018.
 */
object NCCParser {
    fun parseNCC(file: File): DaisyBook {
        val document = Jsoup.parse(file.readText())
        val headChildren = document.head().children()

        val metadata =
            if (headChildren.count() != 0)
                parseDaisyMetadata(document.head().children())
            else // hack since there's a bug in JSoup
                parseDaisyMetadata(document.body().children())
        val navElements = parseNavElements(document.body())
        return DaisyBook(navElements, metadata, file.parent)
    }

    private fun parseDaisyMetadata(headChildren: Iterable<Element>) : DaisyBookMetadata {
        fun Iterable<Element>.getAttr(key: String) =
            this
                .first { it.attr("name") == key }
                .attr("content")
        val title: String = headChildren.getAttr("dc:title")
        val date: String = headChildren.getAttr("dc:date")
        val publisher: String = headChildren.getAttr("dc:publisher")
        val creator: String = headChildren.getAttr("dc:creator")
        return DaisyBookMetadata(title, date, publisher, creator)
    }

    private fun parseNavElements(body: Element): List<NavElement> {
        return body.children()
            .map {
                when (it.tagName()) {
                    in arrayOf("h1", "h2", "h3", "h4", "h5", "h6") ->
                        NavElement.HeadingReference(
                            it.id(),
                            it.selectFirst("a")
                                .text(),
                            it.selectFirst("a")
                                .attr("href")
                                .split("#")
                                .first(),
                            it.selectFirst("a")
                                .attr("href")
                                .split("#")
                                .last(),
                            it.tagName().replace("h", "").toByte()
                        )
                    "span" ->
                        NavElement.PageReference(
                            it.id(),
                            it.selectFirst("a")
                                .text(),
                            it.selectFirst("a")
                                .attr("href")
                                .split("#")
                                .first(),
                            it.selectFirst("a")
                                .attr("href")
                                .split("#")
                                .last(),
                            it.className()
                        )
                    else -> null
                }
            }.filter { it != null }.requireNoNulls()
    }
}