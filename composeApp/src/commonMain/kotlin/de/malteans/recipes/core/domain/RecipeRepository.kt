package de.malteans.recipes.core.domain

import de.malteans.recipes.core.presentation.plan.components.TimeOfDay
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface RecipeRepository {
    suspend fun upsertRecipe(recipe: Recipe, fromCloud: Boolean = false): Long
    suspend fun deleteRecipeById(id: Long)
    fun fetchLocalRecipes(query: String): Flow<List<Recipe>>
    fun getAllRecipes(): Flow<List<Recipe>>
    fun getRecipeById(id: Long): Flow<Recipe>

    suspend fun upsertIngredient(ingredient: Ingredient): Long
    suspend fun deleteIngredientById(id: Long)
    fun getAllIngredients(): Flow<List<Ingredient>>

    fun fetchCloudRecipes(query: String): Flow<kotlin.Result<List<Recipe>>>
    suspend fun saveCloudRecipe(recipe: Recipe): Long  // New function for cloud recipes
    suspend fun uploadLocalRecipe(recipe: Recipe): kotlin.Result<Long>

    suspend fun planRecipe(recipeId: Long, date: LocalDate, timeOfDay: TimeOfDay): Long
    suspend fun planRecipe(plannedRecipe: PlannedRecipe): Long
    suspend fun updatePlan(plannedRecipe: PlannedRecipe)
    suspend fun deletePlan(planId: Long)
    fun getPlannedRecipes(): Flow<List<PlannedRecipe>>
}
