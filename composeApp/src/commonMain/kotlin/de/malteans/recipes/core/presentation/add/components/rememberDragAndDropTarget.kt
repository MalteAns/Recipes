package de.malteans.recipes.core.presentation.add.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.draganddrop.DragAndDropTarget

@Composable
expect fun rememberDragAndDropTarget(
    onHover: (Boolean) -> Unit,
    onDrop: (PickedImageData) -> Unit
): DragAndDropTarget