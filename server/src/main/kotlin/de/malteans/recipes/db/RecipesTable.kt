package de.malteans.recipes.db

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object RecipesTable : Table("recipes") {
    val id = integer("id").autoIncrement()
    val name = text("name")
    val description = text("description")
    val imageUrl = text("imageUrl")
    val workTime = integer("workTime").nullable()
    val totalTime = integer("totalTime").nullable()
    val servings = integer("servings").nullable()
    val rating = integer("rating").nullable()
    val onlineRating = double("onlineRating").nullable()
    val sourceUrl = text("sourceUrl").nullable()
    val addedAt = timestamp("addedAt").nullable()
}