package cl.see4pablo.nightnightreminder.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

/** Night backdrop behind the journal. */
val NightInk = Color(0xFF0B0D12)
val NightSurface = Color(0xFF12151C)
val NightMuted = Color(0xFF8A90A0)
val NightText = Color(0xFFE8E6E1)

/** Closed journal cover. */
val JournalCover = Color(0xFF141414)
val JournalCoverEdge = Color(0xFF2A2A2A)
val JournalGold = Color(0xFFC4B59A)

/** Open paper page. */
val Paper = Color(0xFFF3ECDF)
val PaperLine = Color(0xFFD9D0C0)
val PaperInk = Color(0xFF1C1A17)
val PaperMuted = Color(0xFF6E675C)

val MicRed = Color(0xFFD62828)
val MicRedGlow = Color(0x33D62828)
val MicCreamSoft = Color(0xFF9A9288)
val DoneMuted = Color(0xFF8A8378)

private val NightColors =
    darkColorScheme(
        primary = MicRed,
        onPrimary = NightText,
        background = NightInk,
        onBackground = NightText,
        surface = NightSurface,
        onSurface = NightText,
        onSurfaceVariant = NightMuted,
        secondary = NightMuted,
        onSecondary = NightInk,
    )

private val NightTypography =
    Typography(
        displayLarge =
            TextStyle(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Light,
                fontSize = 42.sp,
                lineHeight = 46.sp,
                letterSpacing = 1.sp,
                color = NightText,
                textAlign = TextAlign.Center,
            ),
        headlineLarge =
            TextStyle(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Normal,
                fontSize = 28.sp,
                letterSpacing = 0.5.sp,
                color = NightText,
                textAlign = TextAlign.Center,
            ),
        headlineMedium =
            TextStyle(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Normal,
                fontSize = 22.sp,
                letterSpacing = 0.3.sp,
                color = NightText,
            ),
        titleMedium =
            TextStyle(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                letterSpacing = 1.5.sp,
                color = NightMuted,
            ),
        bodyLarge =
            TextStyle(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp,
                lineHeight = 28.sp,
                color = PaperInk,
            ),
        bodyMedium =
            TextStyle(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = NightMuted,
            ),
        labelLarge =
            TextStyle(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                letterSpacing = 2.sp,
                color = NightMuted,
            ),
    )

@Composable
fun NightTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = NightColors,
        typography = NightTypography,
        content = content,
    )
}
