package de.malteans.recipes.db

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object IngredientsTable : Table("ingredients") {
    val id = integer("id").autoIncrement()
    val ingredientName = text("ingredientName")
    val ingredientAmount = double("ingredientAmount").nullable()
    val recipeId = reference(
        name = "recipeId",
        refColumn = RecipesTable.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.NO_ACTION,
        fkName = "ingredients_recipe_id",
    )
    val ingredientUnit = text("ingredientUnit").nullable()

    override val primaryKey = PrimaryKey(id, name = "pk_ingredients_id")
}