package com.looklook.app

import android.app.Application
import com.looklook.BuildConfig
import timber.log.Timber
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LookLookApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}

