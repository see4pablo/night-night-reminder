package cl.see4pablo.nightnightreminder.notify

import java.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Test

class BedtimeSchedulerTest {
    @Test
    fun beforeEightSchedulesSameDay() {
        val now = LocalDateTime.of(2026, 10, 18, 19, 30)
        val next = BedtimeScheduler.nextTriggerDateTime(now)
        assertEquals(LocalDateTime.of(2026, 10, 18, 20, 0), next)
    }

    @Test
    fun atOrAfterEightSchedulesNextDay() {
        val now = LocalDateTime.of(2026, 10, 18, 20, 0)
        val next = BedtimeScheduler.nextTriggerDateTime(now)
        assertEquals(LocalDateTime.of(2026, 10, 19, 20, 0), next)

        val later = LocalDateTime.of(2026, 10, 18, 22, 15)
        assertEquals(
            LocalDateTime.of(2026, 10, 19, 20, 0),
            BedtimeScheduler.nextTriggerDateTime(later),
        )
    }
}
