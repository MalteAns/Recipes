package de.malteans.recipes.core.presentation.plan

import de.malteans.recipes.core.domain.PlannedRecipe
import de.malteans.recipes.core.domain.Recipe
import de.malteans.recipes.core.presentation.plan.components.PlannedDayData

data class PlanState(
    val showEditPlanDialog: Boolean = false,
    val planToEdit: PlannedRecipe? = null,

    val allRecipes: List<Recipe> = emptyList(),
    val allPlannedDayData: List<PlannedDayData> = emptyList(),

    val jumpToIndex: Int? = null,
)
