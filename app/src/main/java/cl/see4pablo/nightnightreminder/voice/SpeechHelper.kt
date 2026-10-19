package cl.see4pablo.nightnightreminder.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
    private val onSilenceTimeout: () -> Unit = {},
    /** Only fires if no speech started and no words within this window. */
    private val silenceTimeoutMs: Long = 3_000L,
) {
    private val appContext = context.applicationContext
    private var recognizer: SpeechRecognizer? = null
    private val handler = Handler(Looper.getMainLooper())
    private var speechStarted = false
    private var heardWords = false
    private var stoppingIntentionally = false

    private val silenceRunnable =
        Runnable {
            // Only stop if the user never started talking and we never got words.
            if (!speechStarted && !heardWords) {
                stoppingIntentionally = true
                stop(notifyListening = true)
                onSilenceTimeout()
            }
        }

    val isAvailable: Boolean
        get() = SpeechRecognizer.isRecognitionAvailable(appContext)

    fun start(locale: Locale = Locale.getDefault()) {
        if (!isAvailable) {
            onError("Speech recognition is not available on this device.")
            return
        }
        stop(notifyListening = false)
        speechStarted = false
        heardWords = false
        stoppingIntentionally = false

        val speech =
            SpeechRecognizer.createSpeechRecognizer(appContext).also { recognizer = it }
        speech.setRecognitionListener(
            object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    onListeningChanged(true)
                    armSilenceTimer()
                }

                override fun onBeginningOfSpeech() {
                    speechStarted = true
                    // User is speaking — wait for results; do not re-arm silence cutoff.
                    cancelSilenceTimer()
                }

                override fun onRmsChanged(rmsdB: Float) = Unit

                override fun onBufferReceived(buffer: ByteArray?) = Unit

                override fun onEndOfSpeech() {
                    cancelSilenceTimer()
                    onListeningChanged(false)
                }

                override fun onError(error: Int) {
                    cancelSilenceTimer()
                    onListeningChanged(false)
                    // Ignore client errors caused by our own stop/cancel.
                    if (stoppingIntentionally && error == SpeechRecognizer.ERROR_CLIENT) return
                    onError(errorMessage(error))
                }

                override fun onResults(results: Bundle?) {
                    cancelSilenceTimer()
                    onListeningChanged(false)
                    val text =
                        results
                            ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            ?.firstOrNull()
                            .orEmpty()
                    if (text.isBlank()) {
                        onError("No detecté palabras.")
                    } else {
                        heardWords = true
                        onResult(text)
                    }
                }

                override fun onPartialResults(partialResults: Bundle?) {
                    val text =
                        partialResults
                            ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            ?.firstOrNull()
                    if (!text.isNullOrBlank()) {
                        heardWords = true
                        speechStarted = true
                        cancelSilenceTimer()
                        onPartial(text)
                    }
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
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Night Recaller")
            }
        speech.startListening(intent)
    }

    fun stop(notifyListening: Boolean = true) {
        cancelSilenceTimer()
        stoppingIntentionally = true
        val current = recognizer
        recognizer = null
        try {
            current?.stopListening()
        } catch (_: Exception) {
        }
        try {
            current?.cancel()
        } catch (_: Exception) {
        }
        try {
            current?.destroy()
        } catch (_: Exception) {
        }
        if (notifyListening) onListeningChanged(false)
    }

    private fun armSilenceTimer() {
        cancelSilenceTimer()
        handler.postDelayed(silenceRunnable, silenceTimeoutMs)
    }

    private fun cancelSilenceTimer() {
        handler.removeCallbacks(silenceRunnable)
    }

    private fun errorMessage(code: Int): String =
        when (code) {
            SpeechRecognizer.ERROR_AUDIO -> "Error de micrófono."
            SpeechRecognizer.ERROR_CLIENT -> "Error del reconocedor."
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Permiso de micrófono requerido."
            SpeechRecognizer.ERROR_NETWORK,
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT,
            -> "Problema de red al reconocer."
            SpeechRecognizer.ERROR_NO_MATCH -> "No detecté palabras."
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Reconocedor ocupado. Esperá un momento."
            SpeechRecognizer.ERROR_SERVER -> "Error del servidor de voz."
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Silencio — dejé de grabar."
            else -> "Error de voz ($code)."
        }
}
