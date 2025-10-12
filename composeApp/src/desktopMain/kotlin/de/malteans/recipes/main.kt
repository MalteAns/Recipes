package de.malteans.recipes

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import de.malteans.recipes.app.App
import de.malteans.recipes.di.initKoin
import org.jetbrains.compose.resources.painterResource
import recipes.composeapp.generated.resources.Res
import recipes.composeapp.generated.resources.app_icon

fun main() = application {
    initKoin()

    Window(
        onCloseRequest = ::exitApplication,
        title = "Recipes",
        icon = painterResource(Res.drawable.app_icon),
    ) {
        App()
    }
}