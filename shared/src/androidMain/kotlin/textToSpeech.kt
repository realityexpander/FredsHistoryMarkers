
import android.media.AudioAttributes
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.Locale
import kotlin.math.min

var androidTextToSpeechService: AndroidTextToSpeechService? = null
class AndroidTextToSpeechService(
    private val androidTextToSpeech: TextToSpeech,
) : UtteranceProgressListener() {
    private var spokenWords: List<String> = ArrayList()
    private var currentSpokenWordIndex: Int = 0
    private var previousSpokenWordIndex: Int = 0
    private val kNumWordsToSpeakPerChunk = 10

    init {
        androidTextToSpeech.setOnUtteranceProgressListener(this)
        androidTextToSpeech.setSpeechRate(1.25f)
        androidTextToSpeech.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                .build())
    }

    fun speak(text: String) {
        // Resume speaking if there is unspoken text
        if(!isSpeaking() && spokenWords.isNotEmpty() && currentSpokenWordIndex < spokenWords.size) {
            resumeSpeaking()
            return
        }

        stopTextToSpeech()
        spokenWords = text.split(" ")
        currentSpokenWordIndex = 0

        // Speak the first words
        speakNextWords()
    }

    private fun speakNextWords() {
        previousSpokenWordIndex = currentSpokenWordIndex // Save this in case we need to resume speaking
        val nextWordsToSpeak =
            spokenWords.subList(currentSpokenWordIndex, currentSpokenWordIndex + min(kNumWordsToSpeakPerChunk, spokenWords.size - currentSpokenWordIndex))
        currentSpokenWordIndex += nextWordsToSpeak.size

        val map = Bundle()
        map.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "freds_markers$currentSpokenWordIndex")
        map.putString(TextToSpeech.Engine.KEY_PARAM_STREAM, "Map Marker Description Audio")

        androidTextToSpeech.speak(
            nextWordsToSpeak.joinToString(" "),
            TextToSpeech.QUEUE_ADD,
            map,
            "freds_markers"
        )
    }

    fun stopSpeaking() {
        androidTextToSpeech.stop()
        spokenWords = ArrayList()
        currentSpokenWordIndex = 0
    }

    fun pauseSpeaking() {
        androidTextToSpeech.stop()
        currentSpokenWordIndex = maxOf(previousSpokenWordIndex - kNumWordsToSpeakPerChunk, 0)
    }

    private fun resumeSpeaking() {
        // Speak the last word to resume speaking
        speakNextWords()
    }

    fun isSpeaking(): Boolean {
        return androidTextToSpeech.isSpeaking ?: false
    }

    override fun onStart(utteranceId: String?) {
        Log.d("tts", "onStart: $utteranceId")

        // Yes, this queues up the next words to speak RIGHT AFTER the current word has started speaking.
        // The issue is that the time between words is too long, so we need to queue up as fast as possible.
        if(spokenWords.isNotEmpty() && currentSpokenWordIndex < spokenWords.size) {
            speakNextWords()
        }
    }

    override fun onDone(utteranceId: String?) {
        Log.d("tts", "onDone: $utteranceId")
        // NOT USED - See onStart()
    }

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("deprecated")
    override fun onError(utteranceId: String?) {
        Log.d("tts", "onError: $utteranceId")
    }

    fun setLanguage(locale: Locale): Int {
        return androidTextToSpeech.setLanguage(locale)
    }

    fun shutdown() {
        androidTextToSpeech.shutdown()
    }
}

actual fun speakTextToSpeech(text: String) {
    androidTextToSpeechService?.speak(text)
}
actual fun stopTextToSpeech() {
    androidTextToSpeechService?.stopSpeaking()
}
actual fun isTextToSpeechSpeaking(): Boolean {
    return androidTextToSpeechService?.isSpeaking() == true
}
actual fun pauseTextToSpeech() {
    androidTextToSpeechService?.pauseSpeaking()
}
