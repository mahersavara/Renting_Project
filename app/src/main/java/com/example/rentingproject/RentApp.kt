package com.example.rentingproject

import android.app.Application
import com.example.rentingproject.utils.DataStoreHelper
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class RentApp : Application() {
        override fun onCreate() {
            super.onCreate()
            Timber.plant(Timber.DebugTree())
            FirebaseApp.initializeApp(this)
        }
}