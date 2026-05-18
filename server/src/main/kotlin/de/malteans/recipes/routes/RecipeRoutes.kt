package de.malteans.recipes.routes

import de.malteans.recipes.dto.AddRecipeDto
import de.malteans.recipes.dto.RecipesResponseDto
import de.malteans.recipes.services.RecipeService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.registerRecipeRoutes(
    recipeService: RecipeService
) {
    get("/recipes") {
        val query = call.request.queryParameters["query"]
        val recipes = recipeService.getAllRecipes(query ?: "")
        call.respond(
            HttpStatusCode.OK,
            RecipesResponseDto(recipes),
        )
    }
    post("/recipes") {
        val recipeDto = call.receive<AddRecipeDto>()
        val id = recipeService.addRecipe(recipeDto)
        call.respond(HttpStatusCode.Created, id)
    }
    post("/recipes/{id}") {
        val recipeId = call.parameters["id"]?.toIntOrNull()
        if (recipeId == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid recipe ID")
            return@post
        }
        val recipeDto = call.receive<AddRecipeDto>()
        recipeService.updateRecipe(recipeId, recipeDto)
        call.respond(HttpStatusCode.OK)
    }
}