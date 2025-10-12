package de.malteans.recipes.core.presentation.add.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.malteans.recipes.core.domain.Ingredient

@Composable
fun IngredientListItem(
    ingredient: Ingredient,
    amount: Double?,
    unit: String?,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .clickable { onEdit() }
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1.5f)
            ) {
                Text(
                    text = ingredient.name,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(text = "${amount?.toNiceString() ?: ""} ${unit?: ""}")
            }
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit",
            )
        }
        Row(
            modifier = Modifier
                .clickable { onDelete() }
                .padding(vertical = 4.dp)
        ) {
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
            )
        }
    }
}

fun Double.toNiceString(): String {
    return if (this % 1 == 0.0) {
        this.toInt().toString()
    } else {
        this.toString()
    }
}