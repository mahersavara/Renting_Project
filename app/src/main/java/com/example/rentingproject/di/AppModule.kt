package com.example.rentingproject.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    /**
     * Provides the [Context] for the application.
     * The @Provides annotation tells Hilt that this function provides a dependency.
     * @Singleton means that a single instance of the provided object will be used in the whole app.
     *
     * @param application The [Application] instance of the app.
     * @return The application [Context].
     */
    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }


}