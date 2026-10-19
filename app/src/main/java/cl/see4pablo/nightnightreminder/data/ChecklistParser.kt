package cl.see4pablo.nightnightreminder.data

/**
 * Splits spoken text into checklist lines.
 *
 * Separators (natural speech):
 *  - commas / "and" / "y" / "después" / "luego"
 *
 * Magic word (explicit new task):
 *  - "también", "además", "siguiente", "next", "nueva"
 *
 * Examples:
 *  - "cepillar dientes también cargar teléfono también empacar mochila"
 *  - "brush teeth, pack bag, and charge phone"
 */
object ChecklistParser {
    private val splitter =
        Regex(
            """\s*(?:,|;|/|\band\b|\by\b|\be\b|\bthen\b|\bdespués\b|\bluego\b|\btambién\b|\btambien\b|\bademás\b|\bademas\b|\bsiguiente\b|\bnext\b|\bnueva\b)\s*""",
            RegexOption.IGNORE_CASE,
        )

    private val junkPrefixes =
        listOf(
            Regex("""^(please|por favor)\s+""", RegexOption.IGNORE_CASE),
            Regex(
                """^(remind me to|remember to|i need to|i have to|add|lista|anota|recuérdame|recuerdame)\s+""",
                RegexOption.IGNORE_CASE,
            ),
        )

    fun parse(spoken: String): List<String> {
        var text = spoken.trim()
        if (text.isEmpty()) return emptyList()

        for (prefix in junkPrefixes) {
            text = text.replace(prefix, "")
        }

        return text
            .split(splitter)
            .map { it.trim().trim('.', '!', '?') }
            .filter { it.length >= 2 }
            .map { capitalizePhrase(it) }
            .distinctBy { it.lowercase() }
    }

    /** "cepillar dientes" → "Cepillar dientes" */
    fun capitalizePhrase(phrase: String): String {
        val trimmed = phrase.trim()
        if (trimmed.isEmpty()) return trimmed
        return trimmed.replaceFirstChar { ch ->
            if (ch.isLowerCase()) ch.titlecase(java.util.Locale.getDefault()) else ch.toString()
        }
    }
}
