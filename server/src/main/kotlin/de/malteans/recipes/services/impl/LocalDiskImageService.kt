package de.malteans.recipes.services.impl

import de.malteans.recipes.Endpoints
import de.malteans.recipes.db.ImagesTable
import de.malteans.recipes.dto.image.ImageDto
import de.malteans.recipes.dto.image.ImagePresignReq
import de.malteans.recipes.model.ImageMeta
import de.malteans.recipes.model.Presign
import de.malteans.recipes.services.ImageService
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.nio.file.Files
import java.time.Instant
import java.util.*

class LocalDiskImageService(
    private val db: Database,
    private val uploadDir: String = "serverUploads",
) : ImageService {

    init {
        File(uploadDir).mkdirs()
    }

    override fun presignUpload(req: ImagePresignReq): Presign = transaction(db) {
        val id = UUID.randomUUID().toString()
        val key = "$id-${req.filename}"

        // Pre-create DB record
        ImagesTable.insert {
            it[ImagesTable.id] = id
            it[bucketKey] = key
            it[publicUrl] = "/uploads/$key"
            it[mimeType] = req.mimeType
            it[byteSize] = req.size
            it[width] = null
            it[height] = null
            it[sha256Hex] = null
            it[createdAt] = Instant.now()
        }

        Presign(
            id = id,
            putUrl = Endpoints.Images.Upload(id).url,
            key = key
        )
    }

    override fun saveFile(id: String, bytes: ByteArray) {
        transaction(db) {
            val row = ImagesTable.selectAll().where { ImagesTable.id eq id }.single()
            val key = row[ImagesTable.bucketKey]
            val file = File("$uploadDir/$key")
            Files.write(file.toPath(), bytes)
        }
    }

    override fun finalize(id: String): ImageMeta = transaction(db) {
        val row = ImagesTable.selectAll().where { ImagesTable.id eq id }.single()
        ImageMeta(
            id = id,
            publicUrl = row[ImagesTable.publicUrl] ?: "/uploads/${row[ImagesTable.bucketKey]}",
            width = row[ImagesTable.width],
            height = row[ImagesTable.height]
        )
    }

    override fun getUrl(id: String): String? = transaction(db) {
        ImagesTable.selectAll().where { ImagesTable.id eq id }.singleOrNull()?.let {
            it[ImagesTable.publicUrl] ?: "/uploads/${it[ImagesTable.bucketKey]}"
        }
    }

    override fun list(): List<ImageDto> = transaction(db) {
        ImagesTable
            .selectAll()
            .orderBy(ImagesTable.createdAt, SortOrder.DESC)
            .map { row ->
                ImageDto(
                    id = row[ImagesTable.id],
                    filename = row[ImagesTable.bucketKey].split("-").last(),
                    publicUrl = row[ImagesTable.publicUrl] ?: "/uploads/${row[ImagesTable.bucketKey]}",
                    mimeType = row[ImagesTable.mimeType],
                    byteSize = row[ImagesTable.byteSize],
                    width = row[ImagesTable.width],
                    height = row[ImagesTable.height],
                )
            }
    }

    override fun delete(id: String) = transaction(db) {
        val row = ImagesTable.selectAll().where { ImagesTable.id eq id }.singleOrNull()
        row?.let {
            val key = row[ImagesTable.bucketKey]
            File("$uploadDir/$key").delete()
        }
        ImagesTable.deleteWhere { ImagesTable.id eq id }
    }
}
