package co.harismiftahulhudha.otoklixchallenge.utils

import co.harismiftahulhudha.otoklixchallenge.mvvm.models.ErrorModel

class Resource<out T>(val status: Status, val data: T?, val message: String?, val code: Int?, var errorModel: ErrorModel?) {

    enum class Status {
        SUCCESS, ERROR, LOADING, TIMEOUT, UNAUTHORIZED
    }

    companion object {
        fun <T> success(data: T): Resource<T> {
            return Resource(Status.SUCCESS, data, null, null, null)
        }

        fun <T> error(data: T?, msg: String, code: Int, errorModel: ErrorModel?): Resource<T> {
            return Resource(Status.ERROR, data, msg, code, errorModel)
        }

        fun <T> loading(data: T?): Resource<T> {
            return Resource(Status.LOADING, data, null, null, null)
        }

        fun <T> timeout(data: T?, msg: String): Resource<T> {
            return Resource(Status.TIMEOUT, data, msg, null, null)
        }

        fun <T> unauthorized(data: T?, msg: String, code: Int): Resource<T> {
            return Resource(Status.UNAUTHORIZED, data, msg, code, null)
        }
    }

}