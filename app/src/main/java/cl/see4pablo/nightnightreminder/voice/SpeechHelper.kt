package cl.see4pablo.nightnightreminder.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import java.util.Locale

class SpeechHelper(
    context: Context,
    private val onPartial: (String) -> Unit = {},
    private val onResult: (String) -> Unit,
    private val onError: (String) -> Unit,
    private val onListeningChanged: (Boolean) -> Unit,
) {
    private val appContext = context.applicationContext
    private var recognizer: SpeechRecognizer? = null

    val isAvailable: Boolean
        get() = SpeechRecognizer.isRecognitionAvailable(appContext)

    fun start(locale: Locale = Locale.getDefault()) {
        if (!isAvailable) {
            onError("Speech recognition is not available on this device.")
            return
        }
        stop()
        val speech =
            SpeechRecognizer.createSpeechRecognizer(appContext).also { recognizer = it }
        speech.setRecognitionListener(
            object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    onListeningChanged(true)
                }

                override fun onBeginningOfSpeech() = Unit

                override fun onRmsChanged(rmsdB: Float) = Unit

                override fun onBufferReceived(buffer: ByteArray?) = Unit

                override fun onEndOfSpeech() {
                    onListeningChanged(false)
                }

                override fun onError(error: Int) {
                    onListeningChanged(false)
                    onError(errorMessage(error))
                }

                override fun onResults(results: Bundle?) {
                    onListeningChanged(false)
                    val text =
                        results
                            ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            ?.firstOrNull()
                            .orEmpty()
                    if (text.isBlank()) {
                        onError("I didn't catch that. Try again.")
                    } else {
                        onResult(text)
                    }
                }

                override fun onPartialResults(partialResults: Bundle?) {
                    val text =
                        partialResults
                            ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            ?.firstOrNull()
                    if (!text.isNullOrBlank()) onPartial(text)
                }

                override fun onEvent(eventType: Int, params: Bundle?) = Unit
            },
        )

        val intent =
            Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM,
                )
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, locale.toLanguageTag())
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Night Night Reminder")
            }
        speech.startListening(intent)
    }

    fun stop() {
        recognizer?.stopListening()
        recognizer?.cancel()
        recognizer?.destroy()
        recognizer = null
        onListeningChanged(false)
    }

    private fun errorMessage(code: Int): String =
        when (code) {
            SpeechRecognizer.ERROR_AUDIO -> "Microphone error."
            SpeechRecognizer.ERROR_CLIENT -> "Recognition client error."
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission required."
            SpeechRecognizer.ERROR_NETWORK,
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT,
            -> "Network problem during recognition."
            SpeechRecognizer.ERROR_NO_MATCH -> "No speech match. Try again."
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer busy. Wait a moment."
            SpeechRecognizer.ERROR_SERVER -> "Recognition server error."
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech heard. Try again."
            else -> "Speech error ($code)."
        }
}
