package com.theost.workchat.data.models.core

import com.theost.workchat.data.models.state.ResourceStatus

sealed class Resource<T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(val data: T?, val error: Throwable) : Resource<T>()
    data class Loading<T>(val data: T?) : Resource<T>()
}

data class RxResource<out T>(val status: ResourceStatus, val data: T?, val error: Throwable?) {
    companion object {
        fun <T> success(data: T?): RxResource<T> {
            return RxResource(ResourceStatus.SUCCESS, data, null)
        }

        fun <T> error(error: Throwable, data: T?): RxResource<T> {
            return RxResource(ResourceStatus.ERROR, data, error)
        }

        fun <T> loading(data: T?): RxResource<T> {
            return RxResource(ResourceStatus.LOADING, data, null)
        }
    }
}