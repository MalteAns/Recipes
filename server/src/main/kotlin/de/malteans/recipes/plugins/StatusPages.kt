package de.malteans.recipes.plugins

import de.malteans.recipes.dto.ErrorDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureStatusPages() {
    install(StatusPages) {
        val httpClient = HttpClient(CIO)

        suspend fun respondCat(call: ApplicationCall, status: HttpStatusCode) {
            val bytes: ByteArray = httpClient.get("https://http.cat/${status.value}.jpg").body()
            call.respondBytes(bytes, ContentType.Image.JPEG, status)
        }

//        exception<AbortRoute> { call, _ -> /* already responded */ }
        exception<Throwable> { call, cause ->
            // TODO: log cause
            cause.printStackTrace()
            call.respond(HttpStatusCode.InternalServerError, ErrorDto("server_error", cause.message ?: ""))
        }

        status(
            HttpStatusCode.BadRequest, HttpStatusCode.Unauthorized, HttpStatusCode.Forbidden,
            HttpStatusCode.NotFound, HttpStatusCode.InternalServerError
        ) { call, status ->
            if (status.value in 400..599) {
                respondCat(call, status)
            }
        }
    }
}