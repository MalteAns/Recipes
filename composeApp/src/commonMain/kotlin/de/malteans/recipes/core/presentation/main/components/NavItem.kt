package de.malteans.recipes.core.presentation.main.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun NavItem(
    icon: ImageVector,
    selectedIcon: (ImageVector)? = null,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier,
) {
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .clickable {
                onClick()
            }
            .background(
                MaterialTheme.colorScheme.surfaceContainerHigh
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            Column(
                modifier = Modifier
                    .matchParentSize(),
                verticalArrangement = Arrangement.Bottom,
            ) {
                AnimatedVisibility (
                    visible = selected,
                    enter = expandVertically(
                        animationSpec = tween(
                            durationMillis = 300
                        )
                    ),
                    exit = shrinkVertically(
                        animationSpec = tween(
                            durationMillis = 300
                        )
                    ),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                    )
                }
            }
            Icon(
                imageVector = if (selected && selectedIcon != null) selectedIcon
                else icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .scale(1.2f)
                    .padding(12.dp)
            )
        }
    }
}