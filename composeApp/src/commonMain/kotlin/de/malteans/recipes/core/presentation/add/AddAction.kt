package de.malteans.recipes.core.presentation.add

import de.malteans.recipes.core.domain.Ingredient

sealed interface AddAction {
    data object OnRecipeAdd : AddAction
    data class OnRecipeShow(val id: Long) : AddAction
    data object OnBack: AddAction
    data object OnClear : AddAction

    data class OnNameChange(val name: String) : AddAction
    data class OnDescriptionChange(val description: String) : AddAction
    data class OnImageUrlChange(val imageUrl: String) : AddAction
    data class OnIngredientCreate(val ingredient: Ingredient) : AddAction
    data class OnIngredientAdd(val ingredient: Ingredient) : AddAction
    data class OnIngredientEdit(val ingredient: Ingredient) : AddAction
    data class OnIngredientChange(val ingredient: Ingredient, val amount: Double?, val overrideUnit: String?) : AddAction
    data class OnIngredientRemove(val ingredient: Ingredient) : AddAction
    data class OnStepAdd(val index: Int) : AddAction
    data class OnStepChange(val newValue: String, val index: Int) : AddAction
    data class OnStepRemove(val index: Int) : AddAction
    data class OnWorkTimeChange(val time: Int?) : AddAction
    data class OnTotalTimeChange(val time: Int?) : AddAction
    data class OnServingsChange(val servings: Int?) : AddAction
    data class OnRatingChange(val rating: Int?) : AddAction

    data class OnTabSelect(val index: Int) : AddAction
    data object OnIngredientDialogDismiss : AddAction
}