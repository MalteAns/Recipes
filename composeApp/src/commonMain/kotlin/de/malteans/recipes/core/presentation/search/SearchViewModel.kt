package de.malteans.recipes.core.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.malteans.recipes.core.domain.Recipe
import de.malteans.recipes.core.domain.RecipeRepository
import de.malteans.recipes.core.domain.errorHandling.DataError
import de.malteans.recipes.core.domain.errorHandling.Result
import de.malteans.recipes.core.domain.errorHandling.onError
import de.malteans.recipes.core.domain.errorHandling.onSuccess
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class SearchViewModel(
    private val repository: RecipeRepository,
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)

    private val _searchQuery = MutableStateFlow("")

    private val _selectedTabIndex = MutableStateFlow(0)

    private val _localRecipes: Flow<List<Recipe>?> = combine(
        _searchQuery,
        _selectedTabIndex,
    ) { query, index ->
        if (index == 0) {
            _isLoading.update { true }
            repository.fetchLocalRecipes(query)
        } else {
            flowOf(null)
        }
    }
        .flatMapLatest { it }
        .onEach { newLocalRecipes ->
            if (newLocalRecipes != null)
                _isLoading.update { false }
        }

    private val _forceRefreshCloud = MutableStateFlow(false)

    private val _cloudRecipesResult: Flow<Result<List<Recipe>, DataError.Remote>?> = combine(
        _forceRefreshCloud,
        _searchQuery,
        _selectedTabIndex,
    ) { forceRefresh, query, index ->
        if (index == 1) {
            _isLoading.update { true }
            repository.fetchCloudRecipes(query)
        } else {
            flowOf(null)
        }
    }
        .flatMapLatest { it }
        .onEach { newCloudRecipesResult ->
            if (newCloudRecipesResult != null)
                _isLoading.update { false }
        }

    private val _state = MutableStateFlow(SearchState())

    val state = combine(
        _state,
        _isLoading,
        _selectedTabIndex,
        _localRecipes,
        _cloudRecipesResult,
    ) { state, isLoading, selectedTabIndex, localRecipes, cloudRecipesResult ->
        var result = state.copy(
            isLoading = isLoading,
            selectedTabIndex = selectedTabIndex,
            localRecipes = localRecipes ?: emptyList(),
        )
        cloudRecipesResult
            ?.onSuccess { cloudRecipes ->
                result = result.copy(
                    cloudRecipes = cloudRecipes,
                    cloudError = null
                )
            }
            ?.onError { error ->
                result = result.copy(
                    cloudRecipes = emptyList(),
                    cloudError = error
                )
            }
            ?: run {
                result = result.copy(
                    cloudRecipes = emptyList(),
                    cloudError = null
                )
            }

        return@combine result
    }
        .onStart {
            observeSearchQuery()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SearchState()
        )

    fun onAction(action: SearchAction) {
        when (action) {
            is SearchAction.OnSearchQueryChange -> {
                _state.update { it.copy(searchQuery = action.query) }
            }
            is SearchAction.OnTabSelected -> {
                _selectedTabIndex.update { action.index }
            }
            is SearchAction.OnRefreshCloud -> {
                _forceRefreshCloud.update { !_forceRefreshCloud.value }
            }
            is SearchAction.ResetForceScrollTo -> {
                _state.update { it.copy(forceScrollTo = null) }
            }
            else -> throw IllegalArgumentException("Action not implemented in ViewModel: $action")
        }
    }

    fun scrollTo(key: Pair<Long, Long?>?) {
        _state.update { it.copy(forceScrollTo = key) }
    }

    private fun observeSearchQuery() {
        state
            .map { it.searchQuery }
            .distinctUntilChanged()
            .debounce(500L)
            .onEach { query ->
                _searchQuery.update { query }
            }
            .launchIn(viewModelScope)
    }
}
