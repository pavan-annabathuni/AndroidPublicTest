package com.waycool.data.utils

class NetworkResource<T> private constructor(val status: NetworkResource.Status, val data: T?, val apiError:String?) {
    enum class Status {
        SUCCESS, ERROR, LOADING
    }
    companion object {
        fun <T> success(data: T?): NetworkResource<T> {
            return NetworkResource(Status.SUCCESS, data, null)
        }
        fun <T> error(apiError: String?): NetworkResource<T> {
            return NetworkResource(Status.ERROR, null, apiError)
        }
        fun <T> loading(data: T?): NetworkResource<T> {
            return NetworkResource(Status.LOADING, data, null)
        }
    }
}