package de.malteans.recipes.app

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable
    data object Main

    @Serializable
    data object PlanScreen

    @Serializable
    data object SearchScreen

    @Serializable
    data object AddScreen

    @Serializable
    data class DetailScreen(val id: Long? = null)

    @Serializable
    data class EditScreen(val id: Long)
}