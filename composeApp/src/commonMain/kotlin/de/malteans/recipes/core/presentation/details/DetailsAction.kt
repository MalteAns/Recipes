package de.malteans.recipes.core.presentation.details

import de.malteans.recipes.core.domain.Recipe
import de.malteans.recipes.core.presentation.plan.components.TimeOfDay
import kotlinx.datetime.LocalDate

sealed interface DetailsAction {
    data class OnSelectedRecipeChange(val recipe: Recipe) : DetailsAction

    data object OnBack : DetailsAction
    data object OnDelete : DetailsAction
    data object OnEdit : DetailsAction
    data object OnSave : DetailsAction  // New action: trigger saving the cloud recipe locally

    data object ShowPlanDialog : DetailsAction
    data object DismissPlanDialog : DetailsAction
    data class OnPlan(val date: LocalDate, val timeOfDay: TimeOfDay) : DetailsAction

    data class OnTabSelected(val index: Int) : DetailsAction

    data class CustomServings(val servings: Int?) : DetailsAction
    data class OnRatingChanged(val newRating: Int?) : DetailsAction
}
