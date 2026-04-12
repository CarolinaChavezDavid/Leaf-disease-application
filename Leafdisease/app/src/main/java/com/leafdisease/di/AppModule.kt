package com.leafdisease.di

import android.app.Application
import android.content.Context
import com.leafdisease.ml.LeafDiseaseClassifier
import com.leafdisease.ml.PlantDiseaseClassifier
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object AppModule {

    @Provides
    @Singleton
    fun provideApplicationContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun provideLeafDiseaseClassifier(context: Context): LeafDiseaseClassifier {
        return PlantDiseaseClassifier(context)
    }
}
