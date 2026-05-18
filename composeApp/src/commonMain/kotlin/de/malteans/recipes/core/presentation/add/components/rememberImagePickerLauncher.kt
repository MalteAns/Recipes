package de.malteans.recipes.core.presentation.add.components

import androidx.compose.runtime.Composable

@Composable
expect fun rememberImagePickerLauncher(
    onResult: (PickedImageData) -> Unit
): ImagePickerLauncher

class ImagePickerLauncher(
    private val onLaunch: () -> Unit
) {
    fun launch() {
        onLaunch()
    }
}

@Suppress("ArrayInDataClass")
data class PickedImageData(
    val filename: String = "",
    val bytes: ByteArray,
    val mimeType: String,
)