package com.leafdisease

import android.app.Application
import com.leafdisease.di.AppComponent
import com.leafdisease.di.DaggerAppComponent

class LeafDiseaseApplication : Application() {
    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(this)
    }
}
