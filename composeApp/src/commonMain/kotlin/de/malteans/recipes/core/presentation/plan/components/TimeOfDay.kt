package de.malteans.recipes.core.presentation.plan.components

import de.malteans.recipes.core.presentation.components.UiText
import recipes.composeapp.generated.resources.*

enum class TimeOfDay {
    PRE_COOKING,
    BREAKFAST,
    MORNING_SNACK,
    LUNCH,
    AFTERNOON_SNACK,
    DINNER,
    EVENING_SNACK;

    val toUiText: UiText
        get() = when (this) {
            PRE_COOKING -> UiText.FromStringResource(Res.string.pre_cooking)
            BREAKFAST -> UiText.FromStringResource(Res.string.breakfast)
            MORNING_SNACK -> UiText.FromStringResource(Res.string.morning_snack)
            LUNCH -> UiText.FromStringResource(Res.string.lunch)
            AFTERNOON_SNACK -> UiText.FromStringResource(Res.string.afternoon_snack)
            DINNER -> UiText.FromStringResource(Res.string.dinner)
            EVENING_SNACK -> UiText.FromStringResource(Res.string.evening_snack)
        }
}