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

val NightInk = Color(0xFF0B0D12)
val NightSurface = Color(0xFF12151C)
val NightMuted = Color(0xFF8A90A0)
val NightText = Color(0xFFE8E6E1)
val MicRed = Color(0xFFD62828)
val MicRedGlow = Color(0x33D62828)
val MicCream = Color(0xFFC8C0B4)
val MicCreamSoft = Color(0xFF9A9288)
val MicCreamGlow = Color(0x22C8C0B4)
val DoneMuted = Color(0xFF5C6370)

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
                fontSize = 48.sp,
                lineHeight = 52.sp,
                letterSpacing = 0.5.sp,
                color = NightText,
                textAlign = TextAlign.Center,
            ),
        displayMedium =
            TextStyle(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Light,
                fontSize = 36.sp,
                letterSpacing = 1.sp,
                color = NightText,
                textAlign = TextAlign.Center,
            ),
        headlineLarge =
            TextStyle(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Normal,
                fontSize = 32.sp,
                letterSpacing = 0.5.sp,
                color = NightText,
                textAlign = TextAlign.Center,
            ),
        headlineMedium =
            TextStyle(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Normal,
                fontSize = 24.sp,
                letterSpacing = 0.5.sp,
                color = NightText,
            ),
        titleMedium =
            TextStyle(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                letterSpacing = 1.2.sp,
                color = NightMuted,
            ),
        bodyLarge =
            TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Normal,
                fontSize = 17.sp,
                lineHeight = 24.sp,
                color = NightText,
            ),
        bodyMedium =
            TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = NightMuted,
            ),
        labelLarge =
            TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
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
