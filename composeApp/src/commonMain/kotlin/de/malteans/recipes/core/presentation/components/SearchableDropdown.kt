package de.malteans.recipes.core.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SearchableDropdown(
    label: @Composable (() -> Unit)? = null,
    selectedOption: Pair<T?, String>,
    options: Map<T, String>,
    onValueChanged: (T) -> Unit,
    onValueAdded: ((String) -> Unit)? = null,
    enabled: Boolean = true,
    optionIcon: @Composable ((T?) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    var currentInput by remember { mutableStateOf(selectedOption.second) }
    val focusManager = LocalFocusManager.current

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
            if (expanded)
                currentInput = ""
        },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = if (expanded) currentInput else selectedOption.second,
            enabled = enabled,
            readOnly = !expanded,
            singleLine = true,
            onValueChange = {
                currentInput = it
            },
            label = label,
            leadingIcon = optionIcon?.let { { optionIcon(selectedOption.first) } },
            colors = OutlinedTextFieldDefaults.colors(),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    expanded = false
                    focusManager.clearFocus()
                    val option = options.entries.find { it.value.equals(currentInput, ignoreCase = true) }?.key
                    if (currentInput.isNotBlank()) {
                        if (option == null) {
                            onValueAdded?.invoke(currentInput)
                        } else {
                            onValueChanged(option)
                        }
                    }
                }
            ),
            modifier = Modifier
                .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryEditable, enabled = enabled)
                .fillMaxWidth(),
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                focusManager.clearFocus()
            }
        ) {
            if (currentInput.isNotBlank()) {
                options.forEach { (option, text) ->
                    if (text.contains(currentInput, ignoreCase = true)) {
                        DropdownMenuItem(
                            text = { Text(text = text) },
                            onClick = {
                                expanded = false
                                currentInput = text
                                focusManager.clearFocus()
                                onValueChanged(option)
                            },
                            leadingIcon = if (optionIcon != null) { { optionIcon(option) } }
                                else null
                        )
                    }
                }
            }
        }
    }
}