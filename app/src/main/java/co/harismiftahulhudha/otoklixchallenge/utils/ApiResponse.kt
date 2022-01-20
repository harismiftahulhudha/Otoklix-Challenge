package co.harismiftahulhudha.otoklixchallenge.utils

import co.harismiftahulhudha.otoklixchallenge.mvvm.models.ErrorModel
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import java.net.SocketTimeoutException

private const val TAG = "ApiResponse"

sealed class ApiResponse<T> {

    companion object {
        fun <T> create(error: Throwable): ApiResponse<T> {
            return if (error is SocketTimeoutException) {
                ApiTimeoutResponse("ConnectionTimeout")
            } else {
                ApiErrorResponse(
                    error.message ?: "unknown error",
                    400,
                    null
                )
            }
        }

        fun <T> create(response: Response<T>): ApiResponse<T> {
            if (response.isSuccessful) {
                val body = response.body()
                val code = response.code()
                return if (body == null || code == 204) {
                    ApiEmptyResponse()
                } else if (code == 503 || code == 401) {
                    ApiUnAuthorizedResponse(
                        "401 Unauthorized. Token may be invalid.",
                        code
                    )
                } else {
                    ApiSuccessResponse(body)
                }
            } else {
                try {
                    val jsonObject = JSONObject(response.errorBody()!!.string()).toString()
                    val errorModel: ErrorModel = Gson().fromJson(jsonObject, ErrorModel::class.java)
                    val msg = "error"
                    val code: Int = response.code()
                    return if (code == 503 || code == 401) {
                        ApiUnAuthorizedResponse(
                            msg,
                            code
                        )
                    } else {
                        ApiErrorResponse(
                            msg,
                            code,
                            errorModel
                        )
                    }
                } catch (e: JSONException) {
                    val msg = "502 Bad Gateway"
                    val code: Int = response.code()
                    return ApiErrorResponse(
                        msg,
                        code,
                        null
                    )
                }
            }
        }
    }
}

/**
 * separate class for HTTP 204 responses so that we can make ApiSuccessResponse's body non-null.
 */
class ApiEmptyResponse<T> : ApiResponse<T>()

data class ApiSuccessResponse<T>(val body: T) : ApiResponse<T>() {}

data class ApiErrorResponse<T>(val errorMessage: String, val code: Int, val errorModel: ErrorModel?) : ApiResponse<T>()

data class ApiTimeoutResponse<T>(val errorMessage: String) : ApiResponse<T>()

data class ApiUnAuthorizedResponse<T>(val errorMessage: String, val code: Int) : ApiResponse<T>()