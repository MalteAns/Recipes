package de.malteans.recipes.core.domain

import de.malteans.recipes.core.presentation.plan.components.TimeOfDay
import kotlinx.datetime.LocalDate

data class PlannedRecipe (
    val id: Long = 0L,
    val recipe: Recipe,
    val date: LocalDate,
    val timeOfDay: TimeOfDay,
)