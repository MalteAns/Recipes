package de.malteans.recipes.core.presentation.components

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RatingBar(
    value: Int?,
    online: Boolean = false,
    small: Boolean = false,
    onChange: ((Int?) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
    ) {
        for (i in 1..5) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Rating",
                tint = if (value == null) MaterialTheme.colorScheme.onSurface.copy(0.4f)
                    else if (online) MaterialTheme.colorScheme.secondary.copy(if (i <= value) 1f else 0.4f)
                    else MaterialTheme.colorScheme.tertiary.copy(if (i <= value) 1f else 0.4f),
                modifier = Modifier
                    .then(
                        if (!small) Modifier
                        else Modifier.size(16.dp)
                    )
                    .then(
                        if (onChange == null) Modifier
                        else Modifier
                            .combinedClickable(
                                onClick = {
                                    onChange(i)
                                },
                                onLongClick = {
                                    onChange(null)
                                }
                            )
                    )
            )
        }
    }
}