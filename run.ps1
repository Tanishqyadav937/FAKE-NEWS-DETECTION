# Fake News Detection API - Run Script
# This script starts the Java HTTP server

$ErrorActionPreference = "Stop"

Write-Host "🚀 Starting Fake News Detection API Server..." -ForegroundColor Green

# Check if classes directory exists
if (-not (Test-Path "classes")) {
    Write-Host "❌ Classes directory not found. Please run .\build.ps1 first" -ForegroundColor Red
    exit 1
}

# Check if Gson jar exists
if (-not (Test-Path "lib\gson-2.10.1.jar")) {
    Write-Host "❌ Gson dependency not found. Please run .\build.ps1 first" -ForegroundColor Red
    exit 1
}

# Start the server
Write-Host "🔥 Launching server on http://localhost:8080" -ForegroundColor Yellow
Write-Host "📝 Check the UI at: ui/index.html (open in browser)" -ForegroundColor Cyan
Write-Host "🛑 Press Ctrl+C to stop the server" -ForegroundColor Yellow
Write-Host ""

try {
    java -cp "classes;lib\*" FakeNewsAPI
} catch {
    Write-Host "❌ Failed to start server: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}