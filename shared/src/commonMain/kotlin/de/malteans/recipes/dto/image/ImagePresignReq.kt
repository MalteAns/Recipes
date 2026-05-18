package de.malteans.recipes.dto.image

import kotlinx.serialization.Serializable

@Serializable
data class ImagePresignReq(val filename: String, val mimeType: String, val size: Int)