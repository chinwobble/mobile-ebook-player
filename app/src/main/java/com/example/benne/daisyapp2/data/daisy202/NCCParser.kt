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
        val navElements = ParseSections(document.body())
        return DaisyBook(navElements, metadata, file.parent)
    }

    private fun parseDaisyMetadata(headChildren: Iterable<Element>) : DaisyBookMetadata {
        fun Iterable<Element>.getAttr(key: String) =
            this
                .firstOrNull() { it.attr("name") == key }
                ?.attr("content")
        val title = headChildren.getAttr("dc:title")!!
        val date = headChildren.getAttr("dc:date")!!
        val publisher = headChildren.getAttr("dc:publisher")!!
        val creator = headChildren.getAttr("dc:creator")!!
        val isbn = headChildren.getAttr("dc:source")
        return DaisyBookMetadata(title, date, publisher, creator, isbn)
    }

    private fun ParseSections(body: Element): List<NavElement> {
        val results = mutableListOf<NavElement>()
        for (i in 0 until body.children().count()) {
            val current = body.children()[i]
            val itemToAdd = when (current.tagName().toLowerCase()) {
                in arrayOf("h1", "h2", "h3", "h4", "h5", "h6") -> toHeading(current)
                "span" -> {
                    val parentElement = results.findLast { it is NavElement.HeadingReference }
                    toPageReference(current, parentElement!!.groupId)
                }
                else -> null
            }
            itemToAdd?.also { results.add(it) }
        }

        return results;
    }

    private fun toPageReference(it: Element, parentId: String): NavElement.PageReference {
        return NavElement.PageReference(
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
                it.className(),
                parentId
        )
    }

    private fun toHeading(it: Element): NavElement.HeadingReference {
        return NavElement.HeadingReference(
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
    }
}