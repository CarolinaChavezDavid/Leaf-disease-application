package com.leafdisease.di

import android.app.Application
import android.content.Context
import com.leafdisease.ml.ClassifierFactory
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

    /**
     * Proporciona la fábrica de clasificadores como singleton.
     * La fábrica crea y cachea un [com.leafdisease.ml.BenchmarkClassifier] por modelo,
     * midiendo el tiempo de inicialización del intérprete LiteRT en cada primera carga.
     */
    @Provides
    @Singleton
    fun provideClassifierFactory(context: Context): ClassifierFactory {
        return ClassifierFactory(context)
    }
}
