package com.example.tasteindia.data.remote

import retrofit2.Response

/**
 * Sealed interface representing network execution outcomes safely.
 */
sealed interface NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>
    data class HttpError(val code: Int, val message: String?) : NetworkResult<Nothing>
    data class NetworkError(val throwable: Throwable) : NetworkResult<Nothing>
    data class SerializationError(val throwable: Throwable) : NetworkResult<Nothing>
    data class UnexpectedError(val throwable: Throwable) : NetworkResult<Nothing>
}

/**
 * Executes an API call safely, capturing network/HTTP/serialization errors without crashing.
 */
suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> {
    return try {
        val response = apiCall()
        val body = response.body()
        if (response.isSuccessful && body != null) {
            NetworkResult.Success(body)
        } else {
            NetworkResult.HttpError(response.code(), response.message())
        }
    } catch (e: kotlinx.serialization.SerializationException) {
        NetworkResult.SerializationError(e)
    } catch (e: java.io.IOException) {
        NetworkResult.NetworkError(e)
    } catch (e: Exception) {
        NetworkResult.UnexpectedError(e)
    }
}
