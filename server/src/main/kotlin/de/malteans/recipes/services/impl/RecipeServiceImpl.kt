package de.malteans.recipes.services.impl

import de.malteans.recipes.db.IngredientsTable
import de.malteans.recipes.db.RecipesTable
import de.malteans.recipes.db.StepsTable
import de.malteans.recipes.dto.AddRecipeDto
import de.malteans.recipes.dto.IngredientDto
import de.malteans.recipes.dto.RecipeDto
import de.malteans.recipes.dto.StepDto
import de.malteans.recipes.services.RecipeService
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class RecipeServiceImpl(
    private val db: Database,
): RecipeService {
    override suspend fun getAllRecipes(query: String) = transaction(db) {
        val recipes = RecipesTable
            .selectAll()
            .where { RecipesTable.name.lowerCase() like "%${query.lowercase()}%" }
            .orderBy(RecipesTable.name, SortOrder.ASC)
            .map { recipesRow ->
                recipesRow.toRecipeDto()
            }
        val ingredients = IngredientsTable
            .selectAll()
            .where { IngredientsTable.recipeId inList recipes.map { it.id.toInt() } }
            .map { ingredientRow ->
                ingredientRow[IngredientsTable.recipeId] to ingredientRow.toIngredientDto()
            }
            .groupBy { (recipeId, _) -> recipeId }
            .mapValues { entry -> entry.value.map { it.second } }
        val steps = StepsTable
            .selectAll()
            .where { StepsTable.recipeId inList recipes.map { it.id.toInt() } }
            .orderBy(StepsTable.stepNumber)
            .map { stepRow ->
                stepRow[StepsTable.recipeId] to stepRow.toStepDto()
            }
            .groupBy { (recipeId, _) -> recipeId }
            .mapValues { entry -> entry.value.map { it.second } }
        return@transaction recipes.map { recipe ->
            recipe.copy(
                ingredients = ingredients[recipe.id.toInt()] ?: emptyList(),
                steps = steps[recipe.id.toInt()] ?: emptyList(),
            )
        }
    }

    override suspend fun addRecipe(recipeDto: AddRecipeDto): Int {
        return transaction(db) {
            var existingRecipeId: Int? = null
            if (!recipeDto.sourceUrl.isNullOrEmpty()) {
                existingRecipeId = RecipesTable
                    .selectAll()
                    .where { RecipesTable.sourceUrl eq recipeDto.sourceUrl }
                    .singleOrNull()
                    ?.get(RecipesTable.id)
            }

            val recipeId = existingRecipeId ?: ((RecipesTable
                .selectAll()
                .maxByOrNull {
                    it[RecipesTable.id]
                }
                ?.get(RecipesTable.id)
                ?: 0) + 1)

            saveRecipeData(recipeId, recipeDto, isUpdate = existingRecipeId != null)
            return@transaction recipeId
        }
    }

    override suspend fun updateRecipe(id: Int, recipeDto: AddRecipeDto) {
        transaction(db) {
            saveRecipeData(id, recipeDto, isUpdate = true)
        }
    }

    private fun saveRecipeData(recipeId: Int, recipeDto: AddRecipeDto, isUpdate: Boolean) {
        if (isUpdate) {
            RecipesTable.update({ RecipesTable.id eq recipeId }) { row ->
                row[name] = recipeDto.name
                row[description] = recipeDto.description
                row[imageUrl] = recipeDto.imageUrl
                row[workTime] = recipeDto.workTime
                row[totalTime] = recipeDto.totalTime
                row[servings] = recipeDto.servings
                row[onlineRating] = recipeDto.onlineRating
                row[sourceUrl] = recipeDto.sourceUrl
            }
            IngredientsTable.deleteWhere { IngredientsTable.recipeId eq recipeId }
            StepsTable.deleteWhere { StepsTable.recipeId eq recipeId }
        } else {
            RecipesTable.insert { row ->
                row[id] = recipeId
                row[name] = recipeDto.name
                row[description] = recipeDto.description
                row[imageUrl] = recipeDto.imageUrl
                row[workTime] = recipeDto.workTime
                row[totalTime] = recipeDto.totalTime
                row[servings] = recipeDto.servings
                row[onlineRating] = recipeDto.onlineRating
                row[sourceUrl] = recipeDto.sourceUrl
                row[addedAt] = Instant.now()
            }
        }

        recipeDto.ingredients.forEach { ingredient ->
            val ingredientId = (IngredientsTable
                .selectAll()
                .maxByOrNull {
                    it[IngredientsTable.id]
                }
                ?.get(IngredientsTable.id)
                ?: 1) + 1
            IngredientsTable.insert { row ->
                row[id] = ingredientId
                row[this.recipeId] = recipeId
                row[ingredientName] = ingredient.name
                row[ingredientAmount] = ingredient.amount
                row[ingredientUnit] = ingredient.unit
            }
        }
        recipeDto.steps.forEachIndexed { index, stepDto ->
            val stepId = (StepsTable
                .selectAll()
                .maxByOrNull {
                    it[StepsTable.id]
                }
                ?.get(StepsTable.id)
                ?: 1) + 1
            StepsTable.insert { row ->
                row[id] = stepId
                row[this.recipeId] = recipeId
                row[stepNumber] = index + 1
                row[description] = stepDto.description
                row[duration] = stepDto.duration
            }
        }
    }

    private fun ResultRow.toRecipeDto(
        ingredients: List<IngredientDto> = emptyList(),
        steps: List<StepDto> = emptyList(),
    ) = RecipeDto(
        id = this[RecipesTable.id].toLong(),
        name = this[RecipesTable.name],
        description = this[RecipesTable.description],
        imageUrl = this[RecipesTable.imageUrl],
        ingredients = ingredients,
        steps = steps,
        workTime = this[RecipesTable.workTime],
        totalTime = this[RecipesTable.totalTime],
        servings = this[RecipesTable.servings],
        rating = this[RecipesTable.rating],
        onlineRating = this[RecipesTable.onlineRating],
        sourceUrl = this[RecipesTable.sourceUrl],
    )

    private fun ResultRow.toIngredientDto() = IngredientDto(
        id = this[IngredientsTable.id].toLong(),
        name = this[IngredientsTable.ingredientName],
        amount = this[IngredientsTable.ingredientAmount],
        unit = this[IngredientsTable.ingredientUnit]
    )

    private fun ResultRow.toStepDto() = StepDto(
        id = this[StepsTable.id].toLong(),
        stepNumber = this[StepsTable.stepNumber],
        description = this[StepsTable.description],
        duration = this[StepsTable.duration],
    )
}