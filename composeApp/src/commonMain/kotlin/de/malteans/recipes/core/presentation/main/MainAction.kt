package de.malteans.recipes.core.presentation.main

import de.malteans.recipes.core.presentation.main.components.Screen

sealed interface MainAction {
    data class SetScreen(val screen: Screen) : MainAction
}