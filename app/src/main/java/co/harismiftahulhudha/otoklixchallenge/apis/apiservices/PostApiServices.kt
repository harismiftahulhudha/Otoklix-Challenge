package co.harismiftahulhudha.otoklixchallenge.apis.apiservices

import androidx.lifecycle.LiveData
import co.harismiftahulhudha.otoklixchallenge.mvvm.models.PostModel
import co.harismiftahulhudha.otoklixchallenge.utils.ApiResponse
import retrofit2.http.*

interface PostApiServices {
    @GET("posts")
    fun list(): LiveData<ApiResponse<MutableList<PostModel>>>

    @GET("posts/{id}")
    fun show(@Path("id") id: Long): LiveData<ApiResponse<PostModel>>

    @FormUrlEncoded
    @POST("posts")
    fun create(
        @Field("title") title: String,
        @Field("content") content: String,
    ): LiveData<ApiResponse<PostModel>>

    @FormUrlEncoded
    @PUT("posts/{id}")
    fun update(
        @Path("id") id: Long,
        @Field("title") title: String,
        @Field("content") content: String,
    ): LiveData<ApiResponse<PostModel>>

    @DELETE("posts/{id}")
    fun delete(@Path("id") id: Long): LiveData<ApiResponse<PostModel>>
}
