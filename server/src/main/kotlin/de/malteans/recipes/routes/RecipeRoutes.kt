package de.malteans.recipes.routes

import de.malteans.recipes.dto.recipe.RecipesResponseDto
import de.malteans.recipes.dto.recipe.add.AddRecipeDto
import de.malteans.recipes.dto.recipe.add.AddRecipeResponseDto
import de.malteans.recipes.services.RecipeService
import de.malteans.recipes.services.impl.RecipeServiceImpl
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
        recipeService.addRecipe(recipeDto)
            .onSuccess { id ->
                call.respond<AddRecipeResponseDto>(
                    HttpStatusCode.Created,
                    AddRecipeResponseDto.AddRecipeSuccessDto(newRecipeCloudId = id.toLong()),
                )
            }
            .onFailure { exception ->
                println("Failed to add recipe: ${exception.message}")
                when (exception) {
                    is RecipeServiceImpl.Companion.RecipeAlreadyExistsException -> call.respond<AddRecipeResponseDto>(
                        HttpStatusCode.Conflict,
                        AddRecipeResponseDto.AddRecipeErrorDto(
                            existingRecipeId = exception.existingRecipeDto.id,
                            existingRecipeName = exception.existingRecipeDto.name,
                        )
                    )
                    else -> call.respond(HttpStatusCode.InternalServerError)
                }
            }
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