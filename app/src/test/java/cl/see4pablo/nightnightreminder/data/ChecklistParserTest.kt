package cl.see4pablo.nightnightreminder.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ChecklistParserTest {
    @Test
    fun splitsEnglishWithAndAndCommas() {
        val items =
            ChecklistParser.parse("brush teeth, pack bag, and charge phone")
        assertEquals(
            listOf("Brush teeth", "Pack bag", "Charge phone"),
            items,
        )
    }

    @Test
    fun splitsSpanishWithMagicWordTambien() {
        val items =
            ChecklistParser.parse(
                "cepillar dientes también cargar teléfono también empacar mochila",
            )
        assertEquals(
            listOf("Cepillar dientes", "Cargar teléfono", "Empacar mochila"),
            items,
        )
    }

    @Test
    fun stripsJunkPrefixes() {
        val items = ChecklistParser.parse("recuérdame comprar pan y leche")
        assertEquals(listOf("Comprar pan", "Leche"), items)
    }

    @Test
    fun capitalizesEachPhrase() {
        assertEquals("Hola mundo", ChecklistParser.capitalizePhrase("hola mundo"))
        assertEquals("Already Capital", ChecklistParser.capitalizePhrase("Already Capital"))
    }

    @Test
    fun emptyOrTooShortReturnsEmpty() {
        assertTrue(ChecklistParser.parse("").isEmpty())
        assertTrue(ChecklistParser.parse("   ").isEmpty())
        assertTrue(ChecklistParser.parse("a").isEmpty())
    }

    @Test
    fun dedupesCaseInsensitive() {
        val items = ChecklistParser.parse("Leche también leche también LECHE")
        assertEquals(listOf("Leche"), items)
    }
}
