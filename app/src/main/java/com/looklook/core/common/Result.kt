package com.looklook.core.common

sealed class Result<out T> {
    data class Success<T>(val value: T) : Result<T>()
    data class Error(val throwable: Throwable) : Result<Nothing>()
}

