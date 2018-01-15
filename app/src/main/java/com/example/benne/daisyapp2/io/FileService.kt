package com.example.benne.daisyapp2.io

import android.os.Environment
import com.example.benne.daisyapp2.data.daisy202.*
import org.jsoup.*
import java.io.File
import javax.inject.*
import kotlinx.*
import kotlinx.coroutines.experimental.*


/**
 * Created by benne on 7/01/2018.
 */
class FileService @Inject constructor() {
    fun getDaisyBooks() = async(CommonPool) {

        Environment.getExternalStorageDirectory()
            .walkTopDown()
            .filter { it.isFile && it.name.toLowerCase() == "ncc.html" }
            .map { NCCParser.parseNCC(it) }
            .toList()
    }

    fun asyncGetSmilFile(path: String, fileName: String) = async(CommonPool) {
        val smil = File(path, fileName)
        SmilParser.parseSmil(smil.readText())
    }

}