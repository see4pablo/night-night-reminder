package cl.see4pablo.nightnightreminder.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import cl.see4pablo.nightnightreminder.data.ChecklistItem
import cl.see4pablo.nightnightreminder.data.ChecklistParser
import cl.see4pablo.nightnightreminder.data.ChecklistStore
import cl.see4pablo.nightnightreminder.data.DailyChecklist
import cl.see4pablo.nightnightreminder.data.SeedData
import cl.see4pablo.nightnightreminder.notify.BedtimeNotifier
import cl.see4pablo.nightnightreminder.notify.BedtimeScheduler
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class AppScreen {
    Home,
    Today,
    Past,
}

data class ChecklistUiState(
    val screen: AppScreen = AppScreen.Home,
    val today: LocalDate = LocalDate.now(),
    val todayItems: List<ChecklistItem> = emptyList(),
    val past: List<DailyChecklist> = emptyList(),
    val sessionActive: Boolean = false,
    val listening: Boolean = false,
    val partialTranscript: String = "",
    val lastHeard: String = "",
    val status: String = "Tap the mic and say what’s left for tonight.",
    val nextAlarmLabel: String = "",
    val pendingCount: Int = 0,
    val lastAddedCount: Int = 0,
    val debugMessage: String = "",
)

class ChecklistViewModel(application: Application) : AndroidViewModel(application) {
    private val store = ChecklistStore(application)
    private val _state = MutableStateFlow(ChecklistUiState())
    val state: StateFlow<ChecklistUiState> = _state.asStateFlow()

    init {
        seedBirthdayIfNeeded()
        reloadFromStore()
        refreshScheduleInfo()
    }

    fun openPast() = _state.update { it.copy(screen = AppScreen.Past) }

    fun openToday() = _state.update { it.copy(screen = AppScreen.Today) }

    fun openHome() = _state.update { it.copy(screen = AppScreen.Home) }

    fun setSessionActive(active: Boolean) {
        _state.update {
            it.copy(
                sessionActive = active,
                status = if (active) "Listening…" else "Done.",
                partialTranscript = if (active) it.partialTranscript else "",
                listening = if (active) it.listening else false,
                // Fresh session resets the "items added" counter.
                lastAddedCount = if (active) 0 else it.lastAddedCount,
            )
        }
    }

    fun setListening(listening: Boolean) {
        _state.update { it.copy(listening = listening) }
    }

    fun setPartial(text: String) {
        _state.update { it.copy(partialTranscript = text) }
    }

    fun onSpeechError(message: String) {
        _state.update {
            it.copy(
                listening = false,
                status = message,
                partialTranscript = "",
            )
        }
    }

    /** @return how many new items were added (0 if none). */
    fun ingestSpokenText(spoken: String): Int {
        val lines = ChecklistParser.parse(spoken)
        if (lines.isEmpty()) {
            _state.update {
                it.copy(
                    listening = false,
                    lastHeard = spoken,
                    partialTranscript = "",
                    status = "Heard you, but couldn’t split tasks. Try saying «next».",
                    lastAddedCount = 0,
                )
            }
            return 0
        }
        val newItems = lines.map { ChecklistItem(text = it) }
        var added = 0
        _state.update { current ->
            val today = LocalDate.now()
            val existing = current.todayItems.map { it.text.lowercase() }.toSet()
            val uniqueNew = newItems.filter { it.text.lowercase() !in existing }
            added = uniqueNew.size
            val merged = current.todayItems + uniqueNew
            current.copy(
                today = today,
                listening = false,
                lastHeard = spoken,
                partialTranscript = "",
                todayItems = merged,
                status = "Added $added.",
                pendingCount = merged.count { !it.done },
                lastAddedCount = if (added > 0) current.lastAddedCount + added else current.lastAddedCount,
            )
        }
        persist()
        return added
    }

    fun toggleToday(id: String) {
        _state.update { current ->
            val items =
                current.todayItems.map { item ->
                    if (item.id == id) item.copy(done = !item.done) else item
                }
            current.copy(todayItems = items, pendingCount = items.count { !it.done })
        }
        persist()
    }

    fun removeToday(id: String) {
        _state.update { current ->
            val items = current.todayItems.filterNot { it.id == id }
            current.copy(todayItems = items, pendingCount = items.count { !it.done })
        }
        persist()
    }

    fun togglePast(date: LocalDate, id: String) {
        _state.update { current ->
            current.copy(
                past =
                    current.past.map { day ->
                        if (day.date != date) {
                            day
                        } else {
                            day.copy(
                                items =
                                    day.items.map { item ->
                                        if (item.id == id) item.copy(done = !item.done) else item
                                    },
                            )
                        }
                    },
            )
        }
        persist()
    }

    fun refreshScheduleInfo() {
        val app = getApplication<Application>()
        val canExact = BedtimeScheduler.canScheduleExact(app)
        val next = BedtimeScheduler.formatNextTrigger()
        val pending = _state.value.todayItems.count { !it.done }
        _state.update {
            it.copy(
                nextAlarmLabel = next,
                pendingCount = pending,
                debugMessage =
                    if (canExact) {
                        "Exact alarm OK · $pending pending"
                    } else {
                        "No exact alarm (system permission) · $pending pending"
                    },
            )
        }
    }

    fun debugFireNotificationNow() {
        val app = getApplication<Application>()
        BedtimeNotifier.showPendingNotification(app, debug = true)
        _state.update {
            it.copy(debugMessage = "Debug notification sent · ${it.pendingCount} pending")
        }
    }

    fun rescheduleBedtimeAlarm() {
        val app = getApplication<Application>()
        BedtimeNotifier.ensureChannel(app)
        BedtimeScheduler.scheduleDaily(app)
        refreshScheduleInfo()
        _state.update {
            it.copy(debugMessage = "Rescheduled · next ${it.nextAlarmLabel}")
        }
    }

    private fun seedBirthdayIfNeeded() {
        val all = store.loadAll().toMutableMap()
        val birthday = SeedData.birthdayDate()
        if (all.containsKey(birthday)) return
        all[birthday] = SeedData.birthdayItems()
        store.saveAll(all)
    }

    private fun reloadFromStore() {
        val today = LocalDate.now()
        val all = store.loadAll()
        val todayItems = all[today].orEmpty()
        val past =
            all
                .filterKeys { it != today }
                .map { (date, items) -> DailyChecklist(date, items) }
                .sortedByDescending { it.date }
        _state.update {
            it.copy(
                today = today,
                todayItems = todayItems,
                past = past,
                pendingCount = todayItems.count { !it.done },
            )
        }
    }

    private fun persist() {
        val s = _state.value
        val map =
            buildMap {
                put(s.today, s.todayItems)
                s.past.forEach { put(it.date, it.items) }
            }.filterValues { it.isNotEmpty() }
        store.saveAll(map)
        val past =
            map
                .filterKeys { it != s.today }
                .map { (date, items) -> DailyChecklist(date, items) }
                .sortedByDescending { it.date }
        _state.update { it.copy(past = past, pendingCount = s.todayItems.count { !it.done }) }
    }

    companion object {
        private val dayFmt =
            DateTimeFormatter.ofPattern("d MMM", Locale.ENGLISH)

        fun formatDay(date: LocalDate): String = date.format(dayFmt)
    }
}
