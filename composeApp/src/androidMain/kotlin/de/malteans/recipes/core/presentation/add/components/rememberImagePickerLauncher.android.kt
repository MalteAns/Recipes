package de.malteans.recipes.core.presentation.add.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

@Composable
actual fun rememberImagePickerLauncher(
    onResult: (PickedImageData) -> Unit
): ImagePickerLauncher {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if(uri != null) {
            val parser = ContentUriParser(context)
            val mimeType = parser.getMimeType(uri)

            scope.launch {
                val data = PickedImageData(
                    filename = parser.getFileName(uri) ?: return@launch,
                    bytes = parser.readUri(uri) ?: return@launch,
                    mimeType = mimeType ?: return@launch,
                )
                onResult(data)
            }
        }
    }

    return ImagePickerLauncher(
        onLaunch = {
            photoPickerLauncher.launch(
                PickVisualMediaRequest(
                    mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }
    )
}