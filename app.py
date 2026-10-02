import os
import re
import json
import requests
from flask import Flask, render_template, request, jsonify
from dotenv import load_dotenv

load_dotenv()

app = Flask(__name__)

GEMINI_API_KEY = os.environ.get("GEMINI_API_KEY", "")
GEMINI_TEXT_MODEL = os.environ.get("GEMINI_TEXT_MODEL", "gemini-3.5-flash")
GEMINI_LIVE_MODEL = os.environ.get("GEMINI_LIVE_MODEL", "gemini-3.8-live")

DEMO_ROASTS = {
    "Tanglish": [
        "Dei {name}, nee late ah varradhu habit illa da... time itself unakku wait pannitu tired aagiduchu! 😂",
        "Bro enters classroom like a mass hero slow motion entry, aana attendance register-la zero motion! 💀",
        "{name} oda 5-minute ETA is scientifically proven to be longer than an Interstellar black hole orbit da! 🚀"
    ],
    "Tamil": [
        "{name} எப்பவும் லேட்டா வரது ஒன்னும் புதுசு இல்ல... காலண்டர் கூட இவருக்காக புதுசா ஒரு கிழமை ரெடி பண்ண போகுது! 😂",
        "கிளாஸ்க்கு வர்றதை விட இவரு ரீல்ஸ் பாக்கிற நேரம் தான் அதிகம்... எக்ஸாம்ல மார்க் கேக்குறப்போ மட்டும் வைப் பண்றாரு! 💀",
        "{name} டான்ஸ் ஆடுறேன்னு சொல்லி இவரு போடுற ஸ்டெப்ஸ பாத்தா, வை-ஃபை சிக்னல் தேடுற மாதிரி இருக்கு! 🔥"
    ],
    "English": [
        "{name} has two speeds: loading... and still loading. Bro enters the room like it's a movie climax, but the only thing dramatic is the attendance percentage. 💀",
        "Your Wi-Fi has more stable connections than your life choices. You spend 5 hours gaming and still blame lag for missing every single shot. 🎮",
        "{name} treats the snooze button like an Olympic sport and the to-do list like Terms & Conditions: ignored without reading. 😂"
    ]
}

def is_demo_mode():
    return not GEMINI_API_KEY or GEMINI_API_KEY == "MY_GEMINI_API_KEY" or GEMINI_API_KEY == "your_api_key"

@app.route("/")
def index():
    return render_template("index.html", demo_mode=is_demo_mode())

@app.route("/api/health", methods=["GET"])
def health():
    return jsonify({"status": "ok"})

@app.route("/api/roast", methods=["POST"])
def generate_roast():
    try:
        data = request.get_json() or {}
        name = data.get("name", "").strip() or "Bro"
        description = data.get("description", "").strip()
        language = data.get("language", "Tanglish")
        level = data.get("level", "Savage")
        style = data.get("style", "Funny")

        if not description:
            return jsonify({"success": False, "error": "Description cannot be empty"}), 400

        if is_demo_mode():
            template_roasts = DEMO_ROASTS.get(language, DEMO_ROASTS["English"])
            roasts = [r.replace("{name}", name) for r in template_roasts]
            return jsonify({"success": True, "roasts": roasts, "demo": True})

        system_instruction = (
            "You are a professional comedy writer and world-class roastmaster specializing in playful, friendly, "
            "and wildly hilarious roasts. Analyze the target description and create 3 distinct jokes.\n"
            "Safety: Absolutely NO threats, hate speech, slurs, sexual content, serious accusations, or attacking protected characteristics. "
            "Focus exclusively on harmless everyday quirks: procrastination, bad Wi-Fi, gaming habits, being late, attendance, exam prep, sleep schedule.\n"
            "Language rules:\n"
            "- English: Sharp, witty Gen-Z burns.\n"
            "- Tamil: Relatable colloquial Tamil comedy.\n"
            "- Tanglish: Casual conversational Tamil written in English letters (Dei, Machi, scene podra, attendance, vibe).\n"
            "Format: Return exactly 3 numbered roasts:\n1. ...\n2. ...\n3. ..."
        )

        prompt = (
            f"Target Name: {name}\n"
            f"About Them: {description}\n"
            f"Roast Level: {level}\n"
            f"Language: {language}\n"
            f"Style: {style}\n\n"
            f"Generate 3 unique, hilarious roast variations."
        )

        url = f"https://generativelanguage.googleapis.com/v1beta/models/{GEMINI_TEXT_MODEL}:generateContent?key={GEMINI_API_KEY}"
        payload = {
            "contents": [{"parts": [{"text": prompt}]}],
            "systemInstruction": {"parts": [{"text": system_instruction}]},
            "generationConfig": {
                "temperature": 0.9,
                "topP": 0.95
            }
        }

        resp = requests.post(url, json=payload, timeout=40)
        if resp.status_code != 200:
            template_roasts = DEMO_ROASTS.get(language, DEMO_ROASTS["English"])
            roasts = [r.replace("{name}", name) for r in template_roasts]
            return jsonify({"success": True, "roasts": roasts, "demo": True})

        res_data = resp.json()
        text = res_data["candidates"][0]["content"]["parts"][0]["text"]

        roasts = []
        for line in text.split("\n"):
            line = line.strip()
            match = re.match(r"^\d+[\.\)]\s*(.*)", line)
            if match:
                roasts.append(match.group(1).strip())
            elif line and not roasts and not line.startswith("#"):
                roasts.append(line)

        if not roasts:
            roasts = [p.strip() for p in text.split("\n\n") if len(p.strip()) > 10]

        roasts = roasts[:3]
        if not roasts:
            template_roasts = DEMO_ROASTS.get(language, DEMO_ROASTS["English"])
            roasts = [r.replace("{name}", name) for r in template_roasts]

        return jsonify({"success": True, "roasts": roasts, "demo": False})

    except Exception as e:
        template_roasts = DEMO_ROASTS.get("English")
        roasts = [r.replace("{name}", "Friend") for r in template_roasts]
        return jsonify({"success": True, "roasts": roasts, "demo": True})

@app.route("/api/live-roast", methods=["POST"])
def live_roast():
    try:
        data = request.get_json() or {}
        message = data.get("message", "").strip()
        language = data.get("language", "Tanglish")
        history = data.get("history", [])

        if not message:
            return jsonify({"success": False, "error": "Message cannot be empty"}), 400

        if is_demo_mode():
            reply = f"Dei machi, un pecha kettale theriyudhu... un Wi-Fi router kooda unna vida punctual da! 😂 (Live demo with {GEMINI_LIVE_MODEL})"
            return jsonify({"success": True, "reply": reply, "model": GEMINI_LIVE_MODEL, "demo": True})

        system_instruction = (
            f"You are a quick-witted, hilarious AI Roastmaster in a LIVE verbal roast battle using {GEMINI_LIVE_MODEL}!\n"
            "Rules:\n"
            "- Deliver punchy, direct comebacks (1-2 sentences) suited for spoken voice playback.\n"
            "- Match the language: English, colloquial Tamil, or casual Tanglish (Dei, Machi, scene podra, vibe).\n"
            "- Strict safety: harmless quirks only (sleep schedule, bugs, being late, bad Wi-Fi, procrastination).\n"
        )

        contents = []
        for turn in history[-6:]:
            role = "user" if turn.get("sender") == "user" else "model"
            contents.append({"role": role, "parts": [{"text": turn.get("text", "")}]})
        contents.append({"role": "user", "parts": [{"text": message}]})

        url = f"https://generativelanguage.googleapis.com/v1beta/models/{GEMINI_LIVE_MODEL}:generateContent?key={GEMINI_API_KEY}"
        payload = {
            "contents": contents,
            "systemInstruction": {"parts": [{"text": system_instruction}]},
            "generationConfig": {"temperature": 0.95, "maxOutputTokens": 160}
        }

        resp = requests.post(url, json=payload, timeout=20)
        if resp.status_code != 200:
            url_fallback = f"https://generativelanguage.googleapis.com/v1beta/models/{GEMINI_TEXT_MODEL}:generateContent?key={GEMINI_API_KEY}"
            resp = requests.post(url_fallback, json=payload, timeout=20)

        if resp.status_code == 200:
            res_json = resp.json()
            reply = res_json["candidates"][0]["content"]["parts"][0]["text"].strip()
            return jsonify({"success": True, "reply": reply, "model": GEMINI_LIVE_MODEL, "demo": False})
        else:
            return jsonify({"success": True, "reply": "Dei, un logic paatha NASA scientist-ke vertigo vandhurum da! 😂", "model": GEMINI_LIVE_MODEL, "demo": True})

    except Exception:
        return jsonify({"success": True, "reply": "You speak with 100% confidence but 1% battery logic! 😂", "model": GEMINI_LIVE_MODEL, "demo": True})

if __name__ == "__main__":
    app.run(host="127.0.0.1", port=5000, debug=True)
