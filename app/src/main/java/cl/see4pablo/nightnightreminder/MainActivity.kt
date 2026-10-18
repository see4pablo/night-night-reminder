package cl.see4pablo.nightnightreminder

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.core.content.ContextCompat
import cl.see4pablo.nightnightreminder.ui.ChecklistScreen
import cl.see4pablo.nightnightreminder.ui.ChecklistViewModel
import cl.see4pablo.nightnightreminder.voice.SpeechHelper

class MainActivity : ComponentActivity() {
    private val viewModel: ChecklistViewModel by viewModels()
    private var speechHelper: SpeechHelper? = null

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                startListening()
            } else {
                viewModel.onSpeechError("Microphone permission is required for voice checklists.")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        speechHelper =
            SpeechHelper(
                context = this,
                onPartial = viewModel::setPartial,
                onResult = viewModel::ingestSpokenText,
                onError = viewModel::onSpeechError,
                onListeningChanged = viewModel::setListening,
            )

        setContent {
            MaterialTheme {
                Surface {
                    ChecklistScreen(
                        onMicClick = ::onMicClick,
                        viewModel = viewModel,
                    )
                }
            }
        }
    }

    private fun onMicClick() {
        val helper = speechHelper ?: return
        if (viewModel.state.value.listening) {
            helper.stop()
            return
        }
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED -> startListening()
            else -> permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    private fun startListening() {
        val helper = speechHelper ?: return
        if (!helper.isAvailable) {
            viewModel.onSpeechError("Speech recognition is not available on this device.")
            return
        }
        helper.start()
    }

    override fun onDestroy() {
        speechHelper?.stop()
        speechHelper = null
        super.onDestroy()
    }
}
