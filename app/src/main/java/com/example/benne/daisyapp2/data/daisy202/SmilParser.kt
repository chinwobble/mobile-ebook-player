package com.example.benne.daisyapp2.data.daisy202

import org.jsoup.*
import org.jsoup.nodes.*

/**
 * Created by benne on 6/01/2018.
 */
class SmilParser {
    companion object {

        fun parseSmil(document: String): List<SmilParElement> {
            val doc = Jsoup.parse(document)
            val outSeq = doc.getElementsByTag("seq").first()

            return outSeq.children()
                .map {
                    when (it.tagName()) {
                        "par" -> toPar(it)
                        else -> null
                    }
                }.filter { it != null }.requireNoNulls()
        }

        private fun toPar(element: Element): SmilParElement {
            val id = element.id()
            val text = element.children()
                .filter { it.tagName() == "text" }
                .map { SmilTextElement(it) }
                .first()
            val audio = element.children()
                .filter { it.tagName() == "audio" }
                .map { SmilAudioElement(it) }
                .firstOrNull()

            val nestedSeq = element.children()
                .filter { it.tagName() == "seq" }
                .map { toNestedSeq(it) }

            return SmilParElement(id, text, audio, nestedSeq)
        }

        private fun toNestedSeq(nestedSeq: Element) : SmilNestedSeqElement {
            val audioRefs = nestedSeq.children()
                .filter { it.tagName() == "audio" }
                .map { SmilAudioElement(it) }

            return SmilNestedSeqElement(audioRefs)
        }

        private fun toSmilText(element: Element) {

        }
    }
}