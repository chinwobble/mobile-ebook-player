package com.example.benne.daisyapp2.data.daisy202

/**
 * Created by benne on 6/01/2018.
 */
// try convert a string to duration
// eg "1.1" returns a duration of 1100 miliseconds
// eg "" will return null
fun String.toMaybeMicroSeconds(): Long? {
    return try {
        this
            .toDouble()
            .times(1000 * 1000)
            .toLong()
    } catch (e: Exception) {
        null
    }
}