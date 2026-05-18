package de.malteans.recipes.core.data.network

import de.malteans.recipes.core.domain.errorHandling.DataError
import de.malteans.recipes.core.domain.errorHandling.Result
import io.ktor.client.call.*
import io.ktor.client.network.sockets.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.network.*
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

@Deprecated("Use safeCall instead")
suspend inline fun <reified T> oldSafeCall(
    execute: () -> HttpResponse
): Result<T, DataError.Remote> {
    val response = try {
        execute()
    } catch(_: SocketTimeoutException) {
        return Result.Error(DataError.Remote.REQUEST_TIMEOUT)
    } catch(_: UnresolvedAddressException) {
        return Result.Error(DataError.Remote.NO_INTERNET)
    } catch (_: Exception) {
        currentCoroutineContext().ensureActive()
        return Result.Error(DataError.Remote.UNKNOWN)
    }

    return oldResponseToResult(response)
}

@Deprecated("Use responseToResult instead")
suspend inline fun <reified T> oldResponseToResult(
    response: HttpResponse
): Result<T, DataError.Remote> {
    return when(response.status.value) {
        in 200..299 -> {
            try {
                Result.Success(response.body<T>())
            } catch(_: NoTransformationFoundException) {
                Result.Error(DataError.Remote.SERIALIZATION)
            }
        }
        408 -> Result.Error(DataError.Remote.REQUEST_TIMEOUT)
        429 -> Result.Error(DataError.Remote.TOO_MANY_REQUESTS)
        in 500..599 -> Result.Error(DataError.Remote.SERVER)
        else -> Result.Error(DataError.Remote.UNKNOWN)
    }
}

suspend inline fun <reified T> safeCall(
    execute: () -> HttpResponse
): kotlin.Result<T> {
    val response = try {
        execute()
    } catch (e: Exception) {
        currentCoroutineContext().ensureActive()
        return kotlin.Result.failure(e)
    }
    return responseToResult(response)
}

suspend inline fun <reified T> responseToResult(
    response: HttpResponse
): kotlin.Result<T> {
    return when(response.status.value) {
        in 200..299 -> {
            try {
                kotlin.Result.success(response.body<T>())
            } catch(e: NoTransformationFoundException) {
                kotlin.Result.failure(e)
            }
        }
        else -> {
            val data = try {
                response.body<T>()
            } catch(_: NoTransformationFoundException) {
                response.bodyAsText().ifBlank { null }
            }
            kotlin.Result.failure(HttpStatusException(response.status, data))
        }
    }
}

data class HttpStatusException(val statusCode: HttpStatusCode, val data: Any? = null) : Exception()