package com.onami.kagawacourses

import android.app.Application
import com.google.android.libraries.places.api.Places
import com.google.firebase.ktx.Firebase

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Places.initialize(applicationContext, "AIzaSyAIbLQgf6Ux6IhL2ZCHS-xVfV-Q8j0bTOM")
        Firebase.initializeApp(this)
    }
}

private fun Firebase.initializeApp(myApplication: MyApplication) {

}
