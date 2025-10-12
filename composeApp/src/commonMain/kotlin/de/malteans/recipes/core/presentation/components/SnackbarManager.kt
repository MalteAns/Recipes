package de.malteans.recipes.core.presentation.components

import androidx.compose.material3.SnackbarDuration
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

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
        message: String, actionLabel: String? = null, withDismissAction: Boolean = false,
        duration: SnackbarDuration = if (actionLabel == null) SnackbarDuration.Short else SnackbarDuration.Indefinite,
        onAction: () -> Unit = {},
    ) {
        _snackbarMessages.emit(SnackbarValue(message, actionLabel, withDismissAction, duration, onAction))
    }
}
