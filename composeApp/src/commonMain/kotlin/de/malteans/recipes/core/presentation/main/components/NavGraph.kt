package de.malteans.recipes.core.presentation.main.components

import androidx.compose.animation.*
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import de.malteans.recipes.app.Route
import de.malteans.recipes.core.presentation.SelectedRecipeViewModel
import de.malteans.recipes.core.presentation.add.AddScreenRoot
import de.malteans.recipes.core.presentation.add.EditScreenRoot
import de.malteans.recipes.core.presentation.details.DetailsAction
import de.malteans.recipes.core.presentation.details.DetailsScreenRoot
import de.malteans.recipes.core.presentation.details.DetailsViewModel
import de.malteans.recipes.core.presentation.plan.PlanScreenRoot
import de.malteans.recipes.core.presentation.search.SearchScreenRoot
import de.malteans.recipes.core.presentation.search.SearchViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    setCurScreen: (Screen) -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = Route.Main,
        // Set default transitions to None (individual composables below override)
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None },
    ) {
        navigation<Route.Main>(
            startDestination = Route.SearchScreen
        ) {
            // Plan Screen: slide in/out from left
            composable<Route.PlanScreen>(
                enterTransition = { slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(durationMillis = 300, easing = EaseInOut)) },
                exitTransition = { slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(durationMillis = 300, easing = EaseInOut)) },
                popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(durationMillis = 300, easing = EaseInOut)) },
                popExitTransition = { slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(durationMillis = 300, easing = EaseInOut)) }
            ) {
                PlanScreenRoot(
                    onRecipeShow = { recipeId ->
                        navController.navigate(Route.DetailScreen(recipeId))
                    }
                )
                setCurScreen(Screen.Plan)
            }
            // Search Screen: fade in/out
            composable<Route.SearchScreen>(
                enterTransition = { fadeIn(animationSpec = tween(durationMillis = 300, easing = EaseInOut)) },
                exitTransition = { fadeOut(animationSpec = tween(durationMillis = 300, easing = EaseInOut)) },
                popEnterTransition = { fadeIn(animationSpec = tween(durationMillis = 300, easing = EaseInOut)) },
                popExitTransition = { fadeOut(animationSpec = tween(durationMillis = 300, easing = EaseInOut)) },
            ) {
                val viewModel = it.sharedKoinViewModel<SearchViewModel>(navController)

                val selectedRecipeViewModel =
                    it.sharedKoinViewModel<SelectedRecipeViewModel>(navController)

                LaunchedEffect(true) {
                    selectedRecipeViewModel.onSelectRecipe(null)
                }

                SearchScreenRoot(
                    viewModel = viewModel,
                    onRecipeClick = { recipe ->
                        selectedRecipeViewModel.onSelectRecipe(recipe)
                        navController.navigate(Route.DetailScreen())
                    }
                )
                setCurScreen(Screen.Search)
            }
            // Add Screen: slide in/out from right
            composable<Route.AddScreen>(
                enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(durationMillis = 300, easing = EaseInOut)) },
                exitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(durationMillis = 300, easing = EaseInOut)) },
                popEnterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(durationMillis = 300, easing = EaseInOut)) },
                popExitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(durationMillis = 300, easing = EaseInOut)) },
            ) {
                AddScreenRoot(
                    onRecipeShow = { recipeId ->
                        navController.navigate(Route.DetailScreen(recipeId))
                    }
                )
                setCurScreen(Screen.Add)
            }
            // Detail Screen: slide in/out vertically from bottom
            composable<Route.DetailScreen>(
                enterTransition = { slideInVertically(initialOffsetY = { it }, animationSpec = tween(durationMillis = 300, easing = EaseInOut)) },
                exitTransition = { slideOutVertically(targetOffsetY = { it }, animationSpec = tween(durationMillis = 300, easing = EaseInOut)) },
                popEnterTransition = { slideInVertically(initialOffsetY = { it }, animationSpec = tween(durationMillis = 300, easing = EaseInOut)) },
                popExitTransition = { slideOutVertically(targetOffsetY = { it }, animationSpec = tween(durationMillis = 300, easing = EaseInOut)) },
            ) {
                val args = it.toRoute<Route.DetailScreen>()

                val viewModel = koinViewModel<DetailsViewModel>()

                val searchViewModel = it.sharedKoinViewModel<SearchViewModel>(navController)

                val selectedRecipeViewModel = it.sharedKoinViewModel<SelectedRecipeViewModel>(navController)
                val selectedRecipe by selectedRecipeViewModel.selectedRecipe.collectAsStateWithLifecycle()

                LaunchedEffect(selectedRecipe) {
                    selectedRecipe?.let {
                        viewModel.onAction(DetailsAction.OnSelectedRecipeChange(it))
                    }
                }

                DetailsScreenRoot(
                    onBack = { listKey ->
                        navController.popBackStack()
                        searchViewModel.scrollTo(listKey)
                    },
                    onEdit = { recipeId ->
                        navController.navigate(Route.EditScreen(recipeId))
                    },
                    onSave = { selectedRecipeViewModel.onSelectRecipe(null) },
                    recipeId = args.id
                )
                setCurScreen(Screen.Other)
            }
            composable<Route.EditScreen>(
                enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(durationMillis = 300, easing = EaseInOut)) },
                exitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(durationMillis = 300, easing = EaseInOut)) },
                popEnterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(durationMillis = 300, easing = EaseInOut)) },
                popExitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(durationMillis = 300, easing = EaseInOut)) }
            ) {
                val args = it.toRoute<Route.EditScreen>()
                EditScreenRoot(
                    recipeId = args.id,
                    onBack = { navController.popBackStack() },
                    onFinished = {
                        navController.popBackStack()
                    }
                )
                setCurScreen(Screen.Edit)
            }
        }
    }
}



@Composable
private inline fun <reified T: ViewModel> NavBackStackEntry.sharedKoinViewModel(
    navController: NavController
): T {
    val navGraphRoute = destination.parent?.route ?: return koinViewModel<T>()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return koinViewModel(
        viewModelStoreOwner = parentEntry
    )
}