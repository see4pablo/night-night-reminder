package cl.see4pablo.nightnightreminder.voice

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import cl.see4pablo.nightnightreminder.R

/** Short wooden UI cues via MediaPlayer. */
class UiSounds(context: Context) {
    private val appContext = context.applicationContext

    fun playStart() = play(R.raw.wood_start)

    fun playStop() = play(R.raw.wood_stop)

    fun playAdd() = play(R.raw.wood_add)

    private fun play(resId: Int) {
        runCatching {
            MediaPlayer.create(appContext, resId)?.apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build(),
                )
                setOnCompletionListener { mp ->
                    mp.reset()
                    mp.release()
                }
                setVolume(1f, 1f)
                start()
            }
        }
    }

    fun release() = Unit
}
