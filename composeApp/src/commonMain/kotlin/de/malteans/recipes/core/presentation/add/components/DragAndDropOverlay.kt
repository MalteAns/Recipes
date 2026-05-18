package de.malteans.recipes.core.presentation.add.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import recipes.composeapp.generated.resources.Res
import recipes.composeapp.generated.resources.upload_image

@Composable
fun DragAndDropOverlay(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.8f)),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.AddPhotoAlternate,
            contentDescription = stringResource(Res.string.upload_image),
            modifier = Modifier.size(100.dp)
        )
        Text(
            text = stringResource(Res.string.upload_image),
            style = MaterialTheme.typography.titleMedium
        )
    }
}