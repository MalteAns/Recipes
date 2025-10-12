package de.malteans.recipes.dto

import kotlinx.serialization.Serializable

@Serializable
data class ImagePresignReq(val filename: String, val mimeType: String, val size: Int)