package co.harismiftahulhudha.otoklixchallenge.apis

import co.halokak.app.utils.LiveDataCallAdapterFactory
import co.harismiftahulhudha.otoklixchallenge.BuildConfig.API
import co.harismiftahulhudha.otoklixchallenge.BuildConfig.DEBUG
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit


class RetrofitClient {
    private var retrofit: Retrofit? = null

    fun getClient(): Retrofit? {
        val interceptor = HttpLoggingInterceptor()
        val BASE_URL: String = if (DEBUG) {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            API
        } else {
            interceptor.setLevel(HttpLoggingInterceptor.Level.NONE)
            API
        }
        try {
            val timeout = 7
            val client = OkHttpClient.Builder()
                .connectTimeout(timeout.toLong(), TimeUnit.MINUTES)
                .readTimeout(timeout.toLong(), TimeUnit.MINUTES)
                .writeTimeout(timeout.toLong(), TimeUnit.MINUTES)
                .addInterceptor(interceptor).build()
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(LiveDataCallAdapterFactory())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return retrofit
    }
}