package de.malteans.recipes.core.presentation.search

import de.malteans.recipes.core.domain.Recipe

sealed interface SearchAction {
    data class OnSearchQueryChange(val query: String) : SearchAction
    data class OnTabSelected(val index: Int) : SearchAction
    data class OnRecipeClick(val recipe: Recipe) : SearchAction
    data object OnRefreshCloud : SearchAction

    data object ResetForceScrollTo : SearchAction
}
