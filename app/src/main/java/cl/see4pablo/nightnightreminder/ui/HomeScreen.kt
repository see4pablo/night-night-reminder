package cl.see4pablo.nightnightreminder.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cl.see4pablo.nightnightreminder.ui.theme.MicRed
import cl.see4pablo.nightnightreminder.ui.theme.MicRedGlow
import cl.see4pablo.nightnightreminder.ui.theme.NightMuted
import cl.see4pablo.nightnightreminder.ui.theme.NightText

@Composable
fun HomeScreen(
    onMicClick: () -> Unit,
    viewModel: ChecklistViewModel,
) {
    val state by viewModel.state.collectAsState()
    val listening = state.listening || state.sessionActive

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF0B0D12), Color(0xFF10131A), Color(0xFF0A0C10)),
                    ),
                ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(0.14f))

            Text(
                text = "Night\nRecaller",
                style = MaterialTheme.typography.displayLarge,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "¿Algo pendiente\npara mañana?",
                style = MaterialTheme.typography.headlineMedium,
                color = NightText.copy(alpha = 0.88f),
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text =
                    when {
                        state.partialTranscript.isNotBlank() -> "«${state.partialTranscript}»"
                        listening -> "Te escucho…"
                        else -> "Decilo en voz alta"
                    },
                style = MaterialTheme.typography.bodyMedium,
                color = NightMuted,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.weight(0.1f))

            RedMicButton(listening = listening, onClick = onMicClick)

            Spacer(modifier = Modifier.weight(0.12f))

            HomeNavButton(
                title = "Hoy",
                subtitle =
                    if (state.pendingCount == 0) {
                        ChecklistViewModel.formatDay(state.today)
                    } else {
                        "${state.pendingCount} pendiente${if (state.pendingCount == 1) "" else "s"}"
                    },
                onClick = viewModel::openToday,
            )
            if (state.lastAddedCount > 0) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text =
                        if (state.lastAddedCount == 1) {
                            "1 ítem agregado"
                        } else {
                            "${state.lastAddedCount} ítems agregados"
                        },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MicRed.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(modifier = Modifier.height(22.dp))
            HomeNavButton(
                title = "Anteriores",
                subtitle =
                    if (state.past.isEmpty()) {
                        "Sin noches guardadas"
                    } else {
                        "${state.past.size} checklist${if (state.past.size == 1) "" else "s"}"
                    },
                onClick = viewModel::openPast,
            )

            Spacer(modifier = Modifier.weight(0.16f))

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "20:00 · ${state.nextAlarmLabel.ifBlank { "—" }}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NightMuted.copy(alpha = 0.65f),
                    textAlign = TextAlign.Center,
                )
                if (state.debugMessage.isNotBlank()) {
                    Text(
                        text = state.debugMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = NightMuted.copy(alpha = 0.55f),
                        textAlign = TextAlign.Center,
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    Text(
                        text = "Probar aviso",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MicRed.copy(alpha = 0.7f),
                        modifier =
                            Modifier.clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                                onClick = viewModel::debugFireNotificationNow,
                            ),
                    )
                    Text(
                        text = "Reprogramar",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NightMuted.copy(alpha = 0.75f),
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
}

@Composable
private fun RedMicButton(
    listening: Boolean,
    onClick: () -> Unit,
) {
    val pulse =
        rememberInfiniteTransition(label = "micPulse").animateFloat(
            initialValue = 1f,
            targetValue = if (listening) 1.08f else 1f,
            animationSpec =
                infiniteRepeatable(
                    animation = tween(850),
                    repeatMode = RepeatMode.Reverse,
                ),
            label = "pulse",
        )

    Box(contentAlignment = Alignment.Center) {
        if (listening) {
            Box(
                modifier =
                    Modifier
                        .size(132.dp)
                        .scale(pulse.value)
                        .background(MicRedGlow, CircleShape),
            )
        }
        Box(
            modifier =
                Modifier
                    .size(100.dp)
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
                imageVector = Icons.Default.Mic,
                contentDescription = if (listening) "Detener" else "Grabar",
                tint = Color.White,
                modifier = Modifier.size(40.dp),
            )
        }
    }
}

@Composable
private fun HomeNavButton(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onClick,
                )
                .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = NightMuted,
        )
    }
}
