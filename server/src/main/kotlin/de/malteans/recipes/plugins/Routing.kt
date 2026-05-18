package de.malteans.recipes.plugins

import de.malteans.recipes.routes.registerImageRoutes
import de.malteans.recipes.routes.registerRecipeRoutes
import de.malteans.recipes.services.ImageService
import de.malteans.recipes.services.RecipeService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.io.File


fun Application.configureRouting(
//    tokenConfig: TokenConfig,
) {
    val recipeService by inject<RecipeService>()
    val imageService by inject<ImageService>()
    routing {
        staticFiles("/uploads", File("serverUploads"))
        route("/v2") {
            get("/health") { call.respond(mapOf("status" to "ok")) }
            authenticate("bearer") {
                registerRecipeRoutes(
                    recipeService = recipeService
                )
                registerImageRoutes(
                    imageService = imageService
                )
            }
        }
    }
}