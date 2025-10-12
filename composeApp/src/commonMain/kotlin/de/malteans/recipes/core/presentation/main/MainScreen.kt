package de.malteans.recipes.core.presentation.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import de.malteans.recipes.app.Route
import de.malteans.recipes.core.presentation.components.SnackbarManager
import de.malteans.recipes.core.presentation.main.components.NavGraph
import de.malteans.recipes.core.presentation.main.components.NavItem
import de.malteans.recipes.core.presentation.main.components.Screen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainScreenRoot(
    viewModel: MainViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    MainScreen(
        state = state,
        onAction = { action -> viewModel.onAction(action) }
    )
}

@Composable
fun MainScreen(
    state: MainState,
    onAction: (MainAction) -> Unit,
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        SnackbarManager.snackbarMessages.collect { snackbarValue ->
            val snackbarResult = snackbarHostState.showSnackbar(
                message = snackbarValue.message,
                actionLabel = snackbarValue.actionLabel,
                withDismissAction = snackbarValue.withDismissAction,
                duration = snackbarValue.duration
            )
            if (snackbarResult == SnackbarResult.ActionPerformed) {
                snackbarValue.onAction()
            }
        }
    }

    Scaffold (
        snackbarHost = { SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .padding(bottom = 48.dp)
        ) },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier
                    .height(64.dp),
                contentPadding = PaddingValues(0.dp),
            ) {
                NavItem(
                    icon = Icons.Outlined.DateRange,
                    selectedIcon = Icons.Filled.DateRange,
                    onClick = {
                        if (state.curScreen != Screen.Plan) {
                            navController.navigate(Route.PlanScreen)
                        }
                    },
                    selected = state.curScreen == Screen.Plan,
                    modifier = Modifier
                        .weight(1f)
                )
                NavItem(
                    icon = Icons.Outlined.Search,
                    selectedIcon = Icons.Filled.Search,
                    onClick = {
                        if (state.curScreen != Screen.Search) {
                            navController.popBackStack<Route.SearchScreen>(false)
                        }
                    },
                    selected = state.curScreen == Screen.Search,
                    modifier = Modifier
                        .weight(1f)
                )
                NavItem(
                    icon = if (state.curScreen == Screen.Edit) Icons.Filled.Edit
                    else Icons.Filled.AddCircle,
                    onClick = {
                        if (state.curScreen != Screen.Add && state.curScreen != Screen.Edit) {
                            navController.navigate(Route.AddScreen)
                        }
                    },
                    selected = state.curScreen == Screen.Add || state.curScreen == Screen.Edit,
                    modifier = Modifier
                        .weight(1f)
                )
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(bottom = pad.calculateBottomPadding())
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                NavGraph(
                    navController = navController,
                    setCurScreen = {
                        onAction(MainAction.SetScreen(it))
                    }
                )
            }
        }
    }
}