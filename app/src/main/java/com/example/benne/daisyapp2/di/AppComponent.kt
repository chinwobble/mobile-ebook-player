package com.example.benne.daisyapp2.di

/**
 * Created by benne on 5/01/2018.
 */
import com.example.benne.daisyapp2.MainActivity
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector

@Component(modules = arrayOf(
        AndroidInjectionModule::class,
        AppModule::class
))
interface AppComponent: AndroidInjector<MainActivity>