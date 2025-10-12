package de.malteans.recipes.model.ext

import de.malteans.recipes.Constants
import de.malteans.recipes.dto.ImageDto
import de.malteans.recipes.model.Image

fun ImageDto.toDomain() = Image(
    id = this.id,
    filename = this.filename,
    publicUrl = Constants.BASE_URL + this.publicUrl,
    mimeType = this.mimeType,
    byteSize = this.byteSize,
    width = this.width,
    height = this.height,
)