# 🔍 Fake News Detection API

A **Core Java** fake news detection system with a clean, responsive web interface. This project demonstrates a complete end-to-end solution using pure Java HTTP server, JSON processing, and modern frontend technologies.

![Java](https://img.shields.io/badge/Java-17+-orange.svg)
![Frontend](https://img.shields.io/badge/Frontend-Vanilla%20JS-yellow.svg)
![API](https://img.shields.io/badge/API-REST-blue.svg)
![License](https://img.shields.io/badge/License-MIT-green.svg)

## 🚀 Features

- **Pure Core Java Backend** - No Spring Framework, just built-in `HttpServer`
- **RESTful API** - Clean JSON-based endpoints
- **Modern Web UI** - Responsive, accessible interface
- **Real-time Analysis** - Instant feedback with loading states
- **Heuristic Detection** - Pattern-based fake news indicators
- **Health Monitoring** - API status checking
- **Error Handling** - Comprehensive error states
- **CORS Support** - Ready for cross-origin requests

## 📋 API Endpoints

### `POST /detect`
Analyze news text for authenticity.

**Request:**
```json
{
  "text": "Your news article content here..."
}
```

**Response:**
```json
{
  "prediction": "Fake",
  "confidence": 0.87,
  "analysis": "Text contains indicators commonly found in fake news articles",
  "textLength": 247,
  "timestamp": "Thu Sep 26 15:30:45 UTC 2024"
}
```

### `GET /health`
Check API server health status.

**Response:**
```json
{
  "status": "healthy",
  "timestamp": "Thu Sep 26 15:30:45 UTC 2024",
  "service": "Fake News Detection API"
}
```

## 🏗️ Project Structure

```
java.project/
├── src/
│   └── FakeNewsAPI.java      # Main server implementation
├── ui/
│   ├── index.html            # Web interface
│   ├── styles.css            # UI styling
│   └── app.js               # Frontend logic
├── lib/                      # Dependencies (auto-created)
├── classes/                  # Compiled Java classes (auto-created)
├── build.ps1                # Build script
├── run.ps1                  # Run script
└── README.md                # This file
```

## 🔧 Prerequisites

- **Java 11+** (Java 17+ recommended)
- **PowerShell** (for Windows scripts)
- **Modern web browser** (Chrome, Firefox, Safari, Edge)
- **Internet connection** (for downloading Gson dependency)

## ⚡ Quick Start

### 1. Build the Project
```powershell
# Download dependencies and compile Java source
.\build.ps1
```

### 2. Start the Server
```powershell
# Launch the API server
.\run.ps1
```

### 3. Open the Web Interface
```bash
# Open in your default browser
start ui\index.html

# Or manually navigate to:
# file:///C:/Users/TANISHQ/OneDrive/Desktop/java.project/ui/index.html
```

## 🖥️ Manual Setup (Alternative)

If PowerShell scripts don't work, use these manual commands:

### Build Manually
```bash
# Create directories
mkdir lib classes

# Download Gson (or download manually from Maven Central)
curl -o lib/gson-2.10.1.jar https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar

# Compile Java source
javac -cp "lib/*" -d classes src/FakeNewsAPI.java
```

### Run Manually
```bash
# Start the server
java -cp "classes;lib/*" FakeNewsAPI
```

## 🌐 Usage

### Web Interface
1. **Start the server** using `.\run.ps1`
2. **Open** `ui/index.html` in your browser
3. **Paste news text** into the textarea
4. **Click "Analyze Article"** or press `Ctrl+Enter`
5. **View results** with prediction, confidence, and analysis

### API Usage
```bash
# Test with curl
curl -X POST http://localhost:8080/detect \
  -H "Content-Type: application/json" \
  -d '{"text": "Breaking news! Scientists discover miracle cure that doctors hate!"}'

# Check health
curl http://localhost:8080/health
```

## 🔍 How It Works

### Backend (Java)
- **HTTP Server**: Uses Java's built-in `com.sun.net.httpserver.HttpServer`
- **JSON Processing**: Gson library for serialization/deserialization
- **Detection Logic**: Heuristic-based analysis using keyword indicators
- **CORS Support**: Headers for cross-origin requests
- **Error Handling**: Comprehensive error responses

### Frontend (JavaScript)
- **Vanilla JS**: No frameworks, pure JavaScript
- **Responsive Design**: CSS Grid and Flexbox layout
- **State Management**: Loading, success, and error states
- **API Integration**: Fetch API for HTTP requests
- **UX Features**: Character counting, keyboard shortcuts, animations

### Detection Algorithm
The current implementation uses a simple heuristic approach:

**Fake News Indicators:**
- "breaking:", "shocking", "you won't believe"
- "doctors hate", "secret", "conspiracy"
- "miracle cure", "instant", "guaranteed"

**Real News Indicators:**
- "according to", "research shows", "study found"
- "expert says", "data indicates", "report states"

> **Note:** This is a demonstration implementation. In production, you would integrate a trained ML model using libraries like DL4J, Weka, or JPMML.

## 🚀 Enhancement Ideas

### Backend Enhancements
- [ ] **ML Integration**: Replace heuristics with trained models
- [ ] **Database Storage**: Store articles and predictions
- [ ] **Authentication**: Add API key authentication
- [ ] **Rate Limiting**: Implement request rate limiting
- [ ] **Logging**: Add comprehensive logging

### Frontend Enhancements
- [ ] **History**: Store analysis history locally
- [ ] **Export**: Download results as PDF/CSV
- [ ] **Bulk Analysis**: Upload and analyze multiple articles
- [ ] **Dark Mode**: Add theme switching
- [ ] **PWA**: Make it a Progressive Web App

---

**Built with ❤️ using Core Java and Vanilla JavaScript**

*Happy coding! 🚀*
