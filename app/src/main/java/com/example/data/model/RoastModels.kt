package com.example.data.model

enum class RoastLevel(val label: String, val emoji: String, val subtitle: String) {
    FRIENDLY("Friendly", "🔥", "Just a little spicy"),
    SAVAGE("Savage", "🌶️", "Friendship may survive"),
    NUCLEAR("Nuclear", "☢️", "Proceed at your own risk")
}

enum class RoastLanguage(val displayName: String, val code: String, val badge: String) {
    ENGLISH("English", "en", "🇺🇸 Eng"),
    TAMIL("Tamil", "ta", "🇮🇳 தமிழ்"),
    TANGLISH("Tanglish", "tanglish", "🔥 Tanglish")
}

enum class RoastStyle(val displayName: String, val emoji: String) {
    FUNNY("Funny", "😂"),
    SARCASTIC("Sarcastic", "😏"),
    CLEVER("Clever", "🧠"),
    MEME("Meme", "💀"),
    COLLEGE_FRIEND("College Friend", "🤝"),
    ONE_LINER("One-Liner", "⚡"),
    STAND_UP("Stand-up Comedy", "🎤")
}

data class RoastRequest(
    val name: String,
    val description: String,
    val language: RoastLanguage,
    val level: RoastLevel,
    val style: RoastStyle
)

data class RoastResult(
    val id: Long = 0,
    val targetName: String,
    val description: String,
    val roasts: List<String>,
    val language: RoastLanguage,
    val level: RoastLevel,
    val style: RoastStyle,
    val timestamp: Long = System.currentTimeMillis(),
    val isDemo: Boolean = false
)
