package de.malteans.recipes.core.presentation.search.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.malteans.recipes.core.domain.Recipe

@Composable
fun RecipesList(
    recipes: List<Recipe>,
    onRecipeClick: (Recipe) -> Unit,
    onRecipeLongClick: (Recipe) -> Unit = {},
    modifier: Modifier = Modifier,
    scrollState: LazyListState = rememberLazyListState()
) {
    LazyColumn(
        modifier = modifier,
        state = scrollState,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(
            items = recipes,
            key = { Pair(it.id, it.cloudId) }
        ) { recipe ->
//            val visible = remember { mutableStateOf(false) }
//            LaunchedEffect(Unit) {
//                visible.value = true
//            }
//            AnimatedVisibility(
//                visible = visible.value,
//                enter = scaleIn(
//                    initialScale = 0.5f,
//                    animationSpec = tween(
//                        durationMillis = 300,
//                        easing = EaseOut
//                    )
//                )
//            ) {
                RecipeListItem(
                    recipe = recipe,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onRecipeClick(recipe) },
                    onLongClick = { onRecipeLongClick(recipe) }
                )
//            }
        }
        item {
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}