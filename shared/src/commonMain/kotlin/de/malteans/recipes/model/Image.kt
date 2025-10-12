package de.malteans.recipes.model

data class Image(
    val id: String,
    val filename: String,
    val publicUrl: String,
    val mimeType: String,
    val byteSize: Int,
    val width: Int?,
    val height: Int?,
)
