package cl.see4pablo.nightnightreminder.data

import java.time.LocalDate

object SeedData {
    /** Demo birthday checklist for the Past screen (Oct 9). */
    fun birthdayDate(today: LocalDate = LocalDate.now()): LocalDate {
        val thisYear = LocalDate.of(today.year, 10, 9)
        return if (!thisYear.isAfter(today)) thisYear else thisYear.minusYears(1)
    }

    fun birthdayItems(): List<ChecklistItem> =
        listOf(
            ChecklistItem(text = "Buy candles for the cake", done = true),
            ChecklistItem(text = "Order the chocolate cake", done = true),
            ChecklistItem(text = "Invite the family", done = true),
            ChecklistItem(text = "Pick a birthday playlist", done = false),
            ChecklistItem(text = "Buy balloons and streamers", done = false),
            ChecklistItem(text = "Book a restaurant table", done = true),
            ChecklistItem(text = "Prepare the toast", done = false),
            ChecklistItem(text = "Charge the camera", done = false),
            ChecklistItem(text = "Wrap the surprise gift", done = false),
            ChecklistItem(text = "Confirm who brings the ice", done = false),
        )
}
