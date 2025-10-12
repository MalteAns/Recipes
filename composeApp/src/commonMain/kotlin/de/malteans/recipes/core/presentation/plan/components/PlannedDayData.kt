package de.malteans.recipes.core.presentation.plan.components

import de.malteans.recipes.core.domain.PlannedRecipe
import kotlinx.datetime.LocalDate

data class PlannedDayData(
    val date: LocalDate,
    val recipes: List<PlannedRecipe> = emptyList(),
    val isExpanded: Boolean = false,
)
