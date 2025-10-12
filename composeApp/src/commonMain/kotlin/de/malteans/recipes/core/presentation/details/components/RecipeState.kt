package de.malteans.recipes.core.presentation.details.components

import de.malteans.recipes.core.domain.Recipe
import de.malteans.recipes.core.presentation.components.UiText
import recipes.composeapp.generated.resources.*

enum class RecipeState {
    LOCAL_ONLY,
    CLOUD_ONLY,
    SAVED_CLOUD,
    EDITED_CLOUD;
    
    val uiText : UiText
        get() = when (this) {
            LOCAL_ONLY -> UiText.FromStringResource(Res.string.local_recipe)
            CLOUD_ONLY -> UiText.FromStringResource(Res.string.cloud_recipe)
            SAVED_CLOUD -> UiText.FromStringResource(Res.string.saved_cloud_recipe)
            EDITED_CLOUD -> UiText.FromStringResource(Res.string.edited_cloud_recipe)
        }
}

fun Recipe.isCloudOnly(): Boolean {
    return this.id == 0L && this.cloudId != null
}

fun Recipe.isLocalOnly(): Boolean {
    return this.id != 0L && this.cloudId == null
}

fun Recipe.isEditedCloudRecipe(): Boolean {
    return this.id != 0L && this.cloudId != 0L && (
        this.name != this.cloudName
        || this.description != this.cloudDescription
        || this.imageUrl != this.cloudImageUrl
        || this.workTime != this.cloudWorkTime
        || this.totalTime != this.cloudTotalTime
        || this.servings != this.cloudServings
        || this.ingredients != this.cloudIngredients
        || this.steps != this.cloudSteps
    )
}

fun Recipe.getState() : RecipeState {
    return if (this.isCloudOnly())
        RecipeState.CLOUD_ONLY
    else if (this.isLocalOnly())
        RecipeState.LOCAL_ONLY
    else if (this.isEditedCloudRecipe())
        RecipeState.EDITED_CLOUD
    else
        RecipeState.SAVED_CLOUD
}