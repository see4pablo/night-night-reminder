package cl.see4pablo.nightnightreminder.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import cl.see4pablo.nightnightreminder.ui.theme.DoneMuted
import cl.see4pablo.nightnightreminder.ui.theme.MicRed
import cl.see4pablo.nightnightreminder.ui.theme.NightMuted

@Composable
fun PastChecklistsScreen(viewModel: ChecklistViewModel) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF0B0D12), Color(0xFF10131A), Color(0xFF0A0C10)),
                    ),
                )
                .statusBarsPadding()
                .padding(horizontal = 28.dp),
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = viewModel::openHome) {
                Icon(
                    Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = NightMuted,
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = "PAST", style = MaterialTheme.typography.labelLarge)
                Text(
                    text = "Nights",
                    style = MaterialTheme.typography.headlineLarge,
                )
            }
            Spacer(modifier = Modifier.size(48.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (state.past.isEmpty()) {
            Text(
                text = "When you save tasks on different days, they’ll show up here.",
                style = MaterialTheme.typography.bodyMedium,
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(28.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(state.past, key = { it.date.toString() }) { day ->
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = ChecklistViewModel.formatDay(day.date).uppercase(),
                            style = MaterialTheme.typography.labelLarge,
                        )
                        day.items.forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                Icon(
                                    imageVector =
                                        if (item.done) {
                                            Icons.Outlined.CheckCircle
                                        } else {
                                            Icons.Outlined.Circle
                                        },
                                    contentDescription = null,
                                    tint = if (item.done) DoneMuted else MicRed.copy(alpha = 0.55f),
                                    modifier =
                                        Modifier
                                            .size(20.dp)
                                            .clickable(
                                                indication = null,
                                                interactionSource =
                                                    remember { MutableInteractionSource() },
                                                onClick = {
                                                    viewModel.togglePast(day.date, item.id)
                                                },
                                            ),
                                )
                                Text(
                                    text = item.text,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color =
                                        if (item.done) {
                                            DoneMuted
                                        } else {
                                            MaterialTheme.colorScheme.onBackground
                                        },
                                    textDecoration =
                                        if (item.done) TextDecoration.LineThrough else null,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
