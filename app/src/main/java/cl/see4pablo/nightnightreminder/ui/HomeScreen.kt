package cl.see4pablo.nightnightreminder.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.Close
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import cl.see4pablo.nightnightreminder.data.ChecklistItem
import cl.see4pablo.nightnightreminder.ui.theme.DoneMuted
import cl.see4pablo.nightnightreminder.ui.theme.JournalCover
import cl.see4pablo.nightnightreminder.ui.theme.JournalCoverEdge
import cl.see4pablo.nightnightreminder.ui.theme.JournalGold
import cl.see4pablo.nightnightreminder.ui.theme.MicRed
import cl.see4pablo.nightnightreminder.ui.theme.MicRedGlow
import cl.see4pablo.nightnightreminder.ui.theme.NightMuted
import cl.see4pablo.nightnightreminder.ui.theme.Paper
import cl.see4pablo.nightnightreminder.ui.theme.PaperInk
import cl.see4pablo.nightnightreminder.ui.theme.PaperLine
import cl.see4pablo.nightnightreminder.ui.theme.PaperMuted

@Composable
fun HomeScreen(
    onMicClick: () -> Unit,
    viewModel: ChecklistViewModel,
) {
    val state by viewModel.state.collectAsState()
    val listening = state.listening || state.sessionActive
    val diaryOpen = !listening && state.todayItems.isNotEmpty()

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF0A0B0F), Color(0xFF12141A), Color(0xFF0A0B0F)),
                    ),
                ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Sleepy Goals",
                style = MaterialTheme.typography.labelLarge,
                color = NightMuted,
            )
            Spacer(modifier = Modifier.height(16.dp))

            AnimatedContent(
                targetState = diaryOpen,
                transitionSpec = {
                    fadeIn(tween(420)) togetherWith fadeOut(tween(280))
                },
                label = "diary",
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxWidth(),
            ) { open ->
                if (open) {
                    OpenDiary(
                        dateLabel = ChecklistViewModel.formatDay(state.today),
                        items = state.todayItems,
                        addedCount = state.lastAddedCount,
                        onToggle = viewModel::toggleToday,
                        onRemove = viewModel::removeToday,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    ClosedDiary(
                        listening = listening,
                        partial = state.partialTranscript,
                        status =
                            when {
                                listening && state.partialTranscript.isBlank() -> "Listening…"
                                !listening -> "Anything left for tomorrow?"
                                else -> ""
                            },
                        onMicClick = onMicClick,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (diaryOpen) {
                Text(
                    text = "Tap mic below to add more",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NightMuted.copy(alpha = 0.7f),
                )
                Spacer(modifier = Modifier.height(8.dp))
                CompactMic(listening = false, onClick = onMicClick)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text =
                    if (state.past.isEmpty()) {
                        "Past nights"
                    } else {
                        "Past · ${state.past.size}"
                    },
                style = MaterialTheme.typography.labelLarge,
                color = NightMuted,
                modifier =
                    Modifier
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = viewModel::openPast,
                        )
                        .padding(vertical = 8.dp),
            )

            // Debug strip
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.padding(bottom = 20.dp),
            ) {
                Text(
                    text = "Try ping",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MicRed.copy(alpha = 0.65f),
                    modifier =
                        Modifier.clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = viewModel::debugFireNotificationNow,
                        ),
                )
                Text(
                    text = "Reschedule",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NightMuted.copy(alpha = 0.65f),
                    modifier =
                        Modifier.clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = viewModel::rescheduleBedtimeAlarm,
                        ),
                )
            }
        }
    }
}

@Composable
private fun ClosedDiary(
    listening: Boolean,
    partial: String,
    status: String,
    onMicClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth(0.82f)
                    .fillMaxHeight(0.62f)
                    .shadow(18.dp, RoundedCornerShape(6.dp))
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(JournalCover, Color(0xFF1A1A1A), JournalCover),
                        ),
                    )
                    .border(1.dp, JournalCoverEdge, RoundedCornerShape(6.dp))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onMicClick,
                    ),
            contentAlignment = Alignment.Center,
        ) {
            // Spine
            Box(
                modifier =
                    Modifier
                        .align(Alignment.CenterStart)
                        .fillMaxHeight()
                        .width(10.dp)
                        .background(Color(0xFF0A0A0A)),
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "SLEEPY\nGOALS",
                    style = MaterialTheme.typography.headlineLarge,
                    color = JournalGold,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(28.dp))
                CompactMic(listening = listening, onClick = onMicClick)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text =
                        when {
                            partial.isNotBlank() -> "“$partial”"
                            else -> status
                        },
                    style = MaterialTheme.typography.bodyMedium,
                    color = NightMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp),
                )
            }
        }
    }
}

@Composable
private fun OpenDiary(
    dateLabel: String,
    items: List<ChecklistItem>,
    addedCount: Int,
    onToggle: (String) -> Unit,
    onRemove: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .shadow(12.dp, RoundedCornerShape(4.dp))
                .clip(RoundedCornerShape(4.dp))
                .background(Paper)
                .border(1.dp, PaperLine, RoundedCornerShape(4.dp)),
    ) {
        // Ruled lines
        Canvas(modifier = Modifier.fillMaxSize()) {
            val step = 36.dp.toPx()
            var y = 72.dp.toPx()
            while (y < size.height) {
                drawLine(
                    color = PaperLine,
                    start = Offset(24.dp.toPx(), y),
                    end = Offset(size.width - 24.dp.toPx(), y),
                    strokeWidth = 1f,
                )
                y += step
            }
            // Margin line
            drawLine(
                color = Color(0xFFE0C8C0),
                start = Offset(48.dp.toPx(), 0f),
                end = Offset(48.dp.toPx(), size.height),
                strokeWidth = 1.5f,
            )
        }

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(start = 56.dp, end = 20.dp, top = 20.dp, bottom = 16.dp),
        ) {
            Text(
                text = dateLabel,
                style = MaterialTheme.typography.labelLarge,
                color = PaperMuted,
            )
            Text(
                text = "Tonight",
                style = MaterialTheme.typography.headlineMedium,
                color = PaperInk,
            )
            if (addedCount > 0) {
                Text(
                    text =
                        if (addedCount == 1) {
                            "1 item added"
                        } else {
                            "$addedCount items added"
                        },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MicRed.copy(alpha = 0.85f),
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(items, key = { it.id }) { item ->
                    PaperRow(
                        item = item,
                        onToggle = { onToggle(item.id) },
                        onRemove = { onRemove(item.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun PaperRow(
    item: ChecklistItem,
    onToggle: () -> Unit,
    onRemove: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(36.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = if (item.done) Icons.Outlined.CheckCircle else Icons.Outlined.Circle,
            contentDescription = null,
            tint = if (item.done) DoneMuted else PaperInk.copy(alpha = 0.55f),
            modifier =
                Modifier
                    .size(18.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onToggle,
                    ),
        )
        Text(
            text = item.text,
            style = MaterialTheme.typography.bodyLarge,
            color = if (item.done) DoneMuted else PaperInk,
            textDecoration = if (item.done) TextDecoration.LineThrough else null,
            modifier =
                Modifier
                    .weight(1f)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onToggle,
                    ),
        )
        IconButton(onClick = onRemove, modifier = Modifier.size(28.dp)) {
            Icon(
                Icons.Outlined.Close,
                contentDescription = "Remove",
                tint = PaperMuted,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Composable
private fun CompactMic(
    listening: Boolean,
    onClick: () -> Unit,
) {
    val pulse =
        rememberInfiniteTransition(label = "mic").animateFloat(
            initialValue = 1f,
            targetValue = if (listening) 1.08f else 1f,
            animationSpec =
                infiniteRepeatable(tween(850), RepeatMode.Reverse),
            label = "p",
        )
    Box(contentAlignment = Alignment.Center) {
        if (listening) {
            Box(
                modifier =
                    Modifier
                        .size(88.dp)
                        .scale(pulse.value)
                        .background(MicRedGlow, CircleShape),
            )
        }
        Box(
            modifier =
                Modifier
                    .size(64.dp)
                    .scale(if (listening) pulse.value else 1f)
                    .background(MicRed, CircleShape)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onClick,
                    ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Default.Mic,
                contentDescription = if (listening) "Stop" else "Record",
                tint = Color.White,
                modifier = Modifier.size(28.dp),
            )
        }
    }
}
