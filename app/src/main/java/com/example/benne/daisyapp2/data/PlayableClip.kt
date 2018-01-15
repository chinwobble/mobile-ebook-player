package com.example.benne.daisyapp2.data

import java.io.*

/**
 * Created by benne on 13/01/2018.
 */
data class PlayableClip(
    val file: File,
    val clipStart: Long?,
    val clipEnd: Long?
)