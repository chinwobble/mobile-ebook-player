package com.example.benne.daisyapp2.di

/**
 * Created by benne on 5/01/2018.
 */
import android.content.Context
import android.support.v7.app.AppCompatActivity
import dagger.Module
import dagger.Provides

@Module
abstract class ActivityModule(protected val activity: AppCompatActivity) {

    @Provides
    fun provideActivity(): AppCompatActivity = activity

    @Provides
    fun provideActiviyContext(): Context = activity.baseContext
}