# AI Roast Generator 😂

A modern, responsive, playful AI roast generation app powered by Google Gemini AI with high-grade support for **English**, **Tamil (தமிழ்)**, and **Tanglish** (conversational Tamil written with English characters).

Available as both a **modern Jetpack Compose Android app** and a **responsive Flask web application**.

---

## 🌟 Features

* **🎙️ Live Voice Roast Battle (`gemini-3.8-live`)**:
  * Real-time conversational sparring with the AI Roastmaster using the **`gemini-3.8-live`** Live API.
  * Spoken audio delivery with Text-To-Speech and interactive speech recognition.
  * Real-time dynamic audio waveform visualizer responding to mic input volume.
  * Quick banter comebacks and text fallback for all environments.
* **😂 Multi-Language Roasts**:
  * **English**: Sharp, modern Gen-Z wit and punchy burns.
  * **Tanglish**: Authentic Tamil college banter written with English characters (*Dei, Machi, attendance percentage, scene podra, vibe*).
  * **Tamil**: Colloquial and humorous Tamil script (*தமிழ்*).
* **🔥 3 Spicy Roast Levels**:
  * 🔥 **Friendly**: Just a little spicy.
  * 🌶️ **Savage**: Friendship may survive.
  * ☢️ **Nuclear**: Proceed at your own risk.
* **🎭 7 Comedy Styles**: Funny, Sarcastic, Clever, Meme, College Friend, One-Liner, Stand-up Comedy.
* **🎲 Random Roast Mode**: One-tap harmless comedy targets (Sleep schedule, Attendance, Wi-Fi, Coding bugs, Alarm clock, Procrastination).
* **✨ 3 Unique Roast Variations**: Generates 3 unique variations per target with tab switching.
* **🛡️ Safety First Policy**: Focuses strictly on everyday harmless quirks — never threats, slurs, sexual content, or personal attacks on protected traits.
* **⚡ Intelligent Demo Mode**: Built-in curated comedy roasts when an API key is missing or offline so the app can always be enjoyed immediately.
* **📜 History & Favorites**: Local persistence to review, copy, share, favorite, and delete generated burns.
* **📤 Share & Copy**: One-tap native sharing to WhatsApp, Instagram, Telegram, SMS, or clipboard.

---

## 🛠️ Technology Stack

* **Android (Primary)**:
  * Kotlin & Jetpack Compose (Material 3)
  * Google Gemini API (`gemini-3.5-flash`)
  * Room Database (local offline persistence)
  * Coroutines & StateFlow (MVVM architecture)
  * OkHttp & Moshi
* **Web**:
  * Python 3 & Flask
  * HTML5, CSS3 (Glassmorphism & Gen-Z Dark Theme), Vanilla JavaScript
  * LocalStorage for history

---

## 🚀 Running the Web App

1. **Create and activate a virtual environment**:
   ```bash
   python -m venv venv
   # On Windows:
   venv\Scripts\activate
   # On macOS/Linux:
   source venv/bin/activate
   ```

2. **Install dependencies**:
   ```bash
   pip install -r requirements.txt
   ```

3. **Configure Environment Variables**:
   Copy `.env.example` to `.env`:
   ```bash
   cp .env.example .env
   ```
   Add your Gemini API Key:
   ```env
   GEMINI_API_KEY=your_gemini_api_key_here
   GEMINI_TEXT_MODEL=gemini-3.5-flash
   ```

4. **Run the server**:
   ```bash
   python app.py
   ```

5. **Open in browser**:
   Navigate to `http://127.0.0.1:5000`

---

## 📱 Android App Architecture

* `MainActivity.kt`: Top-level app scaffold, tab navigation (Roast, History, About), edge-to-edge system insets.
* `ui/RoastViewModel.kt`: Centralized state management for generation, random topics, Room database interaction.
* `data/remote/GeminiRoastService.kt`: Direct REST integration with Google Gemini `gemini-3.5-flash` model and fallback demo mode engine.
* `data/local/`: Room Database (`RoastEntity`, `RoastDao`, `RoastDatabase`) for storing history and favorites.
* `ui/components/`: Glassmorphism input card, flame cooking loader, and interactive result card.
