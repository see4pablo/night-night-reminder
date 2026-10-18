package cl.see4pablo.nightnightreminder.ui

import androidx.lifecycle.ViewModel
import cl.see4pablo.nightnightreminder.data.ChecklistItem
import cl.see4pablo.nightnightreminder.data.ChecklistParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ChecklistUiState(
    val items: List<ChecklistItem> = emptyList(),
    val listening: Boolean = false,
    val partialTranscript: String = "",
    val lastHeard: String = "",
    val status: String = "Tap the mic and say what’s left for tonight.",
)

class ChecklistViewModel : ViewModel() {
    private val _state = MutableStateFlow(ChecklistUiState())
    val state: StateFlow<ChecklistUiState> = _state.asStateFlow()

    fun setListening(listening: Boolean) {
        _state.update {
            it.copy(
                listening = listening,
                status = if (listening) "Listening…" else it.status,
                partialTranscript = if (listening) it.partialTranscript else "",
            )
        }
    }

    fun setPartial(text: String) {
        _state.update { it.copy(partialTranscript = text) }
    }

    fun onSpeechError(message: String) {
        _state.update {
            it.copy(listening = false, status = message, partialTranscript = "")
        }
    }

    fun ingestSpokenText(spoken: String) {
        val lines = ChecklistParser.parse(spoken)
        if (lines.isEmpty()) {
            _state.update {
                it.copy(
                    listening = false,
                    lastHeard = spoken,
                    partialTranscript = "",
                    status = "Heard something, but couldn’t split it into tasks.",
                )
            }
            return
        }
        val newItems = lines.map { ChecklistItem(text = it) }
        _state.update { current ->
            current.copy(
                listening = false,
                lastHeard = spoken,
                partialTranscript = "",
                items = current.items + newItems,
                status = "Added ${newItems.size} item(s).",
            )
        }
    }

    fun toggle(id: String) {
        _state.update { current ->
            current.copy(
                items =
                    current.items.map { item ->
                        if (item.id == id) item.copy(done = !item.done) else item
                    },
            )
        }
    }

    fun remove(id: String) {
        _state.update { current ->
            current.copy(items = current.items.filterNot { it.id == id })
        }
    }

    fun clearDone() {
        _state.update { current ->
            current.copy(items = current.items.filterNot { it.done })
        }
    }

    fun clearAll() {
        _state.update {
            it.copy(items = emptyList(), status = "List cleared.", lastHeard = "")
        }
    }
}
