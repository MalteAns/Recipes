package de.malteans.recipes.core.presentation.components

import de.malteans.recipes.core.data.network.HttpStatusException
import de.malteans.recipes.core.domain.errorHandling.DataError
import io.ktor.client.call.*
import io.ktor.client.network.sockets.*
import io.ktor.http.*
import io.ktor.util.network.*
import recipes.composeapp.generated.resources.*

fun DataError.toUiText(): UiText {
    val stringRes = when(this) {
        DataError.Local.DISK_FULL -> Res.string.error_disk_full
        DataError.Local.UNKNOWN -> Res.string.error_unknown
        DataError.Remote.REQUEST_TIMEOUT -> Res.string.error_request_timeout
        DataError.Remote.TOO_MANY_REQUESTS -> Res.string.error_too_many_requests
        DataError.Remote.NO_INTERNET -> Res.string.error_no_internet
        DataError.Remote.SERVER -> Res.string.error_unknown
        DataError.Remote.SERIALIZATION -> Res.string.error_serialization
        DataError.Remote.UNKNOWN -> Res.string.error_unknown
    }
    
    return UiText.FromStringResource(stringRes)
}

fun Throwable.toUiText(): UiText = UiText.FromStringResource(
    when (this) {
        is HttpStatusException -> when (statusCode.value) {
            HttpStatusCode.RequestTimeout.value -> Res.string.error_request_timeout
            HttpStatusCode.TooManyRequests.value -> Res.string.error_too_many_requests
            in 500..599 -> Res.string.server_error
            else -> Res.string.error_unknown
        }
        is SocketTimeoutException -> Res.string.error_request_timeout
        is UnresolvedAddressException -> Res.string.error_no_internet
        is NoTransformationFoundException -> Res.string.error_serialization
        else -> Res.string.error_unknown
    }
)
