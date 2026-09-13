package com.example.tasteindia.domain.model

/**
 * Domain-level sealed result model representing repository operational results.
 */
sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Error(val message: String, val cause: Throwable? = null) : Result<Nothing>
}
