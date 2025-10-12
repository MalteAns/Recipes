package de.malteans.recipes.services

import de.malteans.recipes.dto.ImageDto
import de.malteans.recipes.dto.ImagePresignReq
import de.malteans.recipes.model.ImageMeta
import de.malteans.recipes.model.Presign

interface ImageService {
    fun presignUpload(req: ImagePresignReq): Presign
    fun saveFile(id: String, bytes: ByteArray)
    fun finalize(id: String): ImageMeta
    fun getUrl(id: String): String?
    fun list(): List<ImageDto> // replace with ImageDto
    fun delete(id: String): Int
}