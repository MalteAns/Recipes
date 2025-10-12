package de.malteans.recipes.core.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class IngredientEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val unit: String
)