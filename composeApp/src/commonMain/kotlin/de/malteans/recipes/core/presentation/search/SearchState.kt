package de.malteans.recipes.core.presentation.search

import de.malteans.recipes.core.domain.Recipe

data class SearchState(
    val searchQuery: String = "",
    val selectedTabIndex: Int = 0,
    val localRecipes: List<Recipe> = emptyList(),
    val cloudRecipes: List<Recipe> = emptyList(),
    val isLoading: Boolean = true,
    val cloudError: Throwable? = null,

    val forceScrollTo: Pair<Long, Long?>? = null,
)
