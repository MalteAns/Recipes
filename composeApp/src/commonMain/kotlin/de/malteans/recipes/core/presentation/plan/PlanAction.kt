package de.malteans.recipes.core.presentation.plan

import de.malteans.recipes.core.domain.PlannedRecipe
import kotlinx.datetime.LocalDate

sealed interface PlanAction {
    data class OnRecipeShow(val recipeId: Long) : PlanAction

    data class ShowEditPlanDialog(val plannedRecipe: PlannedRecipe) : PlanAction
    data object DismissEditPlanDialog : PlanAction
    data class OnEditPlan(val plannedRecipe: PlannedRecipe) : PlanAction

    data class OnDeletePlan(val planId: Long) : PlanAction

    data class OnExpandDay(val date: LocalDate, val expand: Boolean = true) : PlanAction

    data object OnDeleteAllPlans : PlanAction
}