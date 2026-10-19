package cl.see4pablo.nightnightreminder

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import cl.see4pablo.nightnightreminder.notify.BedtimeNotifier
import cl.see4pablo.nightnightreminder.notify.BedtimeScheduler
import cl.see4pablo.nightnightreminder.ui.AppScreen
import cl.see4pablo.nightnightreminder.ui.ChecklistViewModel
import cl.see4pablo.nightnightreminder.ui.HomeScreen
import cl.see4pablo.nightnightreminder.ui.PastChecklistsScreen
import cl.see4pablo.nightnightreminder.ui.TodayScreen
import cl.see4pablo.nightnightreminder.ui.theme.NightInk
import cl.see4pablo.nightnightreminder.ui.theme.NightTheme
import cl.see4pablo.nightnightreminder.voice.SpeechHelper
import cl.see4pablo.nightnightreminder.voice.UiSounds

class MainActivity : ComponentActivity() {
    private val viewModel: ChecklistViewModel by viewModels()
    private var speechHelper: SpeechHelper? = null
    private var uiSounds: UiSounds? = null
    private var wantContinuous = false
    private val mainHandler = Handler(Looper.getMainLooper())

    private val micPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                beginSession()
            } else {
                viewModel.onSpeechError("Microphone permission is required for voice checklists.")
            }
        }

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            viewModel.rescheduleBedtimeAlarm()
            viewModel.refreshScheduleInfo()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars =
            false

        BedtimeNotifier.ensureChannel(this)
        BedtimeScheduler.scheduleDaily(this)
        ensureNotificationPermission()
        viewModel.refreshScheduleInfo()

        uiSounds = UiSounds(this)
        speechHelper =
            SpeechHelper(
                context = this,
                onPartial = viewModel::setPartial,
                onResult = ::onSpeechResult,
                onError = ::onSpeechError,
                onListeningChanged = viewModel::setListening,
                onSilenceTimeout = ::onSilenceTimeout,
            )

        setContent {
            NightTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = NightInk,
                ) {
                    val state by viewModel.state.collectAsState()
                    when (state.screen) {
                        AppScreen.Home ->
                            HomeScreen(
                                onMicClick = ::onMicClick,
                                viewModel = viewModel,
                            )
                        AppScreen.Today -> TodayScreen(viewModel = viewModel)
                        AppScreen.Past -> PastChecklistsScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshScheduleInfo()
    }

    private fun ensureNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val granted =
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
        if (!granted) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun onMicClick() {
        if (viewModel.state.value.sessionActive) {
            endSession(playSound = true)
            return
        }
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED -> beginSession()
            else -> micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    private fun beginSession() {
        val helper = speechHelper ?: return
        if (!helper.isAvailable) {
            viewModel.onSpeechError("Speech recognition is not available on this device.")
            return
        }
        wantContinuous = true
        viewModel.setSessionActive(true)
        uiSounds?.playStart()
        // Let the wood tap finish before opening the mic (avoids fighting the recognizer).
        mainHandler.postDelayed({
            if (wantContinuous) helper.start()
        }, 180)
    }

    private fun endSession(playSound: Boolean) {
        wantContinuous = false
        mainHandler.removeCallbacksAndMessages(null)
        speechHelper?.stop()
        viewModel.setSessionActive(false)
        if (playSound) uiSounds?.playStop()
    }

    private fun onSilenceTimeout() {
        wantContinuous = false
        viewModel.setSessionActive(false)
        viewModel.onSpeechError("No words — stopped listening.")
        uiSounds?.playStop()
    }

    private fun onSpeechResult(text: String) {
        val added = viewModel.ingestSpokenText(text)
        if (added > 0) {
            uiSounds?.playAdd()
        }
        if (wantContinuous) {
            mainHandler.postDelayed({
                if (wantContinuous) speechHelper?.start()
            }, 450)
        }
    }

    private fun onSpeechError(message: String) {
        viewModel.onSpeechError(message)
        val endSessionQuietly =
            message.contains("Silence", ignoreCase = true) ||
                message.contains("No words", ignoreCase = true)
        if (endSessionQuietly) {
            wantContinuous = false
            viewModel.setSessionActive(false)
            uiSounds?.playStop()
            return
        }
        if (!wantContinuous) return
        if (message.contains("permission", ignoreCase = true)) {
            endSession(playSound = false)
            return
        }
        // Soft retry (busy / network blip).
        mainHandler.postDelayed({
            if (wantContinuous) speechHelper?.start()
        }, 700)
    }

    override fun onDestroy() {
        wantContinuous = false
        mainHandler.removeCallbacksAndMessages(null)
        speechHelper?.stop()
        speechHelper = null
        uiSounds?.release()
        uiSounds = null
        super.onDestroy()
    }
}
