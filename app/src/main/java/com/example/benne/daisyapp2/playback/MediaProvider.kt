package com.example.benne.daisyapp2.playback

import android.util.*
import com.example.benne.daisyapp2.data.*
import com.example.benne.daisyapp2.data.daisy202.*
import com.example.benne.daisyapp2.io.*
import kotlinx.coroutines.*
import java.io.*
import javax.inject.*

/**
 * Created by benne on 13/01/2018.
 */
class MediaProvider @Inject constructor(
    private val fileService: FileService
) {
    private var books: Iterable<DaisyBook> = emptyList()

    fun asyncGetAllBooks(): Iterable<DaisyBook> {
        if (books.count() == 0) {
            books = fileService.getDaisyBooks()
        }

        return books
    }

    fun getPlayableClip(
        filePath: String,
        navElement: NavElement): PlayableClip? {

        Log.d("getPlayableClip", navElement.toString())
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

        val smilPars = fileService.asyncGetSmilFile(filePath, smilFile)

        val maybeMatchedByParId = smilPars
            .firstOrNull { it.id == fragment }

        val maybeMatchedByTextId =
            smilPars
                .takeIf { maybeMatchedByParId == null }
                ?.firstOrNull { it.text.id == fragment }

        val matchedParElement = maybeMatchedByParId ?: maybeMatchedByTextId

        return matchedParElement?.let {
            Log.w("matched par element", it.toString())
            val audioRef = it.audio?.toPlayableClip(filePath)

            val audioRef2 = it
                .takeIf { audioRef == null }
                ?.let { it.nestedSeq.firstOrNull()?.audioReferences?.toPlayableClip(filePath) }

            val retVal = audioRef ?: audioRef2
            Log.d(TAG, "Get playable clip found: ${retVal?.toString()}")
            return retVal
        }
    }
    companion object {
        val TAG: String = MediaProvider::class.java.simpleName
    }
}