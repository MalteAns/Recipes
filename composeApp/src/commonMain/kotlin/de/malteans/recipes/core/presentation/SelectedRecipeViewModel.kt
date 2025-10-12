package de.malteans.recipes.core.presentation

import androidx.lifecycle.ViewModel
import de.malteans.recipes.core.domain.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SelectedRecipeViewModel: ViewModel() {

    private val _selectedRecipe = MutableStateFlow<Recipe?>(null)
    val selectedRecipe = _selectedRecipe.asStateFlow()

    fun onSelectRecipe(recipe: Recipe?) {
        _selectedRecipe.value = recipe
    }
}