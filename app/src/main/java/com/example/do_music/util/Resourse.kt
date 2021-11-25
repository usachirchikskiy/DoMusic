package com.example.do_music.util

data class Resource<T>(
    var data: T? = null,
    val error: Throwable? = null,
    val isLoading: Boolean = false
) {

    companion object{
        fun <T>success(data: T? =null) : Resource<T>{
            return Resource(data = data)
        }
        fun <T>loading(data: T? = null) : Resource<T> = Resource(isLoading = true)

        fun <T>error(throwable: Throwable, data: T? = null) : Resource<T> = Resource(error=throwable)
    }

}