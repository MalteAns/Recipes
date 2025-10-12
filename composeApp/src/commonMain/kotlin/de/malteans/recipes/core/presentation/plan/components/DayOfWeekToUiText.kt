package de.malteans.recipes.core.presentation.plan.components

import de.malteans.recipes.core.presentation.components.UiText
import kotlinx.datetime.DayOfWeek
import recipes.composeapp.generated.resources.*

fun DayOfWeek.toUiText(): UiText {
    return UiText.FromStringResource(when (this) {
        DayOfWeek.MONDAY -> Res.string.monday
        DayOfWeek.TUESDAY -> Res.string.tuesday
        DayOfWeek.WEDNESDAY -> Res.string.wednesday
        DayOfWeek.THURSDAY -> Res.string.thursday
        DayOfWeek.FRIDAY -> Res.string.friday
        DayOfWeek.SATURDAY -> Res.string.saturday
        DayOfWeek.SUNDAY -> Res.string.sunday
        else -> throw IllegalArgumentException("Invalid day of week: $this")
    })
}