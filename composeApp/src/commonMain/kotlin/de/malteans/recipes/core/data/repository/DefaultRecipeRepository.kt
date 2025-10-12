package de.malteans.recipes.core.data.repository

import de.malteans.recipes.core.data.database.RecipeDao
import de.malteans.recipes.core.data.database.entities.PlanEntity
import de.malteans.recipes.core.data.database.entities.RecipeIngredientEntity
import de.malteans.recipes.core.data.database.entities.RecipeStepEntity
import de.malteans.recipes.core.data.mappers.toDomain
import de.malteans.recipes.core.data.mappers.toEntity
import de.malteans.recipes.core.data.mappers.toIngredientEntity
import de.malteans.recipes.core.data.mappers.toRecipeEntity
import de.malteans.recipes.core.data.network.RemoteRecipeDataSource
import de.malteans.recipes.core.domain.*
import de.malteans.recipes.core.domain.errorHandling.DataError
import de.malteans.recipes.core.domain.errorHandling.Result
import de.malteans.recipes.core.domain.errorHandling.onError
import de.malteans.recipes.core.domain.errorHandling.onSuccess
import de.malteans.recipes.core.presentation.plan.components.TimeOfDay
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.datetime.LocalDate

class DefaultRecipeRepository(
    private val dao: RecipeDao,
    private val remoteDataSource: RemoteRecipeDataSource
) : RecipeRepository {

    override suspend fun upsertRecipe(recipe: Recipe, fromCloud: Boolean): Long {
        // Map domain Recipe to RecipeEntity
        val recipeEntity = recipe.toRecipeEntity()
        // Upsert the recipe and get its ID (using upsert as a combined insert/update)
        var recipeId = dao.upsertRecipe(recipeEntity)
        if (recipeId == -1L) {
            recipeId = recipe.id
        }
        // Remove existing ingredient and step relations
        dao.deleteRecipeIngredientsForRecipe(recipeId)
        dao.deleteRecipeStepsForRecipe(recipeId)

        recipe.ingredients.upsert(false, fromCloud, recipeId)
        recipe.cloudIngredients?.upsert(true, fromCloud, recipeId)

        recipe.steps.upsert(false, recipeId)
        recipe.cloudSteps?.upsert(true, recipeId)

        return recipeId
    }

    private suspend fun List<RecipeIngredientItem>.upsert(isCloudData: Boolean, fromCloud: Boolean, recipeId: Long) {
        // Insert recipe ingredients based on the domain recipe.ingredients list.
        // If the recipe is from the cloud, find ingredients by name or insert them.
        val localIngredients = dao.getAllIngredients().first().associate { it.name.lowercase() to it }
        this.forEach { (ingredient, amount, overrideUnit) ->
            // If from cloud, check for ingredient with same name in local db.
            // -> If found, use the local ingredient, otherwise insert it.
            val localIngredient: Ingredient = if (fromCloud) localIngredients[ingredient.name.lowercase()]?.toDomain()
                    ?: upsertIngredient(ingredient.copy(id = 0L)).let { ingredient.copy(id = it) }
                else ingredient
            val recipeIngredientEntity = RecipeIngredientEntity(
                recipeId = recipeId,
                ingredientId = localIngredient.id,
                amount = amount,
                overrideUnit = overrideUnit
                    ?: if (ingredient.unit != localIngredient.unit) ingredient.unit
                        else null,
                isCloudData = isCloudData,
            )
            dao.upsertRecipeIngredient(recipeIngredientEntity)
        }
    }

    private suspend fun List<String>.upsert(isCloudData: Boolean, recipeId: Long) {
        // Insert recipe steps.
        this.forEachIndexed { index, step ->
            val recipeStepEntity = RecipeStepEntity(
                recipeId = recipeId,
                stepNumber = index,
                description = step,
                duration = null, // TODO: No duration info provided; defaulting to null.
                isCloudData = isCloudData,
            )
            dao.upsertRecipeStep(recipeStepEntity)
        }
    }

    // New function: Save a cloud recipe by upserting it with fromCloud=true.
    override suspend fun saveCloudRecipe(recipe: Recipe): Long {
        return upsertRecipe(recipe, fromCloud = true)
    }

    override suspend fun deleteRecipeById(id: Long) {
        dao.deleteRecipe(id)
    }

    // Updated to perform search directly in the database.
    override fun fetchLocalRecipes(query: String): Flow<List<Recipe>> {
        return dao.searchRecipes(query)
            .map { list ->
                list.map { recipeWithDetails ->
                    recipeWithDetails.toDomain()
                }
            }
    }

    override fun getAllRecipes(): Flow<List<Recipe>> {
        return dao.getAllRecipesWithDetails().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getRecipeById(id: Long): Flow<Recipe> {
        return dao.getRecipeWithDetails(id).map { it.toDomain() }
    }

    override suspend fun upsertIngredient(ingredient: Ingredient): Long {
        return dao.upsertIngredient(ingredient.toIngredientEntity())
    }

    // Fixed: Now calling the correct DAO method to delete an ingredient.
    override suspend fun deleteIngredientById(id: Long) {
        dao.deleteIngredient(id)
    }

    override fun getAllIngredients(): Flow<List<Ingredient>> {
        return dao.getAllIngredients().map { list ->
            list.map { it.toDomain() }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun fetchCloudRecipes(query: String): Flow<Result<List<Recipe>, DataError.Remote>> {
        var result: Flow<Result<List<Recipe>, DataError.Remote>> = flowOf(Result.Error(DataError.Remote.UNKNOWN))
        remoteDataSource
            .fetchRecipes(query)
            .onError { result = flowOf(Result.Error(it)) }
            .onSuccess { dtoList ->
                val cloudOnlyRecipes = dtoList.map { dto -> dto.toDomain() }
                result = dao.getAllRecipesWithDetails()
                    .map { list ->
                        list.associate {
                            it.recipe.cloudId to it.toDomain()
                        }
                    }
                    .flatMapLatest { allLocalRecipes ->
                        flowOf(Result.Success(
                        cloudOnlyRecipes
                            .map { cloudRecipe ->
                                val localRecipe = allLocalRecipes[cloudRecipe.cloudId]
                                if (localRecipe != null) {
                                    Recipe(
                                        id = localRecipe.id,
                                        cloudId = cloudRecipe.cloudId,
                                        sourceUrl = cloudRecipe.sourceUrl,
                                        name = localRecipe.name,
                                        cloudName = cloudRecipe.cloudName,
                                        description = localRecipe.description,
                                        cloudDescription = cloudRecipe.cloudDescription,
                                        imageUrl = localRecipe.imageUrl,
                                        cloudImageUrl = cloudRecipe.cloudImageUrl,
                                        ingredients = localRecipe.ingredients,
                                        cloudIngredients = cloudRecipe.cloudIngredients,
                                        steps = localRecipe.steps,
                                        cloudSteps = cloudRecipe.cloudSteps,
                                        workTime = localRecipe.workTime,
                                        cloudWorkTime = cloudRecipe.cloudWorkTime,
                                        totalTime = localRecipe.totalTime,
                                        cloudTotalTime = cloudRecipe.cloudTotalTime,
                                        servings = localRecipe.servings,
                                        cloudServings = cloudRecipe.cloudServings,
                                        rating = localRecipe.rating,
                                        onlineRating = cloudRecipe.onlineRating,
                                    )
                                }
                                else cloudRecipe
                            }
                        ))
                    }
            }
        return result
    }

    // New functions for planning recipes:

    // Insert a new plan record for the given recipe and date.
    override suspend fun planRecipe(recipeId: Long, date: LocalDate, timeOfDay: TimeOfDay): Long {
        return dao.upsertPlan(PlanEntity(recipeId = recipeId, date = date, timeOfDay = timeOfDay))
    }

    override suspend fun planRecipe(plannedRecipe: PlannedRecipe): Long {
        return dao.upsertPlan(plannedRecipe.toEntity())
    }

    // Update an existing plan with new information.
    override suspend fun updatePlan(plannedRecipe: PlannedRecipe) {
        dao.upsertPlan(plannedRecipe.toEntity())
    }

    // Remove a planned recipe by its plan id.
    override suspend fun deletePlan(planId: Long) {
        dao.deletePlan(planId)
    }

    // Combine plan records with recipes (using details) to produce a list of planned recipes.
    override fun getPlannedRecipes(): Flow<List<PlannedRecipe>> {
        return combine(
            dao.getAllPlans(),
            dao.getAllRecipesWithDetails()
        ) { planEntities, recipesWithDetails ->
            planEntities.mapNotNull { planEntity ->
                val recipeWithDetails = recipesWithDetails.find { it.recipe.id == planEntity.recipeId }
                recipeWithDetails?.toDomain()?.let { recipe ->
                    planEntity.toDomain(recipe)
                }
            }
        }
    }
}