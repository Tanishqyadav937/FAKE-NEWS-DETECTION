# Fake News Detection API - Build Script
# This script downloads the Gson dependency and compiles the Java source

$ErrorActionPreference = "Stop"

Write-Host "🔧 Building Fake News Detection API..." -ForegroundColor Green

# Create lib directory for dependencies
if (-not (Test-Path "lib")) {
    New-Item -ItemType Directory -Path "lib"
    Write-Host "📁 Created lib directory"
}

# Create compiled classes directory
if (-not (Test-Path "classes")) {
    New-Item -ItemType Directory -Path "classes"
    Write-Host "📁 Created classes directory"
}

# Download Gson dependency if not present
$gsonJar = "lib\gson-2.10.1.jar"
if (-not (Test-Path $gsonJar)) {
    Write-Host "📦 Downloading Gson dependency..."
    $gsonUrl = "https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar"
    
    try {
        Invoke-WebRequest -Uri $gsonUrl -OutFile $gsonJar
        Write-Host "✅ Gson downloaded successfully"
    } catch {
        Write-Host "❌ Failed to download Gson: $($_.Exception.Message)" -ForegroundColor Red
        Write-Host "💡 Please manually download gson-2.10.1.jar to the lib/ directory" -ForegroundColor Yellow
        exit 1
    }
}

# Compile Java source
Write-Host "🔨 Compiling Java source..."
try {
    javac -cp "lib\*" -d classes src\FakeNewsAPI.java
    Write-Host "✅ Compilation successful!"
} catch {
    Write-Host "❌ Compilation failed: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "🎉 Build completed successfully!" -ForegroundColor Green
Write-Host "🚀 To run the server: .\run.ps1" -ForegroundColor Cyan