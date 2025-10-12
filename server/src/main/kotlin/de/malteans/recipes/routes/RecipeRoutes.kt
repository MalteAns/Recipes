package de.malteans.recipes.routes

import de.malteans.recipes.dto.RecipesResponseDto
import de.malteans.recipes.services.RecipeService
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.registerRecipeRoutes(
    recipeService: RecipeService
) {
    get("/recipes") {
        val query = call.request.queryParameters["query"]
        val recipes = recipeService.getAllRecipes(query ?: "")
        call.respond(
            RecipesResponseDto(recipes)
        )
    }
}