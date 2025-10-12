package de.malteans.recipes.core.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index

@Entity(
    primaryKeys = ["stepId", "ingredientId"],
    foreignKeys = [
        ForeignKey(
            entity = RecipeStepEntity::class,
            parentColumns = ["id"],
            childColumns = ["stepId"],
            onDelete = CASCADE
        ),
        ForeignKey(
            entity = IngredientEntity::class,
            parentColumns = ["id"],
            childColumns = ["ingredientId"],
            onDelete = CASCADE
        )
    ],
    indices = [
        Index(value = ["stepId"]),
        Index(value = ["ingredientId"]),
    ]
)
data class StepIngredientEntity(
    val stepId: Long,
    val ingredientId: Long,
    val amount: Double?,
    val overrideUnit: String?,
)
