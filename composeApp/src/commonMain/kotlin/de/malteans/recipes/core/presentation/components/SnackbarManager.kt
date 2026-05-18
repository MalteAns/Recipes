package de.malteans.recipes.core.presentation.components

import androidx.compose.material3.SnackbarDuration
import de.malteans.recipes.core.presentation.util.UiText
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString

data class SnackbarValue(
    val message: String,
    val actionLabel: String?,
    val withDismissAction: Boolean,
    val duration: SnackbarDuration,
    val onAction: (() -> Unit),
)

object SnackbarManager {
    private val _snackbarMessages = MutableSharedFlow<SnackbarValue>()
    val snackbarMessages = _snackbarMessages.asSharedFlow()

    suspend fun showSnackbar(
        message: String, actionLabel: String? = null, withDismissAction: Boolean = true,
        duration: SnackbarDuration = if (actionLabel == null) SnackbarDuration.Short else SnackbarDuration.Indefinite,
        onAction: () -> Unit = {},
    ) {
        _snackbarMessages.emit(SnackbarValue(message, actionLabel, withDismissAction, duration, onAction))
    }

    suspend fun showSnackbar(
        message: StringResource, actionLabel: StringResource? = null, withDismissAction: Boolean = true,
        duration: SnackbarDuration = if (actionLabel == null) SnackbarDuration.Short else SnackbarDuration.Indefinite,
        onAction: () -> Unit = {},
    ) = showSnackbar(getString(message), actionLabel?.let { getString(it) }, withDismissAction, duration, onAction)

    suspend fun showSnackbar(
        message: UiText, actionLabel: UiText? = null, withDismissAction: Boolean = true,
        duration: SnackbarDuration = if (actionLabel == null) SnackbarDuration.Short else SnackbarDuration.Indefinite,
        onAction: () -> Unit = {},
    ) = showSnackbar(message.asStringAsync(), actionLabel?.asStringAsync(), withDismissAction, duration, onAction)
}
