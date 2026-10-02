package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.RoastLanguage
import com.example.data.model.RoastLevel
import com.example.data.model.RoastRequest
import com.example.data.model.RoastStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiRoastService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    fun isConfigured(): Boolean {
        val key = BuildConfig.GEMINI_API_KEY
        return !key.isNullOrBlank() && key != "MY_GEMINI_API_KEY"
    }

    suspend fun generateRoasts(request: RoastRequest): Pair<List<String>, Boolean> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val hasKey = isConfigured()

        if (!hasKey) {
            Log.d("GeminiRoastService", "No active Gemini API key found. Using rich demo mode roasts.")
            return@withContext Pair(getDemoRoasts(request), true)
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

            val promptText = buildPrompt(request)
            val systemText = buildSystemInstruction(request)

            val requestBodyJson = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", promptText))
                        })
                    })
                })
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", systemText))
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.9)
                    put("topP", 0.95)
                    put("topK", 40)
                })
            }

            val httpRequest = Request.Builder()
                .url(url)
                .post(requestBodyJson.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(httpRequest).execute()
            val responseBody = response.body?.string()

            if (!response.isSuccessful || responseBody.isNullOrBlank()) {
                Log.w("GeminiRoastService", "Gemini API call failed with code ${response.code}: $responseBody")
                return@withContext Pair(getDemoRoasts(request), true)
            }

            val parsedRoasts = parseGeminiResponse(responseBody)
            if (parsedRoasts.isNotEmpty()) {
                Pair(parsedRoasts, false)
            } else {
                Pair(getDemoRoasts(request), true)
            }
        } catch (e: Exception) {
            Log.e("GeminiRoastService", "Error calling Gemini API: ${e.message}", e)
            Pair(getDemoRoasts(request), true)
        }
    }

    private fun buildSystemInstruction(request: RoastRequest): String {
        return """
            You are a professional comedy writer and world-class roastmaster specializing in playful, friendly, and wildly hilarious roasts.
            Analyze the user's target description and create 3 distinct, creative, punchy jokes.
            The roast should feel like friendly banter between close college friends or coworkers.

            SAFETY MANDATES:
            - Never create threats, hate speech, slurs, sexual content, serious accusations, or attacks based on protected characteristics (race, religion, gender, disability, nationality).
            - Do not make claims about real-world criminal behavior or serious wrongdoing.
            - If the input requests harmful or hateful content, politely and playfully transform it into a harmless comedic joke about harmless everyday habits (e.g. coffee obsession, phone battery at 2%, bad Wi-Fi, sleeping in class).
            - The roast MUST attack harmless quirks: procrastination, bad Wi-Fi, gaming habits, overconfidence, exam preparation, food habits, being late, coding bugs, Instagram habits, college life, movie obsession, sleep schedule.

            LANGUAGE & TONE RULES:
            - Selected Language: ${request.language.displayName}
            ${
                when (request.language) {
                    RoastLanguage.TANGLISH -> """
                        - CRITICAL FOR TANGLISH: Understand Tamil written in English letters. Respond naturally in casual Tanglish that Tamil college friends actually speak!
                        - Use natural conversational Tamil-English slang like 'Dei', 'Machi', 'Bro', 'scene podra', 'attendance', 'vibe', 'sambaavam', 'oru vazhi aaitaaru'.
                        - Do NOT translate into formal classical Tamil. Make it sound like modern Chennai / Tamil Nadu college campus banter.
                    """.trimIndent()
                    RoastLanguage.TAMIL -> """
                        - CRITICAL FOR TAMIL: Respond in lively, colloquial Tamil (தமிழ்) with relatable everyday humor, punchlines, and playful wit.
                    """.trimIndent()
                    RoastLanguage.ENGLISH -> """
                        - CRITICAL FOR ENGLISH: Punchy, modern Gen-Z banter, witty metaphors, sharp comedic timing, and hilarious comparisons.
                    """.trimIndent()
                }
            }

            OUTPUT FORMAT:
            Provide exactly 3 numbered roasts in this format:
            1. [First roast joke with emojis]
            2. [Second roast joke with emojis]
            3. [Third roast joke with emojis]
            Do not include introductions, disclaimer preambles, or markdown asterisks around the numbers.
        """.trimIndent()
    }

    private fun buildPrompt(request: RoastRequest): String {
        return """
            Target Name: ${request.name}
            About Them / Situation: ${request.description}
            Roast Level: ${request.level.label} (${request.level.subtitle})
            Language: ${request.language.displayName}
            Roast Style: ${request.style.displayName}

            Generate 3 unique, hilarious roast variations for ${request.name} matching the requested level, language, and style.
        """.trimIndent()
    }

    private fun parseGeminiResponse(jsonString: String): List<String> {
        val roasts = mutableListOf<String>()
        try {
            val root = JSONObject(jsonString)
            val candidates = root.optJSONArray("candidates") ?: return emptyList()
            if (candidates.length() == 0) return emptyList()

            val firstCandidate = candidates.getJSONObject(0)
            val content = firstCandidate.optJSONObject("content") ?: return emptyList()
            val parts = content.optJSONArray("parts") ?: return emptyList()
            if (parts.length() == 0) return emptyList()

            val text = parts.getJSONObject(0).optString("text", "")
            if (text.isBlank()) return emptyList()

            val lines = text.split("\n")
            var currentRoast = StringBuilder()

            for (line in lines) {
                val trimmed = line.trim()
                val numberedMatch = Regex("^\\d+[\\.\\)]\\s*(.*)").find(trimmed)
                if (numberedMatch != null) {
                    if (currentRoast.isNotBlank()) {
                        roasts.add(currentRoast.toString().trim())
                        currentRoast = StringBuilder()
                    }
                    currentRoast.append(numberedMatch.groupValues[1])
                } else if (trimmed.isNotBlank()) {
                    if (currentRoast.isNotEmpty()) {
                        currentRoast.append(" ").append(trimmed)
                    } else if (roasts.isEmpty() && !trimmed.startsWith("#")) {
                        currentRoast.append(trimmed)
                    }
                }
            }

            if (currentRoast.isNotBlank()) {
                roasts.add(currentRoast.toString().trim())
            }

            // Fallback if formatting was paragraph-based
            if (roasts.isEmpty()) {
                val paragraphs = text.split(Regex("\n\n+")).map { it.trim() }.filter { it.length > 10 }
                roasts.addAll(paragraphs)
            }
        } catch (e: Exception) {
            Log.e("GeminiRoastService", "Failed to parse response: ${e.message}")
        }
        return roasts.take(3)
    }

    fun getDemoRoasts(request: RoastRequest): List<String> {
        val name = if (request.name.isBlank()) "Bro" else request.name.trim()
        val desc = request.description.trim()

        return when (request.language) {
            RoastLanguage.TANGLISH -> getTanglishDemoRoasts(name, desc, request.level, request.style)
            RoastLanguage.TAMIL -> getTamilDemoRoasts(name, desc, request.level, request.style)
            RoastLanguage.ENGLISH -> getEnglishDemoRoasts(name, desc, request.level, request.style)
        }
    }

    private fun getTanglishDemoRoasts(name: String, desc: String, level: RoastLevel, style: RoastStyle): List<String> {
        return when (level) {
            RoastLevel.FRIENDLY -> listOf(
                "Dei $name, nee late ah varradhu habit illa da... time itself unakku wait pannitu tired aagiduchu! 😂",
                "Bro enters classroom like a mass hero slow motion entry, aana attendance register-la zero motion! 💀",
                "$name oda 5-minute ETA is scientifically proven to be longer than an Interstellar black hole orbit da! 🚀"
            )
            RoastLevel.SAVAGE -> listOf(
                "$name has two speeds: loading... and still loading. Reel-la scene podradhu matum 4K, exam paper-la matum 144p resolution! 💀🔥",
                "Dei $name, un phone battery 1%-la irukkum bothu kooda un confidence vida fast-ah work aagum da. Wi-Fi router-kooda unna vida punctual! 📶😂",
                "Avan dancer illa da, Wi-Fi signal kedaikaama phone-ah aattura maadhiri aadurraan. But respect for the confidence! 🕺💀"
            )
            RoastLevel.NUCLEAR -> listOf(
                "Dei $name, un syllabus cover panradha vida un excuse list perusa irukku! Exam hall-ku entry aagum bodhu invigilator kooda 'Nee yaaru pa guest appearance-ah?' nu kekuraanga! ☢️💀",
                "Bro plays BGMI for 6 hours straight, loses all matches, and still blames the 20ms ping for his life choices. 🎮🔥",
                "$name oda daily routine: Padikkanum nu book open pannuvaan, 30 seconds-la 3 hours reel paapan, apram 'Vibe check failed' nu thoongi poiduvaan! 😴💀"
            )
        }
    }

    private fun getTamilDemoRoasts(name: String, desc: String, level: RoastLevel, style: RoastStyle): List<String> {
        return when (level) {
            RoastLevel.FRIENDLY -> listOf(
                "$name எப்பவும் லேட்டா வரது ஒன்னும் புதுசு இல்ல... காலண்டர் கூட இவருக்காக புதுசா ஒரு கிழமை ரெடி பண்ண போகுது! 😂",
                "கிளாஸ்க்கு வர்றதை விட இவரு ரீல்ஸ் பாக்கிற நேரம் தான் அதிகம்... ஆனா வைப் மட்டும் குறைச்சலே இல்ல! 💀",
                "$name ஓட பிளானிங் எப்படின்னா... ஜிம்முக்கு பீஸ் கட்டுவாரு, ஆனா அங்க போறதுக்கு பதில் வீட்ல தூங்கி ரெஸ்ட் எடுப்பாரு! 🏋️‍♂️😂"
            )
            RoastLevel.SAVAGE -> listOf(
                "$name டான்ஸ் ஆடுறேன்னு சொல்லி இவரு போடுற ஸ்டெப்ஸ பாத்தா, மழை பெய்ய வைக்க பழங்குடி மக்கள் பண்ற சடங்கு மாதிரி இருக்கு! 🕺💀",
                "இவரோட கான்பிடன்ஸ் ஹிமாலய மலையை விட பெருசு, ஆனா இன்டர்னல் மார்க் மட்டும் தரைமட்டமா இருக்கு! 🔥😂",
                "போன் சார்ஜர் தேடுற அளவுக்கு கூட வாழ்க்கையில லட்சியத்தை தேட மாட்டாரு நம்ம $name! 📱💀"
            )
            RoastLevel.NUCLEAR -> listOf(
                "$name ஓட தூக்க அட்டவணை நாசா விஞ்ஞானிகளுக்கே புரியாத மர்மம்... நைட் 3 மணிக்கு ஆன்லைன்ல இருப்பாரு, காலைல 11 மணிக்கு அலாரம் ஆஃப் பண்ணிட்டு திருப்பி தூங்குவாரு! ☢️😴",
                "எக்ஸாம் ஹாலுக்குள்ள போனா கொஸ்டின் பேப்பர் இவரை பாக்குது, இவரு கொஸ்டின் பேப்பரை பாக்குறாரு... ரெண்டு பேருக்கும் ஒன்னும் புரியல! 💀🔥",
                "கோடிங் பண்றேன்னு சொல்லி ஒரு செமிகோலன் விட்டுட்டு 4 மணி நேரம் ஸ்டேக் ஓவர்ஃப்ளோல உட்கார்ந்து அழுகுற ஒரே ஆளு நம்ம $name தான்! 💻☢️"
            )
        }
    }

    private fun getEnglishDemoRoasts(name: String, desc: String, level: RoastLevel, style: RoastStyle): List<String> {
        return when (level) {
            RoastLevel.FRIENDLY -> listOf(
                "$name has two speeds: loading... and still loading. Bro enters the room like it's a movie climax, but the only thing dramatic is the attendance percentage. 💀",
                "$name treats the snooze button like an Olympic sport and the to-do list like Terms & Conditions: immediately ignored without reading. 😂",
                "If overthinking was a high-paying career, $name would already be on the Forbes 30 Under 30 list. 🧠✨"
            )
            RoastLevel.SAVAGE -> listOf(
                "Your Wi-Fi has more stable connections than your life choices. You spend 5 hours gaming, lose every match, and still blame 30ms latency for your lack of skills. 🎮💀",
                "$name says 'I am 5 minutes away' with such raw conviction that even Google Maps starts questioning its own satellite calculations. 📍😂",
                "You have 47 browser tabs open, 3 unfinished assignments, and you're sitting there deciding what font to use for your desktop folder. 🔥💀"
            )
            RoastLevel.NUCLEAR -> listOf(
                "$name's phone battery is permanently at 2%, their sleep schedule is based on Tokyo time zone while living locally, and their motivation arrives exclusively at 3:15 AM! ☢️💀",
                "Bro wrote two lines of code, encountered a syntax error, stared at the wall for 45 minutes, and concluded that AI is coming for everyone's job anyway. 💻🔥",
                "Your gym subscription has been donating charity to that fitness center for 8 months straight. The dumbbells miss you more than your ex. 🏋️‍♂️☢️"
            )
        }
    }
}
