package com.leafdisease.di

import android.app.Application
import com.leafdisease.viewmodel.AnalysisViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun analysisViewModelFactory(): AnalysisViewModel.Factory

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): AppComponent
    }
}
