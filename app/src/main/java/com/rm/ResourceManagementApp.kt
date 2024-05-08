package com.rm

import android.app.Application
import android.content.SharedPreferences
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ResourceManagementApp : Application() {

    companion object {
        lateinit var sharedPrefs: SharedPreferences
    }

    override fun onCreate() {
        super.onCreate()
        sharedPrefs = getSharedPreferences("${packageName}_${javaClass.simpleName}", MODE_PRIVATE)
    }
}