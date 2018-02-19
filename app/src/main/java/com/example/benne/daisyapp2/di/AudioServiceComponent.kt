package com.example.benne.daisyapp2.di

import com.example.benne.daisyapp2.*
import dagger.*
import javax.inject.*

/**
 * Created by benne on 13/01/2018.
 */
@Singleton
@Component(modules = [(AudioServiceModule::class)])
interface AudioServiceComponent {
    fun inject(audioService: AudioService)
}