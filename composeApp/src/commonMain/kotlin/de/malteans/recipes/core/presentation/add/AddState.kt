package de.malteans.recipes.core.presentation.add

import de.malteans.recipes.core.domain.Ingredient
import de.malteans.recipes.core.domain.Recipe

data class AddState(
    val name: String = "",
    val description: String = "",
    val rating: Int? = null,
    val servings: Int? = null,
    val workTime: Int? = null,
    val totalTime: Int? = null,
    val imageUrl: String = "",
    val sourceUrl: String = "",
    val ingredients: Map<Ingredient, Pair<Double?, String?>> = emptyMap(),
    val steps: List<String> = listOf(""),

    val selectedTabIndex: Int = 0,
    val showIngredientDialog: Boolean = false,
    val currentIngredient: Ingredient? = null,
    val isEditingIngredient: Boolean = false,
    val imageUploadInProgress: Boolean = false,
    val imageUploadError: Throwable? = null,

    val allIngredients: List<Ingredient> = emptyList(),

    // Fields for Editing
    val editingRecipe: Recipe? = null,
)
