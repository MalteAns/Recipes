package de.malteans.recipes.core.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dropdown(
    selectedOption: Pair<Any?, String>,
    options: Map<Any, String>,
    onValueChanged: (Any) -> Unit,
    label: @Composable (() -> Unit)? = null,
    optionIcon: @Composable ((Any?) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedText(
            value = selectedOption.second,
            singleLine = true,
            label = label,
            leadingIcon = if (optionIcon != null) { { optionIcon(selectedOption.first) } }
                else null,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = true)
                .fillMaxWidth(),
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { (option, text) ->
                DropdownMenuItem(
                    text = { Text(text = text) },
                    onClick = {
                        expanded = false
                        onValueChanged(option)
                    },
                    leadingIcon = if (optionIcon != null) { { optionIcon(option) } }
                        else null
                )
            }
        }
    }
}