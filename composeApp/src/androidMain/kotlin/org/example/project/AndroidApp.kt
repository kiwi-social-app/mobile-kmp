package org.example.project

import android.app.Application
import com.google.firebase.FirebaseApp

class AndroidApp: Application(){
    override fun onCreate(){
        super.onCreate()
        FirebaseApp.initializeApp(this)

    }
}