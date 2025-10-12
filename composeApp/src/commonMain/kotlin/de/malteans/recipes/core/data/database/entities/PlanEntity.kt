package de.malteans.recipes.core.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import de.malteans.recipes.core.presentation.plan.components.TimeOfDay
import kotlinx.datetime.LocalDate

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = RecipeEntity::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index(value = ["recipeId"])]
)
data class PlanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val recipeId: Long,
    val date: LocalDate,
    val timeOfDay: TimeOfDay,
)
