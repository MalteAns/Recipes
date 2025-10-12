package de.malteans.recipes.app

import androidx.compose.runtime.Composable
import de.malteans.recipes.core.presentation.main.MainScreenRoot
import de.malteans.recipes.theme.RecipesTheme

@Composable
fun App() {
    RecipesTheme {
        MainScreenRoot()
    }
}