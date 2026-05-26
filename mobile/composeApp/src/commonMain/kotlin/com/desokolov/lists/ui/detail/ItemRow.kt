package com.desokolov.lists.ui.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.desokolov.lists.domain.model.ListItem

@Composable
fun ItemRow(
    item: ListItem,
    currentUserId: String,
    onClaim: () -> Unit,
    onUnclaim: () -> Unit,
    onCheck: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = item.isDone,
            onCheckedChange = onCheck
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = if (item.isDone) TextDecoration.LineThrough else null
            )
            if (!item.quantity.isNullOrBlank()) {
                Text(
                    text = item.quantity,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (item.claimedByName != null) {
                Text(
                    text = "→ ${item.claimedByName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        when {
            item.claimedBy == currentUserId && !item.isDone -> TextButton(onClick = onUnclaim) {
                Text("Снять")
            }
            item.claimedBy == null && !item.isDone -> TextButton(onClick = onClaim) {
                Text("Взять")
            }
        }
    }
}
