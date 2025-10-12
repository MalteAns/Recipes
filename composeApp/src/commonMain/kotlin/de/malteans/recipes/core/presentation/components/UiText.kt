package de.malteans.recipes.core.presentation.components

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource


sealed interface UiText {
    data class DynamicString(val value: String): UiText
    class FromStringResource(
        val resource: StringResource,
        val args: Array<Any> = arrayOf()
    ): UiText

    @Composable
    fun asString(): String {
        return when(this) {
            is DynamicString -> value
            is FromStringResource -> stringResource(resource = resource, formatArgs = args)
        }
    }
}