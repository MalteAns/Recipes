package de.malteans.recipes.db

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object ImagesTable : Table("images") {
    val id = varchar("id", length = 36)
    val bucketKey = varchar("bucket_key", length = 512)
    val publicUrl = varchar("public_url", length = 1024).nullable()
    val mimeType = varchar("mime_type", length = 100)
    val byteSize = integer("byte_size")
    val width = integer("width").nullable()
    val height = integer("height").nullable()
    val sha256Hex = varchar("sha256_hex", length = 64).nullable()
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(id, name = "pk_images_id")
}