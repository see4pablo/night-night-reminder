package cl.see4pablo.nightnightreminder.data

import java.time.LocalDate

data class DailyChecklist(
    val date: LocalDate,
    val items: List<ChecklistItem> = emptyList(),
)
