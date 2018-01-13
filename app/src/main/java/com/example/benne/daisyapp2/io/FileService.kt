package com.example.benne.daisyapp2.io

import android.os.Environment
import com.example.benne.daisyapp2.data.daisy202.*
import org.jsoup.*
import java.io.File


/**
 * Created by benne on 7/01/2018.
 */
class FileService {
    fun getDaisyBooks(): List<DaisyBook> {

        return Environment.getExternalStorageDirectory()
            .walkTopDown()
            .filter { it.isFile && it.name.toLowerCase() == "ncc.html" }
            .map { it.readText() }
            .map { Jsoup.parse(it) }
            .map { NCCParser.parseNCC(it) }
            .toList()
    }

}