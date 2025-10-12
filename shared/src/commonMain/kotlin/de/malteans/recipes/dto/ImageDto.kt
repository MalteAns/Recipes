package de.malteans.recipes.dto

import kotlinx.serialization.Serializable

@Serializable
data class ImageDto(
    val id: String,
    val filename: String,
    val publicUrl: String,
    val mimeType: String,
    val byteSize: Int,
    val width: Int?,
    val height: Int?,
)
