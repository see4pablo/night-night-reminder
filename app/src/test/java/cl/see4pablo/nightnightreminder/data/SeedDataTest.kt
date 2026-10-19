package cl.see4pablo.nightnightreminder.data

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SeedDataTest {
    @Test
    fun birthdayUsesCurrentYearWhenAlreadyPassed() {
        val today = LocalDate.of(2026, 10, 18)
        assertEquals(LocalDate.of(2026, 10, 9), SeedData.birthdayDate(today))
    }

    @Test
    fun birthdayFallsBackToPreviousYearBeforeOct9() {
        val today = LocalDate.of(2026, 3, 1)
        assertEquals(LocalDate.of(2025, 10, 9), SeedData.birthdayDate(today))
    }

    @Test
    fun birthdaySeedHasTenItems() {
        assertEquals(10, SeedData.birthdayItems().size)
        assertTrue(SeedData.birthdayItems().any { it.done })
        assertTrue(SeedData.birthdayItems().any { !it.done })
    }
}
