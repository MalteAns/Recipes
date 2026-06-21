package de.malteans.recipes.core.presentation.plan.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import de.malteans.recipes.core.domain.PlannedRecipe
import de.malteans.recipes.core.presentation.details.asString
import de.malteans.recipes.core.presentation.search.components.RecipeListItem
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalFoundationApi::class, ExperimentalTime::class)
@Composable
fun PlannedDayItem(
    data: PlannedDayData,
    onExpand: (Boolean) -> Unit,
    onRecipeShow: (Long) -> Unit,
    onEditPlan: (PlannedRecipe) -> Unit,
    modifier: Modifier = Modifier
) {
    val cornerRadius = animateDpAsState(
        targetValue =  if (data.isExpanded) 16.dp else 8.dp,
        animationSpec = tween(300)
    )

    val rotationDegrees = animateFloatAsState(
        targetValue = if (data.isExpanded) 90f else 0f,
        animationSpec = tween(300)
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(8.dp)
            ),
    ) {
        val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp, 8.dp, cornerRadius.value, cornerRadius.value))
                .fillMaxWidth()
                .background(
                    color = if (data.date < currentDate)
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                    else if (data.date == currentDate)
                        MaterialTheme.colorScheme.tertiaryContainer
                    else
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
                )
                .clickable { onExpand(!data.isExpanded) }
                .padding(20.dp, 8.dp, 16.dp, 8.dp)
        ) {
            Text(
                text = data.date.asString(),
                style = MaterialTheme.typography.titleMedium,
                color = if (data.date < currentDate)
                        MaterialTheme.colorScheme.onSurface
                    else if (data.date == currentDate)
                        MaterialTheme.colorScheme.onTertiaryContainer
                    else
                        MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .weight(1f)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = "Dropdown",
                tint = if (data.date < currentDate)
                        MaterialTheme.colorScheme.onSurface
                    else if (data.date == currentDate)
                        MaterialTheme.colorScheme.onTertiaryContainer
                    else
                        MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .size(30.dp)
                    .rotate(rotationDegrees.value)
            )
        }
        AnimatedVisibility(
            visible = data.isExpanded,
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            var lastTimeOfDay: TimeOfDay? = null
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                data.recipes.forEach { plannedRecipe ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        if (plannedRecipe.timeOfDay != lastTimeOfDay) {
                            lastTimeOfDay = plannedRecipe.timeOfDay
                            Text(
                                text = plannedRecipe.timeOfDay.toUiText.asString(),
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier
                                    .padding(horizontal = 24.dp, vertical = 4.dp)
                            )
                        } else {
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        RecipeListItem(
                            recipe = plannedRecipe.recipe,
                            onClick = {
                                onRecipeShow(plannedRecipe.recipe.id)
                            },
                            onLongClick = {
                                onEditPlan(plannedRecipe)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
