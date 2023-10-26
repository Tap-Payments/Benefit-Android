package com.example.benefit

import android.app.Application


class MyApp : Application() {
        override fun onCreate() {
            super.onCreate()
        }

        companion object {
            private val TAG = MyApp::class.java.name
        }
    }
