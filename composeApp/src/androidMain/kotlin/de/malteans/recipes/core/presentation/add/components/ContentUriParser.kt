package de.malteans.recipes.core.presentation.add.components

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ContentUriParser(
    private val context: Context
) {
    suspend fun readUri(uri: Uri): ByteArray? {
        return withContext(Dispatchers.IO) {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.readBytes()
            }
        }
    }

    fun getFileName(uri: Uri): String? {
        return when (uri.scheme) {
            "content" -> {
                try {
                    context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
                        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (nameIndex != -1 && cursor.moveToFirst()) {
                            return cursor.getString(nameIndex)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                // fallback to path extraction if query fails
                uri.lastPathSegment?.substringAfterLast('/')
            }

            "file" -> {
                uri.path?.substringAfterLast('/')
            }

            else -> {
                // generic fallback for other schemes
                uri.lastPathSegment?.substringAfterLast('/')
            }
        }
    }


    fun getMimeType(uri: Uri): String? {
        return context.contentResolver.getType(uri)
            ?: getMimeTypeFromExtension(uri)
    }

    private fun getMimeTypeFromExtension(uri: Uri): String? {
        val extension = uri.toString().substringAfterLast(".", "")
        return if(extension.isNotBlank()) {
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        } else null
    }
}