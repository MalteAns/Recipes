package de.malteans.recipes.core.domain

data class RecipeIngredientItem(
    val ingredient: Ingredient,
    val amount: Double? = null,
    val overrideUnit: String? = null,
)
