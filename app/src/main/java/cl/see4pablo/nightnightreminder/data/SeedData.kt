package cl.see4pablo.nightnightreminder.data

import java.time.LocalDate

object SeedData {
    /** Cumpleaños de Pablo — checklist de mentira para probar "Anteriores". */
    fun birthdayDate(today: LocalDate = LocalDate.now()): LocalDate {
        val thisYear = LocalDate.of(today.year, 10, 9)
        return if (!thisYear.isAfter(today)) thisYear else thisYear.minusYears(1)
    }

    fun birthdayItems(): List<ChecklistItem> =
        listOf(
            ChecklistItem(text = "Comprar velas para la torta", done = true),
            ChecklistItem(text = "Encargar la torta de chocolate", done = true),
            ChecklistItem(text = "Invitar a la familia", done = true),
            ChecklistItem(text = "Elegir playlist de cumpleaños", done = false),
            ChecklistItem(text = "Comprar globos y guirnaldas", done = false),
            ChecklistItem(text = "Reservar mesa en el restaurante", done = true),
            ChecklistItem(text = "Preparar el brindis", done = false),
            ChecklistItem(text = "Cargar la cámara", done = false),
            ChecklistItem(text = "Envolver el regalo sorpresa", done = false),
            ChecklistItem(text = "Confirmar quién trae el hielo", done = false),
        )
}
