package com.example.benne.daisyapp2.io

import android.os.Environment
import android.util.Log
import com.example.ader.NCCEntry
import com.example.benne.daisyapp2.AudioService
import com.example.benne.daisyapp2.data.daisy202.*
import org.jsoup.*
import java.io.File
import javax.inject.*
import kotlinx.*
import kotlinx.coroutines.*
import kotlinx.coroutines.async

/**
 * Created by benne on 7/01/2018.
 */
class FileService @Inject constructor() {
    fun getDaisyBooks(): Iterable<DaisyBook> {
        return Environment.getExternalStorageDirectory()
            .walkTopDown()
            .filter { it.isFile && it.name.equals("ncc.html", true) }
            .map { NCCParser.parseNCC(it) }
            .toList()
    }

    fun asyncGetSmilFile(path: String, fileName: String): List<SmilParElement> {
        val smil = File(path, fileName)
        return SmilParser.parseSmil(smil.readText())
    }

    companion object {
        private val TAG: String = FileService::class.java.simpleName
    }
}