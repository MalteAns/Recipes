package de.malteans.recipes.core.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val cloudId: Long? = null,
    val sourceUrl: String? = null,
    val name: String,
    val cloudName: String? = null,
    val description: String,
    val cloudDescription: String? = null,
    val imageUrl: String,
    val cloudImageUrl: String? = null,
    val workTime: Int?,
    val cloudWorkTime: Int? = null,
    val totalTime: Int?,
    val cloudTotalTime: Int? = null,
    val servings: Int?,
    val cloudServings: Int? = null,
    val rating: Int?,
    val onlineRating: Double? = null,
)
