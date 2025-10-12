package de.malteans.recipes.core.presentation.add

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EditScreenRoot(
    viewModel: AddViewModel = koinViewModel(),
    recipeId: Long,
    onBack: () -> Unit,
    onFinished: (Long) -> Unit
) {
    LaunchedEffect(recipeId) {
        viewModel.loadRecipeForEditing(recipeId)
    }

    val state by viewModel.state.collectAsStateWithLifecycle()

    AddScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is AddAction.OnBack -> onBack()
                is AddAction.OnRecipeShow -> onFinished(action.id)
                else -> viewModel.onAction(action)
            }
        },
        onRecipeAdd = { viewModel.onRecipeAdd() },
    )
}
