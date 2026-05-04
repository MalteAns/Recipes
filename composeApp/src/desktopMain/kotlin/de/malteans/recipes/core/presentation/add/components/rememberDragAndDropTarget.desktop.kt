@file:OptIn(ExperimentalComposeUiApi::class)

package de.malteans.recipes.core.presentation.add.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DropTargetDropEvent
import java.io.File

@Composable
actual fun rememberDragAndDropTarget(
    onHover: (Boolean) -> Unit,
    onDrop: (PickedImageData) -> Unit
): DragAndDropTarget {
    val scope = rememberCoroutineScope()
    return remember {
        object : DragAndDropTarget {
            override fun onStarted(event: DragAndDropEvent) {
                onHover(true)
            }

            override fun onEnded(event: DragAndDropEvent) {
                onHover(false)
            }

            override fun onDrop(event: DragAndDropEvent): Boolean {
                val nativeEvent = event.nativeEvent as DropTargetDropEvent
                val fileList = nativeEvent
                    .transferable
                    .getTransferData(DataFlavor.javaFileListFlavor)
                        as List<*>

                if(fileList.size != 1) {
                    return false
                }

                val file = fileList.first() as File
                val hasValidExtension = allowedImageExtensions.any {
                    file.extension.lowercase() == it
                }

                if(hasValidExtension) {
                    val mimeType = getMimeTypeFromFileName(file.name) ?: return false
                    scope.launch(Dispatchers.IO) {
                        onDrop(
                            PickedImageData(
                                filename = file.name,
                                bytes = file.readBytes(),
                                mimeType = mimeType
                            )
                        )
                    }
                    return true
                }

                return false
            }
        }
    }
}