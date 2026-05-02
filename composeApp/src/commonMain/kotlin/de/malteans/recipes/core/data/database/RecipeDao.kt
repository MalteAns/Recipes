package de.malteans.recipes.core.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import de.malteans.recipes.core.data.database.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    // Recipe Methods ---------------------------------------------------
    @Upsert
    suspend fun upsertRecipe(recipe: RecipeEntity): Long

    @Query("DELETE FROM RecipeEntity WHERE id = :recipeId")
    suspend fun deleteRecipe(recipeId: Long)

    @Upsert
    suspend fun upsertRecipeIngredient(recipeIngredient: RecipeIngredientEntity)

    @Upsert
    suspend fun upsertRecipeStep(recipeStep: RecipeStepEntity)

    @Query("DELETE FROM RecipeIngredientEntity WHERE recipeId = :recipeId")
    suspend fun deleteRecipeIngredientsForRecipe(recipeId: Long)

    @Query("DELETE FROM RecipeStepEntity WHERE recipeId = :recipeId")
    suspend fun deleteRecipeStepsForRecipe(recipeId: Long)

    @Query("SELECT * FROM RecipeEntity")
    suspend fun getAllRecipes(): List<RecipeEntity>

    // Get recipe details with ingredients and steps
    @Transaction
    @Query("SELECT * FROM RecipeEntity WHERE id = :recipeId")
    fun getRecipeWithDetails(recipeId: Long): Flow<RecipeWithDetails?>

    @Transaction
    @Query("SELECT * FROM RecipeEntity ORDER BY name COLLATE NOCASE ASC")
    fun getAllRecipesWithDetails(): Flow<List<RecipeWithDetails>>

    // New DAO method: search for recipes by name using a LIKE query.
    @Transaction
    @Query("SELECT * FROM RecipeEntity WHERE name LIKE '%' || :query || '%' ORDER BY name COLLATE NOCASE ASC")
    fun searchRecipes(query: String): Flow<List<RecipeWithDetails>>

    // New DAO method: get recipe by cloud id
    @Query("SELECT * FROM RecipeEntity WHERE cloudId = :cloudId LIMIT 1")
    suspend fun getRecipeByCloudId(cloudId: Long): RecipeEntity?

    // Ingredient Methods -------------------------------------------------------------------------
    @Upsert
    suspend fun upsertIngredient(ingredient: IngredientEntity): Long

    @Query("DELETE FROM IngredientEntity WHERE id = :ingredientId")
    suspend fun deleteIngredient(ingredientId: Long)

    @Query("SELECT * FROM ingrediententity")
    fun getAllIngredients(): Flow<List<IngredientEntity>>

    // Plan Methods -------------------------------------------------------------------------------
    @Upsert
    suspend fun upsertPlan(plan: PlanEntity): Long

    @Query("DELETE FROM PlanEntity WHERE id = :planId")
    suspend fun deletePlan(planId: Long)

    @Query("SELECT * FROM PlanEntity")
    fun getAllPlans(): Flow<List<PlanEntity>>
}
