package cl.see4pablo.nightnightreminder.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate

/** Simple SharedPreferences JSON store: one checklist per calendar day. */
class ChecklistStore(context: Context) {
    private val prefs =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun loadAll(): Map<LocalDate, List<ChecklistItem>> {
        val raw = prefs.getString(KEY, null) ?: return emptyMap()
        return runCatching {
            val root = JSONObject(raw)
            buildMap {
                for (key in root.keys()) {
                    val date = LocalDate.parse(key)
                    put(date, parseItems(root.getJSONArray(key)))
                }
            }
        }.getOrDefault(emptyMap())
    }

    fun saveAll(checklists: Map<LocalDate, List<ChecklistItem>>) {
        val root = JSONObject()
        checklists.forEach { (date, items) ->
            root.put(date.toString(), itemsToJson(items))
        }
        prefs.edit().putString(KEY, root.toString()).apply()
    }

    private fun parseItems(array: JSONArray): List<ChecklistItem> =
        buildList {
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                add(
                    ChecklistItem(
                        id = obj.getString("id"),
                        text = obj.getString("text"),
                        done = obj.optBoolean("done", false),
                    ),
                )
            }
        }

    private fun itemsToJson(items: List<ChecklistItem>): JSONArray =
        JSONArray().also { array ->
            items.forEach { item ->
                array.put(
                    JSONObject()
                        .put("id", item.id)
                        .put("text", item.text)
                        .put("done", item.done),
                )
            }
        }

    companion object {
        private const val PREFS = "night_night_checklists"
        private const val KEY = "by_date"
    }
}
