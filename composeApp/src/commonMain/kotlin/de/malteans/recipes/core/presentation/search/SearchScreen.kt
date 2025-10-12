package de.malteans.recipes.core.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.malteans.recipes.core.domain.Recipe
import de.malteans.recipes.core.presentation.components.toUiText
import de.malteans.recipes.core.presentation.search.components.RecipesList
import de.malteans.recipes.core.presentation.search.components.SearchBar
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import recipes.composeapp.generated.resources.*

@Composable
fun SearchScreenRoot(
    viewModel: SearchViewModel = koinViewModel(),
    onRecipeClick: (Recipe) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SearchScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is SearchAction.OnRecipeClick -> onRecipeClick(action.recipe)
                else -> viewModel.onAction(action)
            }
        }
    )
}

@Composable
fun SearchScreen(
    state: SearchState,
    onAction: (SearchAction) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val focusManger = LocalFocusManager.current

    val pagerState = rememberPagerState { 2 }
    val localRecipesListState = rememberLazyListState()
    val cloudRecipesListState = rememberLazyListState()

    LaunchedEffect(state.selectedTabIndex) {
        pagerState.animateScrollToPage(state.selectedTabIndex)
    }

    LaunchedEffect(pagerState.currentPage) {
        onAction(SearchAction.OnTabSelected(pagerState.currentPage))
    }

    LaunchedEffect(state.forceScrollTo) {
        if (state.forceScrollTo != null) {
            when (state.selectedTabIndex) {
                0 -> {
                    val index = state.localRecipes.indexOfFirst {
                        it.id == state.forceScrollTo.first && it.cloudId == state.forceScrollTo.second
                    }
                    if (index != -1) {
                        localRecipesListState.scrollToItem(index)
                    }
                }
                1 -> {
                    val index = state.cloudRecipes.indexOfFirst {
                        it.id == state.forceScrollTo.first && it.cloudId == state.forceScrollTo.second
                    }
                    if (index != -1) {
                        cloudRecipesListState.scrollToItem(index)
                    }
                }
            }
            onAction(SearchAction.ResetForceScrollTo)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SearchBar(
            searchQuery = state.searchQuery,
            onSearchQueryChange = {
                onAction(SearchAction.OnSearchQueryChange(it))
            },
            onSearch = {
                focusManger.clearFocus()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceContainer,
            shape = RoundedCornerShape(
                topStart = 32.dp,
                topEnd = 32.dp
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TabRow(
                    selectedTabIndex = state.selectedTabIndex,
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .fillMaxWidth(),
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .tabIndicatorOffset(tabPositions[state.selectedTabIndex])
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                ) {
                    Tab(
                        selected = state.selectedTabIndex == 0,
                        onClick = {
                            if (state.selectedTabIndex == 0)
                                scope.launch { localRecipesListState.animateScrollToItem(0) }
                            else
                                onAction(SearchAction.OnTabSelected(0))
                        },
                        modifier = Modifier
                            .weight(1f),
                        unselectedContentColor = MaterialTheme.colorScheme.onSurface
                    ) {
                        Text(
                            text = stringResource(Res.string.local_recipes),
                            modifier = Modifier
                                .padding(top = 24.dp, bottom = 12.dp)
                        )
                    }
                    Tab(
                        selected = state.selectedTabIndex == 1,
                        onClick = {
                            if (state.selectedTabIndex == 1) {
                                onAction(SearchAction.OnRefreshCloud)
                                scope.launch { cloudRecipesListState.scrollToItem(0) }
                            } else {
                                onAction(SearchAction.OnTabSelected(1))
                            }
                        },
                        modifier = Modifier
                            .weight(1f),
                        unselectedContentColor = MaterialTheme.colorScheme.onSurface
                    ) {
                        Text(
                            text = stringResource(Res.string.cloud_recipes),
                            modifier = Modifier
                                .padding(top = 24.dp, bottom = 12.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                ) { pageIndex ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator()
                        } else {
                            when (pageIndex) {
                                0 -> { // Local recipes
                                    if (state.localRecipes.isEmpty()) {
                                        Text(
                                            text = if (state.searchQuery.isEmpty()) {
                                                stringResource(Res.string.no_local_recipes)
                                            } else {
                                                stringResource(Res.string.no_recipes_for_search)
                                            },
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    } else {
                                        // Note: onRecipeClick now sends the full Recipe object
                                        RecipesList(
                                            recipes = state.localRecipes,
                                            onRecipeClick = { recipe ->
                                                onAction(SearchAction.OnRecipeClick(recipe))
                                            },
                                            modifier = Modifier
                                                .fillMaxSize(),
                                            scrollState = localRecipesListState
                                        )
                                    }
                                }
                                1 -> { // Cloud recipes
                                    if (state.cloudRecipes.isEmpty()) {
                                        Text(
                                            text = if (state.cloudError != null) {
                                                state.cloudError.toUiText().asString()
                                            } else if (state.searchQuery.isNotEmpty()) {
                                                stringResource(Res.string.no_recipes_for_search)
                                            } else {
                                                stringResource(Res.string.no_search_query)
                                            },
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    } else {
                                        RecipesList(
                                            recipes = state.cloudRecipes,
                                            onRecipeClick = { recipe ->
                                                onAction(SearchAction.OnRecipeClick(recipe))
                                            },
                                            modifier = Modifier
                                                .fillMaxSize(),
                                            scrollState = cloudRecipesListState
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
