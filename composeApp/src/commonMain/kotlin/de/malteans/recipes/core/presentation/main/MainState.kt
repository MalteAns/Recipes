package de.malteans.recipes.core.presentation.main

import de.malteans.recipes.core.presentation.main.components.Screen

data class MainState(
    val curScreen: Screen = Screen.Search,
)
