package de.malteans.recipes

import androidx.compose.ui.window.ComposeUIViewController
import de.malteans.recipes.app.App
import de.malteans.recipes.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) { App() }