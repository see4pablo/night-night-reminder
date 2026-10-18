package cl.see4pablo.nightnightreminder.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.see4pablo.nightnightreminder.data.ChecklistItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistScreen(
    onMicClick: () -> Unit,
    viewModel: ChecklistViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Night Night Reminder") },
                actions = {
                    TextButton(onClick = viewModel::clearDone) {
                        Text("Clear done")
                    }
                    TextButton(onClick = viewModel::clearAll) {
                        Text("Clear all")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onMicClick) {
                Icon(
                    imageVector = if (state.listening) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = if (state.listening) "Stop listening" else "Speak tasks",
                )
            }
        },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
        ) {
            Text(
                text = state.status,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (state.partialTranscript.isNotBlank()) {
                Spacer(Modifier = Modifier.height(4.dp))
                Text(
                    text = "“…${state.partialTranscript}”",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            if (state.lastHeard.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Last heard: ${state.lastHeard}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            if (state.items.isEmpty()) {
                Text(
                    text = "No tasks yet. Tap the mic and say something like:\n“brush teeth, pack bag, and charge phone”",
                    style = MaterialTheme.typography.bodyLarge,
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    items(state.items, key = { it.id }) { item ->
                        ChecklistRow(
                            item = item,
                            onToggle = { viewModel.toggle(item.id) },
                            onRemove = { viewModel.remove(item.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChecklistRow(
    item: ChecklistItem,
    onToggle: () -> Unit,
    onRemove: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(checked = item.done, onCheckedChange = { onToggle() })
        Text(
            text = item.text,
            modifier = Modifier.weight(1f),
            style =
                MaterialTheme.typography.bodyLarge.copy(
                    textDecoration = if (item.done) TextDecoration.LineThrough else null,
                ),
        )
        IconButton(onClick = onRemove) {
            Icon(Icons.Default.Delete, contentDescription = "Remove")
        }
    }
}
