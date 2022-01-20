package co.harismiftahulhudha.otoklixchallenge.di

import co.harismiftahulhudha.otoklixchallenge.apis.RetrofitClient
import co.harismiftahulhudha.otoklixchallenge.apis.UtilsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofitClient() = RetrofitClient()

    @Provides
    @Singleton
    fun provideUtilsApi(retrofitClient: RetrofitClient) = UtilsApi(retrofitClient)
}