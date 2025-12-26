package com.kiwisocial.app

import android.app.Application
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.initialize

class AndroidApp: Application(){
    override fun onCreate(){
        super.onCreate()
        Firebase.initialize(this)
    }
}