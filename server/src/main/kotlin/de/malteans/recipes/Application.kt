package de.malteans.recipes

import de.malteans.recipes.di.module
import de.malteans.recipes.plugins.configureRouting
import de.malteans.recipes.plugins.configureStatusPages
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.defaultheaders.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun main() {
    embeddedServer(Netty, port = Constants.SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(DefaultHeaders)
    install(AutoHeadResponse)
    install(CallLogging)
    install(ContentNegotiation) { json() }
    configureStatusPages()

    install(Koin) {
        slf4jLogger()
        modules(
            module
        )
    }

    install(Authentication) {
        bearer("bearer") {
            authenticate { tokenCredential ->
                if (tokenCredential.token == System.getenv("API_TOKEN")) UserIdPrincipal("api-user") else null
            }
        }
    }

    configureRouting()
}