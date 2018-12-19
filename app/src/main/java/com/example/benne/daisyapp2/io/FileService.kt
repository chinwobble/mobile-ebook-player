package com.example.benne.daisyapp2.io

import android.os.Environment
import com.example.benne.daisyapp2.data.daisy202.DaisyBook
import com.example.benne.daisyapp2.data.daisy202.NCCParser
import com.example.benne.daisyapp2.data.daisy202.SmilParElement
import com.example.benne.daisyapp2.data.daisy202.SmilParser
import java.io.File
import javax.inject.Inject

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