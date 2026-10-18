package cl.see4pablo.nightnightreminder.data

/**
 * Tiny MVP splitter: turn a spoken sentence into checklist lines.
 *
 * Examples:
 *  - "buy milk and bread and call mom"
 *  - "comprar pan, leche y huevos"
 */
object ChecklistParser {
    private val splitter =
        Regex(
            """\s*(?:,|;|/|\band\b|\by\b|\be\b|\bthen\b|\bdespués\b|\bluego\b)\s*""",
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
            .distinctBy { it.lowercase() }
    }
}
