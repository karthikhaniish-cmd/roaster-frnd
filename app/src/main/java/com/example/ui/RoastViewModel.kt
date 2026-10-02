package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.RoastDatabase
import com.example.data.local.RoastEntity
import com.example.data.model.RoastLanguage
import com.example.data.model.RoastLevel
import com.example.data.model.RoastRequest
import com.example.data.model.RoastResult
import com.example.data.model.RoastStyle
import com.example.data.remote.GeminiLiveRoastService
import com.example.data.remote.GeminiRoastService
import com.example.util.VoiceManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

sealed interface RoastUiState {
    object Idle : RoastUiState
    data class Cooking(val stepMessage: String) : RoastUiState
    data class Success(
        val result: RoastResult,
        val currentIndex: Int = 0,
        val isSaved: Boolean = false
    ) : RoastUiState
    data class Error(val message: String) : RoastUiState
}

data class RandomPreset(
    val name: String,
    val description: String,
    val language: RoastLanguage,
    val level: RoastLevel,
    val style: RoastStyle
)

enum class MessageRole { USER, AI }

data class LiveRoastMessage(
    val id: String = UUID.randomUUID().toString(),
    val role: MessageRole,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

class RoastViewModel(application: Application) : AndroidViewModel(application) {

    private val db = RoastDatabase.getInstance(application)
    private val dao = db.roastDao()
    private val service = GeminiRoastService()
    private val liveService = GeminiLiveRoastService()

    val historyRoasts: StateFlow<List<RoastEntity>> = dao.getAllRoasts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteRoasts: StateFlow<List<RoastEntity>> = dao.getFavoriteRoasts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Classic Generator State
    private val _targetName = MutableStateFlow("")
    val targetName = _targetName.asStateFlow()

    private val _description = MutableStateFlow("")
    val description = _description.asStateFlow()

    private val _selectedLanguage = MutableStateFlow(RoastLanguage.TANGLISH)
    val selectedLanguage = _selectedLanguage.asStateFlow()

    private val _selectedLevel = MutableStateFlow(RoastLevel.SAVAGE)
    val selectedLevel = _selectedLevel.asStateFlow()

    private val _selectedStyle = MutableStateFlow(RoastStyle.FUNNY)
    val selectedStyle = _selectedStyle.asStateFlow()

    private val _uiState = MutableStateFlow<RoastUiState>(RoastUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _isDemoMode = MutableStateFlow(!service.isConfigured())
    val isDemoMode = _isDemoMode.asStateFlow()

    // --- LIVE VOICE CONVERSATION STATE (gemini-3.8-live) ---
    private val _liveMessages = MutableStateFlow<List<LiveRoastMessage>>(
        listOf(
            LiveRoastMessage(
                role = MessageRole.AI,
                text = "🎙️ Step up to the mic! I am your AI Roastmaster powered by gemini-3.8-live. Speak your mind or tap a prompt below — let's see if your ego can survive! 🔥"
            )
        )
    )
    val liveMessages = _liveMessages.asStateFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening = _isListening.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking = _isSpeaking.asStateFlow()

    private val _isProcessingLive = MutableStateFlow(false)
    val isProcessingLive = _isProcessingLive.asStateFlow()

    private val _audioLevel = MutableStateFlow(0f)
    val audioLevel = _audioLevel.asStateFlow()

    private val _liveTranscript = MutableStateFlow("")
    val liveTranscript = _liveTranscript.asStateFlow()

    private val _isMuted = MutableStateFlow(false)
    val isMuted = _isMuted.asStateFlow()

    private val _liveError = MutableStateFlow<String?>(null)
    val liveError = _liveError.asStateFlow()

    val quickPrompts = listOf(
        "Roast my coding skills!",
        "Why is my sleep schedule so broken?",
        "Dei Machi oru semma roast kudu!",
        "Roast my college attendance!",
        "Come at me, you can't roast me!",
        "Roast my phone addiction!"
    )

    private val voiceManager = VoiceManager(
        context = application,
        onTranscriptReady = { spokenText ->
            _liveTranscript.value = ""
            sendLiveUserMessage(spokenText)
        },
        onPartialTranscript = { partial ->
            _liveTranscript.value = partial
        },
        onAudioLevelChanged = { level ->
            _audioLevel.value = level
        },
        onSpeakingStateChanged = { speaking ->
            _isSpeaking.value = speaking
        },
        onListeningStateChanged = { listening ->
            _isListening.value = listening
        },
        onError = { errorMsg ->
            _liveError.value = errorMsg
        }
    )

    private val randomPresets = listOf(
        RandomPreset(
            name = "Harish",
            description = "Always late to class, thinks he's a great dancer, spends 5 hours gaming",
            language = RoastLanguage.TANGLISH,
            level = RoastLevel.SAVAGE,
            style = RoastStyle.COLLEGE_FRIEND
        ),
        RandomPreset(
            name = "My Sleep Schedule",
            description = "Sleeps at 4 AM, wakes up at 1 PM with a headache, wonders why feeling tired all day",
            language = RoastLanguage.ENGLISH,
            level = RoastLevel.SAVAGE,
            style = RoastStyle.MEME
        ),
        RandomPreset(
            name = "Vignesh",
            description = "எப்போதும் லேட்டாக வருவான், ஆனா கிளாஸ்க்கு வரும்போது ஹீரோ மாதிரி ஸ்லோ மோஷன்ல வருவான்",
            language = RoastLanguage.TAMIL,
            level = RoastLevel.FRIENDLY,
            style = RoastStyle.FUNNY
        ),
        RandomPreset(
            name = "My Attendance",
            description = "Currently at 43%, calculating how many fake medical leaves needed to sit for semester exam",
            language = RoastLanguage.TANGLISH,
            level = RoastLevel.NUCLEAR,
            style = RoastStyle.COLLEGE_FRIEND
        ),
        RandomPreset(
            name = "My Wi-Fi Router",
            description = "Works seamlessly for 4K advertisements, instantly dies when attending an important viva or exam",
            language = RoastLanguage.ENGLISH,
            level = RoastLevel.SAVAGE,
            style = RoastStyle.SARCASTIC
        ),
        RandomPreset(
            name = "Karthik",
            description = "Says 'I will start studying from 6 PM', now it is 3 AM and watching documentary about how pyramids were built",
            language = RoastLanguage.TANGLISH,
            level = RoastLevel.SAVAGE,
            style = RoastStyle.FUNNY
        ),
        RandomPreset(
            name = "My Coding Skills",
            description = "Copies 15 lines from Stack Overflow, breaks the entire database, blames senior developer",
            language = RoastLanguage.ENGLISH,
            level = RoastLevel.NUCLEAR,
            style = RoastStyle.CLEVER
        ),
        RandomPreset(
            name = "Priya",
            description = "Takes 300 selfies for one Instagram story, complains every single morning that she has no clothes to wear",
            language = RoastLanguage.TANGLISH,
            level = RoastLevel.FRIENDLY,
            style = RoastStyle.MEME
        ),
        RandomPreset(
            name = "My Alarm Clock",
            description = "Rings 12 times with 5 minute snooze intervals, wakes up the neighbors but never wakes me up",
            language = RoastLanguage.ENGLISH,
            level = RoastLevel.SAVAGE,
            style = RoastStyle.ONE_LINER
        )
    )

    fun onTargetNameChanged(name: String) {
        _targetName.value = name
    }

    fun onDescriptionChanged(desc: String) {
        _description.value = desc
    }

    fun onLanguageSelected(lang: RoastLanguage) {
        _selectedLanguage.value = lang
    }

    fun onLevelSelected(lvl: RoastLevel) {
        _selectedLevel.value = lvl
    }

    fun onStyleSelected(stl: RoastStyle) {
        _selectedStyle.value = stl
    }

    fun applyRandomPreset() {
        val preset = randomPresets.random()
        _targetName.value = preset.name
        _description.value = preset.description
        _selectedLanguage.value = preset.language
        _selectedLevel.value = preset.level
        _selectedStyle.value = preset.style
    }

    fun generateRoast() {
        val name = _targetName.value.trim().ifEmpty { "Bro" }
        val desc = _description.value.trim()

        if (desc.isEmpty()) {
            _uiState.value = RoastUiState.Error("Please tell us a little bit about them or pick a random topic! 😂")
            return
        }

        viewModelScope.launch {
            val cookingSteps = listOf(
                "🔥 AI is lighting the barbecue grill...",
                "🌶️ Adding extra spice and comedic seasoning...",
                "🧠 Analyzing attendance & procrastination metrics...",
                "😂 Crafting the ultimate roast variations..."
            )

            for (step in cookingSteps) {
                _uiState.value = RoastUiState.Cooking(step)
                delay(320)
            }

            val request = RoastRequest(
                name = name,
                description = desc,
                language = _selectedLanguage.value,
                level = _selectedLevel.value,
                style = _selectedStyle.value
            )

            val (roasts, isDemo) = service.generateRoasts(request)
            _isDemoMode.value = isDemo

            if (roasts.isNotEmpty()) {
                val result = RoastResult(
                    targetName = name,
                    description = desc,
                    roasts = roasts,
                    language = _selectedLanguage.value,
                    level = _selectedLevel.value,
                    style = _selectedStyle.value,
                    isDemo = isDemo
                )

                val id = dao.insertRoast(
                    RoastEntity(
                        targetName = name,
                        description = desc,
                        roastText = roasts[0],
                        language = _selectedLanguage.value.displayName,
                        level = _selectedLevel.value.label,
                        style = _selectedStyle.value.displayName
                    )
                )

                _uiState.value = RoastUiState.Success(
                    result = result.copy(id = id),
                    currentIndex = 0,
                    isSaved = false
                )
            } else {
                _uiState.value = RoastUiState.Error("Oops! The AI kitchen got too spicy. Try again in a moment! 😂")
            }
        }
    }

    fun selectVariation(index: Int) {
        val current = _uiState.value
        if (current is RoastUiState.Success) {
            if (index in current.result.roasts.indices) {
                _uiState.value = current.copy(currentIndex = index)
            }
        }
    }

    fun saveCurrentRoast() {
        val current = _uiState.value
        if (current is RoastUiState.Success) {
            val currentRoastText = current.result.roasts.getOrNull(current.currentIndex) ?: return
            viewModelScope.launch {
                dao.insertRoast(
                    RoastEntity(
                        targetName = current.result.targetName,
                        description = current.result.description,
                        roastText = currentRoastText,
                        language = current.result.language.displayName,
                        level = current.result.level.label,
                        style = current.result.style.displayName,
                        isFavorite = true
                    )
                )
                _uiState.value = current.copy(isSaved = true)
            }
        }
    }

    fun toggleFavorite(id: Long) {
        viewModelScope.launch {
            dao.toggleFavorite(id)
        }
    }

    fun deleteRoast(id: Long) {
        viewModelScope.launch {
            dao.deleteById(id)
        }
    }

    fun roastAgain() {
        _uiState.value = RoastUiState.Idle
    }

    fun dismissError() {
        _uiState.value = RoastUiState.Idle
    }

    // --- LIVE VOICE CONVERSATION METHODS ---

    fun toggleListening() {
        if (_isListening.value) {
            voiceManager.stopListening()
        } else {
            voiceManager.stopSpeaking()
            _liveError.value = null
            voiceManager.startListening(_selectedLanguage.value)
        }
    }

    fun sendLiveUserMessage(text: String) {
        val clean = text.trim()
        if (clean.isBlank()) return

        val userMsg = LiveRoastMessage(role = MessageRole.USER, text = clean)
        _liveMessages.value = _liveMessages.value + userMsg
        _isProcessingLive.value = true

        viewModelScope.launch {
            val history = _liveMessages.value.takeLast(8).map {
                (if (it.role == MessageRole.USER) "user" else "model") to it.text
            }

            val (replyText, isDemo) = liveService.sendLiveMessage(
                userMessage = clean,
                conversationHistory = history,
                language = _selectedLanguage.value
            )

            _isProcessingLive.value = false
            val aiMsg = LiveRoastMessage(role = MessageRole.AI, text = replyText)
            _liveMessages.value = _liveMessages.value + aiMsg

            // Auto-speak response if not muted
            if (!_isMuted.value) {
                voiceManager.speak(replyText, _selectedLanguage.value)
            }
        }
    }

    fun toggleMute() {
        val next = !_isMuted.value
        _isMuted.value = next
        if (next) {
            voiceManager.stopSpeaking()
        }
    }

    fun speakAiMessage(text: String) {
        voiceManager.speak(text, _selectedLanguage.value)
    }

    fun clearLiveBattle() {
        voiceManager.stopSpeaking()
        voiceManager.stopListening()
        _liveMessages.value = listOf(
            LiveRoastMessage(
                role = MessageRole.AI,
                text = "🎙️ Fresh round! I am primed on gemini-3.8-live. Speak up or tap a prompt below! 🔥"
            )
        )
    }

    fun dismissLiveError() {
        _liveError.value = null
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager.destroy()
    }
}
