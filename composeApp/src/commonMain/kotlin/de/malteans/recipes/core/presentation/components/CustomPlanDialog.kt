package de.malteans.recipes.core.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.malteans.recipes.core.domain.PlannedRecipe
import de.malteans.recipes.core.domain.Recipe
import de.malteans.recipes.core.presentation.details.asString
import de.malteans.recipes.core.presentation.plan.components.TimeOfDay
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import recipes.composeapp.generated.resources.*
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun CustomPlanDialog(
    onDismiss: () -> Unit,
    onSubmit: (PlannedRecipe) -> Unit,
    isEdit: Boolean = false,
    onDelete: (() -> Unit)? = null,
    allRecipes: List<Recipe> = emptyList(),
    initialRecipe: Recipe? = null,
    initialPlannedRecipe: PlannedRecipe = PlannedRecipe(
        recipe = initialRecipe ?: throw IllegalStateException("initialRecipe or initialPlannedRecipe must be provided"),
        date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.plus(DatePeriod(days = 1)),
        timeOfDay = TimeOfDay.LUNCH,
    ),
) {
    var selectedRecipe by remember { mutableStateOf(initialPlannedRecipe.recipe) }
    var selectedDate by remember { mutableStateOf(initialPlannedRecipe.date) }
    var selectedTimeOfDay by remember { mutableStateOf(initialPlannedRecipe.timeOfDay) }

    var showDatePickerDialog by remember { mutableStateOf(false) }

    if (showDatePickerDialog) {
        CustomDatePickerDialog(
            onDismissRequest = { showDatePickerDialog = false },
            onSubmit = { newDate ->
                selectedDate = newDate
                showDatePickerDialog = false
            },
            initialSelectedDate = selectedDate
        )
    }

    CustomDialog(
        onDismissRequest = onDismiss,
        titleText = if (isEdit) stringResource(Res.string.edit_plan)
            else stringResource(Res.string.plan_recipe),
        leftIcon = @Composable {
            Row{
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Close",
                    modifier = Modifier.clickable { onDismiss() }
                )
                if (onDelete != null) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .clickable {
                                onDelete()
                                onDismiss()
                            },
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }
        },
        rightIcon = @Composable {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier
                    .clickable {
                        onSubmit(initialPlannedRecipe.copy(
                            recipe = selectedRecipe,
                            date = selectedDate,
                            timeOfDay = selectedTimeOfDay,
                        ))
                        onDismiss()
                    }
            )
        },
    ) {
        SearchableDropdown(
            label = { Text(text = stringResource(Res.string.recipe)) },
            selectedOption = Pair(selectedRecipe, selectedRecipe.name),
            options = allRecipes.associate { it to it.name },
            onValueChanged = { newRecipe ->
                selectedRecipe = newRecipe as Recipe
            },
            enabled = isEdit,
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedText(
            value = selectedDate.asString(),
            label = { Text(text = stringResource(Res.string.date)) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Date",
                )
            },
            modifier = Modifier
                .clickable { showDatePickerDialog = true },
        )
        Spacer(modifier = Modifier.height(8.dp))
        Dropdown(
            selectedOption = Pair(selectedTimeOfDay, selectedTimeOfDay.toUiText.asString()),
            options = TimeOfDay.entries.associate { it to it.toUiText.asString() },
            onValueChanged = { newTimeOfDay ->
                selectedTimeOfDay = newTimeOfDay as TimeOfDay
            },
            label = { Text(text = stringResource(Res.string.time_of_day)) },
        )
    }
}