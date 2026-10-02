package com.example.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.example.data.model.RoastLanguage
import java.util.Locale

class VoiceManager(
    private val context: Context,
    private val onTranscriptReady: (String) -> Unit,
    private val onPartialTranscript: (String) -> Unit,
    private val onAudioLevelChanged: (Float) -> Unit,
    private val onSpeakingStateChanged: (Boolean) -> Unit,
    private val onListeningStateChanged: (Boolean) -> Unit,
    private val onError: (String) -> Unit
) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false
    private var speechRecognizer: SpeechRecognizer? = null

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsInitialized = true
            tts?.setPitch(1.05f)
            tts?.setSpeechRate(1.02f)
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    onSpeakingStateChanged(true)
                }

                override fun onDone(utteranceId: String?) {
                    onSpeakingStateChanged(false)
                }

                override fun onError(utteranceId: String?) {
                    onSpeakingStateChanged(false)
                }
            })
        } else {
            Log.w("VoiceManager", "TextToSpeech init failed with status: $status")
        }
    }

    fun speak(text: String, language: RoastLanguage) {
        if (!isTtsInitialized || tts == null) {
            return
        }

        try {
            when (language) {
                RoastLanguage.TAMIL -> {
                    val tamilLocale = Locale("ta", "IN")
                    if (tts?.isLanguageAvailable(tamilLocale) == TextToSpeech.LANG_AVAILABLE) {
                        tts?.language = tamilLocale
                    } else {
                        tts?.language = Locale.getDefault()
                    }
                }
                RoastLanguage.TANGLISH -> {
                    // Tanglish sounds best with Indian English or default English TTS
                    val indianEnglish = Locale("en", "IN")
                    if (tts?.isLanguageAvailable(indianEnglish) == TextToSpeech.LANG_AVAILABLE) {
                        tts?.language = indianEnglish
                    } else {
                        tts?.language = Locale.US
                    }
                }
                RoastLanguage.ENGLISH -> {
                    tts?.language = Locale.US
                }
            }

            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "ROAST_UTTERANCE_${System.currentTimeMillis()}")
        } catch (e: Exception) {
            Log.e("VoiceManager", "Error in speak: ${e.message}")
        }
    }

    fun stopSpeaking() {
        tts?.stop()
        onSpeakingStateChanged(false)
    }

    fun startListening(language: RoastLanguage) {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            onError("Speech recognition is not available on this device. You can still type below! 🎙️")
            return
        }

        stopSpeaking()
        stopListening()

        try {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        onListeningStateChanged(true)
                    }

                    override fun onBeginningOfSpeech() {
                        onListeningStateChanged(true)
                    }

                    override fun onRmsChanged(rmsdB: Float) {
                        // Normalize -2dB to 10dB to 0.0f - 1.0f
                        val normalized = ((rmsdB + 2f) / 12f).coerceIn(0f, 1f)
                        onAudioLevelChanged(normalized)
                    }

                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {
                        onListeningStateChanged(false)
                        onAudioLevelChanged(0f)
                    }

                    override fun onError(error: Int) {
                        onListeningStateChanged(false)
                        onAudioLevelChanged(0f)
                        val msg = when (error) {
                            SpeechRecognizer.ERROR_NO_MATCH -> "Didn't catch that. Tap the mic and try again! 😂"
                            SpeechRecognizer.ERROR_NETWORK -> "Network issue while listening."
                            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error."
                            else -> "Could not hear you clearly. Tap mic to retry!"
                        }
                        if (error != SpeechRecognizer.ERROR_NO_MATCH) {
                            Log.w("VoiceManager", "SpeechRecognizer error: $error")
                        }
                    }

                    override fun onResults(results: Bundle?) {
                        onListeningStateChanged(false)
                        onAudioLevelChanged(0f)
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull()
                        if (!text.isNullOrBlank()) {
                            onTranscriptReady(text)
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val partial = matches?.firstOrNull()
                        if (!partial.isNullOrBlank()) {
                            onPartialTranscript(partial)
                        }
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)

                when (language) {
                    RoastLanguage.TAMIL -> putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ta-IN")
                    RoastLanguage.TANGLISH -> putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN")
                    RoastLanguage.ENGLISH -> putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
                }
            }

            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            Log.e("VoiceManager", "Error starting speech recognition: ${e.message}")
            onListeningStateChanged(false)
            onError("Could not start microphone: ${e.message}")
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
            speechRecognizer?.destroy()
            speechRecognizer = null
            onListeningStateChanged(false)
            onAudioLevelChanged(0f)
        } catch (e: Exception) {
            Log.e("VoiceManager", "Error stopping speech: ${e.message}")
        }
    }

    fun destroy() {
        stopListening()
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
