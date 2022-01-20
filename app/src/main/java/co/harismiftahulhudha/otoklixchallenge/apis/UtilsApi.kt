package co.harismiftahulhudha.otoklixchallenge.apis

import co.harismiftahulhudha.otoklixchallenge.apis.apiservices.PostApiServices
import javax.inject.Inject

class UtilsApi @Inject constructor(private val retrofitClient: RetrofitClient) {
    fun postApiService(): PostApiServices {
        return retrofitClient.getClient()!!.create(PostApiServices::class.java)
    }
}