package de.malteans.recipes.core.presentation.search.components

import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import de.malteans.recipes.core.domain.Recipe
import de.malteans.recipes.core.presentation.components.RatingBar
import de.malteans.recipes.core.presentation.details.components.CustomClockIcon
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import recipes.composeapp.generated.resources.Res
import recipes.composeapp.generated.resources.min
import recipes.composeapp.generated.resources.recipe_img_2
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecipeListItem(
    recipe: Recipe,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(32.dp),
        modifier = modifier
            .combinedClickable (
                onClick = onClick,
                onLongClick = onLongClick
            ),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(85.dp)
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                var imageLoadResult by remember {
                    mutableStateOf<Result<Painter>?>(null)
                }
                val painter = rememberAsyncImagePainter(
                    model = recipe.imageUrl,
                    onSuccess = {
                        imageLoadResult =
                            if (it.painter.intrinsicSize.width > 1 && it.painter.intrinsicSize.height > 1) {
                                Result.success(it.painter)
                            } else {
                                Result.failure(Exception("Invalid image size"))
                            }
                    },
                    onError = {
                        it.result.throwable.printStackTrace()
                        imageLoadResult = Result.failure(it.result.throwable)
                    }
                )

                val painterState by painter.state.collectAsStateWithLifecycle()
                val animatedScale by animateFloatAsState(
                    targetValue = if(painterState is AsyncImagePainter.State.Success) 0.7f else 0f,
                    animationSpec = tween(
                        durationMillis = 800,
                        easing = EaseOutBack)
                )

                when (val result = imageLoadResult) {
                    null -> PulseAnimation(
                        modifier = Modifier.size(85.dp)
                    )
                    else -> {
                        Image(
                            painter = if (result.isSuccess) painter else {
                                painterResource(Res.drawable.recipe_img_2)
                            },
                            contentDescription = recipe.name,
                            contentScale = if (result.isSuccess) ContentScale.Crop else ContentScale.FillBounds,
                            modifier = Modifier
                                .width(85.dp)
                                .height(100.dp)
                                .graphicsLayer {
                                    val scale = if (result.isSuccess) 0.3f + animatedScale else 1f
                                    scaleX = scale
                                    scaleY = scale
                                }
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    RatingBar(
                        value = recipe.rating ?: recipe.onlineRating?.times(5)?.roundToInt(),
                        online = recipe.rating == null && recipe.onlineRating != null,
                        small = true
                    )
                    Text(
                        text = recipe.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = recipe.description.ifBlank { "–" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = CustomClockIcon,
                            contentDescription = null,
                            modifier = Modifier
                                .size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (recipe.workTime != null && recipe.workTime != recipe.totalTime)
                                    "${recipe.workTime} / ${recipe.totalTime ?: "–"} ${stringResource(Res.string.min)}"
                                else "${recipe.totalTime ?: "–"} ${stringResource(Res.string.min)}",
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Clip,
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = recipe.ingredients.joinToString(", ") { recipeIngredientItem ->
                            var result = recipeIngredientItem.ingredient.name.split(",")[0].split("(")[0]
                                .ifBlank { recipeIngredientItem.ingredient.name }
                            // Return first uppercase word, if any, otherwise return the whole string
                            return@joinToString result.split(" ").firstOrNull { it.isNotBlank() && it[0].isUpperCase() }
                                ?.ifBlank { result }?: result
                        }.ifBlank { "–" },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}