package de.malteans.recipes.db

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object StepsTable : Table("steps") {
    val id = integer("id").autoIncrement()
    val recipeId = reference(
        name = "recipeId",
        refColumn = RecipesTable.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.NO_ACTION,
        fkName = "steps_recipe_id",
    )
    val stepNumber = integer("stepNumber")
    val description = text("description")
    val duration = integer("duration").nullable()

    override val primaryKey = PrimaryKey(id, name = "pk_steps_id")
}