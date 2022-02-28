package com.example.todoapplication.di

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import com.example.todoapplication.data.TaskDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton
import javax.security.auth.callback.Callback

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDataBase(app: Application ,callback: TaskDataBase.CallBack) =
                Room.databaseBuilder(app, TaskDataBase::class.java, "task_database")
            .fallbackToDestructiveMigration()
                    .addCallback(callback)
            .build()

    @Provides
    fun provideTaskDao(db :TaskDataBase) =
        db.taskDao()

    @ApplicationScope
    @Provides
    @Singleton
    fun providesApplicationScope() = CoroutineScope(SupervisorJob())
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope