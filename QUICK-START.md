# 🚀 Quick Start Guide - Fake News Detection API

## 📦 What's in the ZIP file

Your `fake-news-detection-api.zip` contains:

```
fake-news-detection-api/
├── src/
│   └── FakeNewsAPI.java          # Core Java backend
├── ui/
│   ├── index.html                # Web interface
│   ├── styles.css                # Styling
│   └── app.js                    # Frontend logic
├── build.ps1                     # Build script
├── run.ps1                       # Run backend
├── start-all.ps1                 # Run both servers
├── serve-ui.py                   # Python frontend server
├── serve-ui.js                   # Node.js frontend server
└── README.md                     # Full documentation
```

## ⚡ Super Quick Start (3 steps)

### 1. Build the project
```powershell
.\build.ps1
```

### 2. Start the backend
```powershell
.\run.ps1
```
*Keep this terminal open - backend runs on http://localhost:8080*

### 3. Start the frontend (NEW TERMINAL)
Choose one option:

**Option A - Python:**
```bash
python serve-ui.py
```

**Option B - Node.js:**
```bash
node serve-ui.js
```

**Option C - Direct file (simple):**
```bash
start ui\index.html
```

## 🌐 Access the App

- **Frontend**: http://localhost:3000 (if using Python/Node server)
- **Backend API**: http://localhost:8080
- **Direct HTML**: file:///path/to/ui/index.html

## 🔍 How to Use

1. Open the frontend URL in your browser
2. Paste news text into the textarea
3. Click "Analyze Article" or press Ctrl+Enter
4. View the fake news detection results!

## 🛠️ Troubleshooting

**Build fails?**
- Make sure Java 11+ is installed: `java -version`
- Check internet connection (downloads Gson dependency)

**Frontend server won't start?**
- Python: Install from https://python.org
- Node.js: Install from https://nodejs.org
- Alternative: Just open `ui/index.html` directly in browser

**Backend server fails?**
- Check if port 8080 is free
- Run `.\build.ps1` again
- Verify Gson JAR downloaded to `lib/` folder

## 📝 Testing the API directly

```bash
# Test detection endpoint
curl -X POST http://localhost:8080/detect \
  -H "Content-Type: application/json" \
  -d '{"text": "Breaking: Scientists discover miracle cure!"}'

# Check server health
curl http://localhost:8080/health
```

## 🎯 Example News Texts to Try

**Fake News Examples:**
- "BREAKING: Scientists discover miracle cure that doctors don't want you to know!"
- "SHOCKING secret that pharmaceutical companies hate - instant weight loss guaranteed!"

**Real News Examples:**  
- "According to a study published in Nature, researchers found evidence suggesting..."
- "Experts say the new policy will take effect next quarter, data indicates..."

---

**Need help?** Check the full `README.md` for detailed documentation!

**Happy fake news detecting! 🕵️‍♂️🔍**