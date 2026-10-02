package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.RoastLanguage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiLiveRoastService {

    companion object {
        const val LIVE_MODEL = "gemini-3.8-live"
        const val FALLBACK_MODEL = "gemini-3.5-flash"
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    fun isConfigured(): Boolean {
        val key = BuildConfig.GEMINI_API_KEY
        return !key.isNullOrBlank() && key != "MY_GEMINI_API_KEY"
    }

    suspend fun sendLiveMessage(
        userMessage: String,
        conversationHistory: List<Pair<String, String>>, // sender ("user" or "model"), text
        language: RoastLanguage
    ): Pair<String, Boolean> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val hasKey = isConfigured()

        if (!hasKey) {
            return@withContext Pair(getLiveDemoReply(userMessage, language), true)
        }

        try {
            // Attempt with requested model gemini-3.8-live, falling back to gemini-3.5-flash if preview routing differs
            val reply = callModel(LIVE_MODEL, apiKey, userMessage, conversationHistory, language)
                ?: callModel(FALLBACK_MODEL, apiKey, userMessage, conversationHistory, language)

            if (!reply.isNullOrBlank()) {
                Pair(reply, false)
            } else {
                Pair(getLiveDemoReply(userMessage, language), true)
            }
        } catch (e: Exception) {
            Log.e("GeminiLiveRoastService", "Error calling live API: ${e.message}", e)
            Pair(getLiveDemoReply(userMessage, language), true)
        }
    }

    private fun callModel(
        modelName: String,
        apiKey: String,
        userMessage: String,
        history: List<Pair<String, String>>,
        language: RoastLanguage
    ): String? {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/$modelName:generateContent?key=$apiKey"

        val systemInstruction = """
            You are a quick-witted, hilarious, and playful AI Roastmaster participating in a LIVE real-time voice roast battle with the user!
            Powered by $modelName Live API.

            VOICE & CONVERSATION RULES:
            - Deliver punchy, direct, spoken-style jokes (1 to 2 sentences max) suitable for text-to-speech audio delivery!
            - React directly to whatever the user says or asks. If they insult you, hit back with playful banter. If they ask you to roast their habits, roast them thoroughly!
            - Language:
              * If English: Modern Gen-Z sarcasm, witty burns, gaming, attendance, and sleep jokes.
              * If Tanglish: Casual conversational Tamil written in English letters ('Dei', 'Machi', 'scene podadha', 'attendance register', 'vibe check', 'sambaavam').
              * If Tamil: Authentic, colloquial spoken Tamil with punchlines.
            - Safety: Strictly playful friendly banter! NO threats, hate speech, slurs, sexual remarks, or attacks on protected traits. Harmless everyday targets only.
            - Always stay in character as the undefeated, confident comedy roastmaster!
        """.trimIndent()

        val contentsArray = JSONArray()

        // Add history turns (limit last 6 for fast latency)
        history.takeLast(6).forEach { (sender, text) ->
            val role = if (sender == "user") "user" else "model"
            contentsArray.put(JSONObject().apply {
                put("role", role)
                put("parts", JSONArray().apply {
                    put(JSONObject().put("text", text))
                })
            })
        }

        // Add current user message
        contentsArray.put(JSONObject().apply {
            put("role", "user")
            put("parts", JSONArray().apply {
                put(JSONObject().put("text", userMessage))
            })
        })

        val requestBodyJson = JSONObject().apply {
            put("contents", contentsArray)
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().put("text", systemInstruction))
                })
            })
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.95)
                put("topP", 0.95)
                put("maxOutputTokens", 160) // Keep short for conversational voice flow
            })
        }

        val request = Request.Builder()
            .url(url)
            .post(requestBodyJson.toString().toRequestBody(jsonMediaType))
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body?.string()

        if (!response.isSuccessful || responseBody.isNullOrBlank()) {
            Log.w("GeminiLiveRoastService", "Model $modelName returned status ${response.code}: $responseBody")
            return null
        }

        val root = JSONObject(responseBody)
        val candidates = root.optJSONArray("candidates") ?: return null
        if (candidates.length() == 0) return null

        val candidate = candidates.getJSONObject(0)
        val content = candidate.optJSONObject("content") ?: return null
        val parts = content.optJSONArray("parts") ?: return null
        if (parts.length() == 0) return null

        return parts.getJSONObject(0).optString("text", "").trim()
    }

    private fun getLiveDemoReply(userMessage: String, language: RoastLanguage): String {
        val lower = userMessage.lowercase()
        return when (language) {
            RoastLanguage.TANGLISH -> {
                when {
                    lower.contains("code") || lower.contains("bug") || lower.contains("programmer") ->
                        "Dei, un code run aagaradha vida un excuse list fast-ah compile aagum da! One semicolon missing-kku 4 hours Stack Overflow-la azhudhu iruppa! 😂"
                    lower.contains("sleep") || lower.contains("night") || lower.contains("wake") ->
                        "Machi un sleep schedule paatha NASA scientist-ke vertigo vandhurum. Morning 11 AM-kku good morning message anupura unna enna solradhu! 💀"
                    lower.contains("attendance") || lower.contains("class") || lower.contains("college") ->
                        "Class-ku entry kudukradhu mass hero madhiri, aana attendance percentage matum single digit-la vibrate aagudhu bro! 🚀"
                    lower.contains("hi") || lower.contains("hello") || lower.contains("who are you") ->
                        "Vanakkam da machi! Gemini 3.8 Live API roast champion inga irukken. En munnadi scene poda mudiyuma? Ketta kelvi kekkuriye! 🔥"
                    else ->
                        "Dei un pecha kettale theriyudhu, un WiFi router-kooda unna vida punctual-ah vela seiyyum da! 😂 Come at me with something tougher!"
                }
            }
            RoastLanguage.TAMIL -> {
                when {
                    lower.contains("code") || lower.contains("bug") ->
                        "கோடிங் பண்றேன்னு சொல்லி காப்பி பேஸ்ட் பண்ணியே கம்பெனியை மூட வைக்க போற ஒரே ஆளு நீங்க தான்! 💻😂"
                    lower.contains("sleep") || lower.contains("தூக்கம்") ->
                        "நைட் 3 மணி வரைக்கும் ரீல்ஸ் பாத்துட்டு, காலைல அலாரம் சவுண்டுக்கு தலையணையை அடிக்கிற ஒரே ஜீவன் நீங்க தான்! 😴💀"
                    else ->
                        "உங்க கான்பிடன்ஸை பாத்தா உலக அழகி பட்டம் வாங்க போற மாதிரி இருக்கு, ஆனா நிலைமை மட்டும் தரைமட்டமா இருக்கு! 🔥😂"
                }
            }
            RoastLanguage.ENGLISH -> {
                when {
                    lower.contains("code") || lower.contains("bug") || lower.contains("developer") ->
                        "You call yourself a developer, but the only thing you develop is anxiety when git push fails on a Friday afternoon! 💻💀"
                    lower.contains("sleep") || lower.contains("bed") || lower.contains("tired") ->
                        "Your sleep schedule is currently in a different hemisphere. You treat 3 AM like afternoon tea and wonder why your eyes look like glazed donuts! 🍩😂"
                    lower.contains("wifi") || lower.contains("internet") ->
                        "Your life stability is exactly like your 2.4 GHz Wi-Fi: one wall away and everything collapses into a buffering wheel! 📶🔥"
                    lower.contains("hi") || lower.contains("hello") || lower.contains("who are you") ->
                        "Welcome to the hot seat! I am your AI Roastmaster on Gemini 3.8 Live. Step up to the mic if your ego can take the heat! 🎙️🔥"
                    else ->
                        "You speak with the confidence of someone with 100% battery, but your logic is running at critical 1% power saving mode! ⚡😂 Give me your best shot!"
                }
            }
        }
    }
}
