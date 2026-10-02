document.addEventListener('DOMContentLoaded', () => {
  // Elements
  const roastForm = document.getElementById('roast-form');
  const inputCard = document.getElementById('input-card');
  const loaderCard = document.getElementById('loader-card');
  const resultCard = document.getElementById('result-card');
  const loaderStatus = document.getElementById('loader-status');

  const targetNameInput = document.getElementById('target-name');
  const targetDescInput = document.getElementById('target-desc');
  const roastLangSelect = document.getElementById('roast-lang');
  const roastStyleSelect = document.getElementById('roast-style');

  const resultTitle = document.getElementById('result-title');
  const roastTextEl = document.getElementById('roast-text');
  const variationCounter = document.getElementById('variation-counter');
  const tagLevel = document.getElementById('tag-level');
  const tagLang = document.getElementById('tag-lang');
  const tagStyle = document.getElementById('tag-style');
  const variationChipsContainer = document.getElementById('variation-chips');

  const btnRandom = document.getElementById('btn-random');
  const btnCopy = document.getElementById('btn-copy');
  const btnShare = document.getElementById('btn-share');
  const btnSave = document.getElementById('btn-save');
  const btnAgain = document.getElementById('btn-again');
  const btnGenerateMore = document.getElementById('btn-generate-more');
  const btnClearHistory = document.getElementById('btn-clear-history');
  const toast = document.getElementById('toast');

  let currentRoasts = [];
  let currentRoastIndex = 0;
  let currentTargetName = '';

  const PRESETS = [
    { name: "Harish", desc: "Always late to class, thinks he's a great dancer, spends 5 hours gaming", lang: "Tanglish", style: "College Friend" },
    { name: "My Sleep Schedule", desc: "Sleeps at 4 AM, wakes up at 1 PM with a headache, wonders why feeling tired all day", lang: "English", style: "Meme" },
    { name: "Vignesh", desc: "எப்போதும் லேட்டாக வருவான், ஆனா ஹீரோ மாதிரி ஸ்லோ மோஷன்ல வர்றதா நெனப்பு", lang: "Tamil", style: "Funny" },
    { name: "My Attendance", desc: "Currently at 43%, calculating how many fake medical certificates needed to sit for exam", lang: "Tanglish", style: "College Friend" },
    { name: "My Wi-Fi Router", desc: "Works seamlessly for 4K ads, dies instantly when attending online viva or exam", lang: "English", style: "Sarcastic" }
  ];

  // Random preset handler
  btnRandom.addEventListener('click', () => {
    const item = PRESETS[Math.floor(Math.random() * PRESETS.length)];
    targetNameInput.value = item.name;
    targetDescInput.value = item.desc;
    roastLangSelect.value = item.lang;
    roastStyleSelect.value = item.style;
  });

  // Level card selection
  document.querySelectorAll('.level-card').forEach(card => {
    card.addEventListener('click', () => {
      document.querySelectorAll('.level-card').forEach(c => c.classList.remove('active'));
      card.classList.add('active');
    });
  });

  // Tab switching
  document.querySelectorAll('.nav-btn').forEach(btn => {
    btn.addEventListener('click', () => {
      document.querySelectorAll('.nav-btn').forEach(b => b.classList.remove('active'));
      document.querySelectorAll('.tab-content').forEach(t => t.classList.remove('active'));
      btn.classList.add('active');
      const tabId = btn.getAttribute('data-tab');
      document.getElementById(tabId).classList.add('active');
      if (tabId === 'history-tab') {
        renderHistory();
      }
    });
  });

  function showToast(msg) {
    toast.textContent = msg;
    toast.classList.add('show');
    setTimeout(() => toast.classList.remove('show'), 2400);
  }

  // Generate roast API call
  async function triggerRoast() {
    const name = targetNameInput.value.trim() || 'Bro';
    const desc = targetDescInput.value.trim();
    if (!desc) {
      showToast("Please enter a description! 😂");
      return;
    }

    const selectedLevelEl = document.querySelector('input[name="level"]:checked');
    const level = selectedLevelEl ? selectedLevelEl.value : 'Savage';
    const language = roastLangSelect.value;
    const style = roastStyleSelect.value;

    inputCard.style.display = 'none';
    resultCard.style.display = 'none';
    loaderCard.style.display = 'block';

    const steps = [
      "🔥 Lighting the comedy barbecue...",
      "🌶️ Adding extra spice and comedic seasoning...",
      "🧠 Analyzing procrastination metrics...",
      "😂 Finalizing burns..."
    ];
    let stepIdx = 0;
    const interval = setInterval(() => {
      stepIdx = (stepIdx + 1) % steps.length;
      loaderStatus.textContent = steps[stepIdx];
    }, 400);

    try {
      const resp = await fetch('/api/roast', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name, description: desc, language, level, style })
      });
      clearInterval(interval);
      const data = await resp.json();

      loaderCard.style.display = 'none';

      if (data.success && data.roasts && data.roasts.length > 0) {
        currentRoasts = data.roasts;
        currentRoastIndex = 0;
        currentTargetName = name;

        resultTitle.textContent = `😂 ${name.toUpperCase()} GETS ROASTED`;
        tagLevel.textContent = level;
        tagLang.textContent = language;
        tagStyle.textContent = style;

        renderCurrentRoast();
        resultCard.style.display = 'block';

        saveToHistory(name, currentRoasts[0], language, level);
      } else {
        inputCard.style.display = 'block';
        showToast("Oops! The roast kitchen got too spicy. Try again!");
      }
    } catch (err) {
      clearInterval(interval);
      loaderCard.style.display = 'none';
      inputCard.style.display = 'block';
      showToast("Network error. Try again!");
    }
  }

  function renderCurrentRoast() {
    roastTextEl.textContent = currentRoasts[currentRoastIndex];
    variationCounter.textContent = `${currentRoastIndex + 1}/${currentRoasts.length}`;

    // Update chips
    const chipBtns = variationChipsContainer.querySelectorAll('.chip-btn');
    chipBtns.forEach((btn, idx) => {
      btn.classList.toggle('active', idx === currentRoastIndex);
    });
  }

  roastForm.addEventListener('submit', (e) => {
    e.preventDefault();
    triggerRoast();
  });

  btnAgain.addEventListener('click', () => {
    resultCard.style.display = 'none';
    inputCard.style.display = 'block';
  });

  btnGenerateMore.addEventListener('click', () => {
    triggerRoast();
  });

  // Switch variation
  variationChipsContainer.querySelectorAll('.chip-btn').forEach(btn => {
    btn.addEventListener('click', () => {
      const idx = parseInt(btn.getAttribute('data-index'), 10);
      if (idx < currentRoasts.length) {
        currentRoastIndex = idx;
        renderCurrentRoast();
      }
    });
  });

  // Copy
  btnCopy.addEventListener('click', () => {
    const text = currentRoasts[currentRoastIndex];
    navigator.clipboard.writeText(text).then(() => {
      showToast("Roast copied! 😂");
    });
  });

  // Share
  btnShare.addEventListener('click', () => {
    const text = `😂 AI Roast of ${currentTargetName}:\n\n"${currentRoasts[currentRoastIndex]}"\n\n— Generated by AI Roast Generator 🔥`;
    if (navigator.share) {
      navigator.share({ title: 'AI Roast', text });
    } else {
      navigator.clipboard.writeText(text).then(() => {
        showToast("Share text copied! 📤");
      });
    }
  });

  // Save favorite
  btnSave.addEventListener('click', () => {
    saveToHistory(currentTargetName, currentRoasts[currentRoastIndex], roastLangSelect.value, 'Saved');
    showToast("Roast saved to History! ❤️");
  });

  // LocalStorage History
  function getHistory() {
    try {
      return JSON.parse(localStorage.getItem('roast_history') || '[]');
    } catch {
      return [];
    }
  }

  function saveToHistory(name, text, lang, level) {
    const history = getHistory();
    history.unshift({
      id: Date.now(),
      name,
      text,
      lang,
      level,
      date: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    });
    localStorage.setItem('roast_history', JSON.stringify(history.slice(0, 30)));
  }

  function renderHistory() {
    const list = document.getElementById('history-list');
    const history = getHistory();
    if (history.length === 0) {
      list.innerHTML = '<p style="text-align: center; color: var(--text-muted); padding: 24px;">No roasts generated yet. Go cook some! 😂</p>';
      return;
    }

    list.innerHTML = history.map(item => `
      <div class="history-item">
        <div style="display:flex; justify-content:space-between; margin-bottom: 6px;">
          <strong>😂 ${item.name}</strong>
          <span style="font-size: 0.75rem; color: var(--text-muted);">${item.date}</span>
        </div>
        <div style="display:flex; gap: 6px; margin-bottom: 8px;">
          <span class="tag tag-level">${item.level}</span>
          <span class="tag tag-lang">${item.lang}</span>
        </div>
        <p style="font-size: 0.95rem; margin-bottom: 10px;">${item.text}</p>
        <button class="action-btn" onclick="navigator.clipboard.writeText('${item.text.replace(/'/g, "\\'")}'); alert('Copied!');">📋 Copy</button>
      </div>
    `).join('');
  }

  btnClearHistory.addEventListener('click', () => {
    localStorage.removeItem('roast_history');
    renderHistory();
    showToast("History cleared!");
  });
});
