package co.harismiftahulhudha.otoklixchallenge.di

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import co.harismiftahulhudha.otoklixchallenge.BuildConfig
import co.harismiftahulhudha.otoklixchallenge.BuildConfig.DATABASE_NAME
import co.harismiftahulhudha.otoklixchallenge.database.AppDatabase
import co.harismiftahulhudha.otoklixchallenge.helpers.FormatStringHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application, callback: AppDatabase.Callback) =
        if (BuildConfig.DEBUG) {
            Room.databaseBuilder(app, AppDatabase::class.java, DATABASE_NAME)
                .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
                .fallbackToDestructiveMigration()
                .addCallback(callback)
                .build()
        } else {
            Room.databaseBuilder(app, AppDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .addCallback(callback)
                .build()
        }

    @Provides
    @Singleton
    fun providePostDao(db: AppDatabase) = db.postDao()

    @Provides
    @Singleton
    fun provideFormatStringHelper() = FormatStringHelper()

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope