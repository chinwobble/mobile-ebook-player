package com.example.benne.daisyapp2.playback

import android.util.*
import com.example.benne.daisyapp2.data.*
import com.example.benne.daisyapp2.data.daisy202.*
import com.example.benne.daisyapp2.io.*
import kotlinx.coroutines.experimental.*
import java.io.*
import javax.inject.*

/**
 * Created by benne on 13/01/2018.
 */
class MediaProvider @Inject constructor(
    private val fileService: FileService
) {
    private var books: Iterable<DaisyBook> = emptyList()

    suspend fun asyncGetAllBooks(): Iterable<DaisyBook> {
        if (books.count() == 0) {
            books = fileService.getDaisyBooks().await()
        }

        return books
    }

    suspend fun getPlayableClip(
        filePath: String,
        navElement: NavElement): PlayableClip? {

        val smilFile: String
        val fragment: String
        when (navElement) {
            is NavElement.HeadingReference -> {
                smilFile = navElement.smilFile
                fragment = navElement.fragment
            }
            is NavElement.PageReference -> {
                smilFile = navElement.smilFile
                fragment = navElement.fragment
            }
            else -> {
                smilFile = ""
                fragment = ""
            }
        }

        val smilPars = fileService.asyncGetSmilFile(filePath, smilFile).await()

        val maybeMatchedByParId = smilPars
            .firstOrNull { it.id == fragment }

        val maybeMatchedByTextId =
            smilPars
                .takeIf { maybeMatchedByParId == null }
                ?.firstOrNull { it.text.id == fragment }

        val matched = maybeMatchedByParId ?: maybeMatchedByTextId

        return matched?.let {
            val audioRef = it.audio?.toPlayableClip(filePath)

            val audioRef2 = it.takeIf { audioRef == null }?.
                let { it.nestedSeq.first().audioReferences.toPlayableClip(filePath) }

            audioRef ?: audioRef2
        }
    }
}