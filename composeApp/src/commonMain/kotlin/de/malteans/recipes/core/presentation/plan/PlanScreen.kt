package de.malteans.recipes.core.presentation.plan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.malteans.recipes.core.presentation.components.CustomPlanDialog
import de.malteans.recipes.core.presentation.plan.components.PlannedDayItem
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import recipes.composeapp.generated.resources.Res
import recipes.composeapp.generated.resources.no_planned_recipes

@Composable
fun PlanScreenRoot(
    viewModel: PlanViewModel = koinViewModel(),
    onRecipeShow: (Long) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    PlanScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is PlanAction.OnRecipeShow -> onRecipeShow(action.recipeId)

                else -> viewModel.onAction(action)
            }
        },
    )
}

@Composable
fun PlanScreen(
    state: PlanState,
    onAction: (PlanAction) -> Unit,
) {
    val scrollState = rememberLazyListState()

    LaunchedEffect(state.jumpToIndex) {
        if (state.jumpToIndex != null) {
            scrollState.scrollToItem(
                index = if (state.jumpToIndex != -1) state.jumpToIndex
                    else if (state.allPlannedDayData.isNotEmpty()) state.allPlannedDayData.size - 1
                    else 0,
            )
        }
    }

    if (state.showEditPlanDialog && state.planToEdit != null) {
        CustomPlanDialog(
            onDismiss = { onAction(PlanAction.DismissEditPlanDialog) },
            onSubmit = { onAction(PlanAction.OnEditPlan(it)) },
            isEdit = true,
            onDelete = { onAction(PlanAction.OnDeletePlan(state.planToEdit.id)) },
            allRecipes = state.allRecipes,
            initialPlannedRecipe = state.planToEdit
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (state.allPlannedDayData.isNotEmpty()) {
            LazyColumn(
                state = scrollState
            ) {
                items(
                    items = state.allPlannedDayData,
                ) { data ->
                    PlannedDayItem(
                        data = data,
                        onExpand = {
                            onAction(PlanAction.OnExpandDay(data.date, it))
                        },
                        onRecipeShow = { recipeId ->
                            onAction(PlanAction.OnRecipeShow(recipeId))
                        },
                        onEditPlan = { plannedRecipe ->
                            onAction(PlanAction.ShowEditPlanDialog(plannedRecipe))
                        },
                        modifier = Modifier
                            .padding(8.dp)
                    )
                }
//            item {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth(),
//                    horizontalArrangement = Arrangement.Center,
//                ) {
//                    var deleteIsClicked by remember { mutableStateOf(false) }
//                    LaunchedEffect(deleteIsClicked) {
//                        if (deleteIsClicked) {
//                            delay(3000)
//                            deleteIsClicked = false
//                        }
//                    }
//                    Button(
//                        onClick = {
//                            if (deleteIsClicked) onAction(PlanAction.OnDeleteAllPlans)
//                            else deleteIsClicked = true
//                        },
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = MaterialTheme.colorScheme.errorContainer,
//                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
//                        )
//                    ) {
//                        Text(
//                            text = "Delete All" + if (deleteIsClicked) " (confirm)" else "",
//                        )
//                    }
//                }
//            }
                item {
                    Spacer(modifier = Modifier.height(64.dp))
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(Res.string.no_planned_recipes),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    }
}