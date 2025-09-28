# Start All Servers - Fake News Detection API
# This script starts both the Java backend and Python frontend server

param(
    [switch]$SkipBuild,
    [int]$BackendPort = 8080,
    [int]$FrontendPort = 3000
)

$ErrorActionPreference = "Continue"

Write-Host "🚀 Starting Fake News Detection Full Stack..." -ForegroundColor Green
Write-Host ""

# Build the project if not skipping
if (-not $SkipBuild) {
    Write-Host "🔧 Building Java backend..." -ForegroundColor Yellow
    try {
        & .\build.ps1
        if ($LASTEXITCODE -ne 0) {
            Write-Host "❌ Build failed. Exiting." -ForegroundColor Red
            exit 1
        }
    } catch {
        Write-Host "❌ Build failed: $($_.Exception.Message)" -ForegroundColor Red
        exit 1
    }
    Write-Host ""
}

# Check if Python is available
try {
    $pythonVersion = python --version 2>&1
    if ($LASTEXITCODE -ne 0) {
        throw "Python not found"
    }
    Write-Host "🐍 Python detected: $pythonVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ Python is required for the frontend server" -ForegroundColor Red
    Write-Host "💡 Please install Python from https://python.org" -ForegroundColor Yellow
    Write-Host "💡 Alternative: Open ui\index.html directly in your browser" -ForegroundColor Yellow
    exit 1
}

Write-Host ""
Write-Host "🎯 Starting servers..." -ForegroundColor Cyan
Write-Host "📊 Backend (Java): http://localhost:$BackendPort" -ForegroundColor White
Write-Host "🌐 Frontend (Python): http://localhost:$FrontendPort" -ForegroundColor White
Write-Host ""
Write-Host "🛑 Press Ctrl+C to stop all servers" -ForegroundColor Yellow
Write-Host ""

# Function to clean up processes
function Stop-Servers {
    Write-Host "`n🛑 Stopping servers..." -ForegroundColor Yellow
    
    # Stop background jobs
    Get-Job | Stop-Job -Force
    Get-Job | Remove-Job -Force
    
    # Kill any remaining processes on our ports
    try {
        $backendProcess = Get-NetTCPConnection -LocalPort $BackendPort -ErrorAction SilentlyContinue
        if ($backendProcess) {
            Stop-Process -Id $backendProcess.OwningProcess -Force -ErrorAction SilentlyContinue
        }
        
        $frontendProcess = Get-NetTCPConnection -LocalPort $FrontendPort -ErrorAction SilentlyContinue
        if ($frontendProcess) {
            Stop-Process -Id $frontendProcess.OwningProcess -Force -ErrorAction SilentlyContinue
        }
    } catch {
        # Ignore errors during cleanup
    }
    
    Write-Host "✅ All servers stopped" -ForegroundColor Green
}

# Set up cleanup on exit
trap {
    Stop-Servers
    exit
}

try {
    # Start Java backend server in background
    Write-Host "[Backend] Starting Java API server..." -ForegroundColor Blue
    $backendJob = Start-Job -ScriptBlock {
        param($port)
        Set-Location $using:PWD
        java -cp "classes;lib\*" FakeNewsAPI
    } -ArgumentList $BackendPort
    
    # Wait a moment for backend to start
    Start-Sleep -Seconds 2
    
    # Check if backend started successfully
    $backendState = Get-Job -Id $backendJob.Id
    if ($backendState.State -eq "Failed") {
        Write-Host "❌ Failed to start Java backend server" -ForegroundColor Red
        $backendJob | Receive-Job
        exit 1
    }
    
    Write-Host "[Backend] ✅ Java server started (Job ID: $($backendJob.Id))" -ForegroundColor Blue
    
    # Start Python frontend server in background
    Write-Host "[Frontend] Starting Python web server..." -ForegroundColor Magenta
    $frontendJob = Start-Job -ScriptBlock {
        param($port)
        Set-Location $using:PWD
        python serve-ui.py
    } -ArgumentList $FrontendPort
    
    # Wait a moment for frontend to start
    Start-Sleep -Seconds 2
    
    # Check if frontend started successfully
    $frontendState = Get-Job -Id $frontendJob.Id
    if ($frontendState.State -eq "Failed") {
        Write-Host "❌ Failed to start Python frontend server" -ForegroundColor Red
        $frontendJob | Receive-Job
        Stop-Job $backendJob -Force
        Remove-Job $backendJob -Force
        exit 1
    }
    
    Write-Host "[Frontend] ✅ Python server started (Job ID: $($frontendJob.Id))" -ForegroundColor Magenta
    Write-Host ""
    
    # Display status
    Write-Host "🎉 All servers are running!" -ForegroundColor Green
    Write-Host ""
    Write-Host "🌍 URLs:" -ForegroundColor Cyan
    Write-Host "  Frontend: http://localhost:$FrontendPort" -ForegroundColor White
    Write-Host "  Backend:  http://localhost:$BackendPort" -ForegroundColor White
    Write-Host ""
    Write-Host "📝 Open http://localhost:$FrontendPort in your browser to use the app" -ForegroundColor Yellow
    Write-Host ""
    
    # Open browser automatically
    try {
        Start-Process "http://localhost:$FrontendPort"
        Write-Host "🌐 Opening browser..." -ForegroundColor Green
    } catch {
        Write-Host "💡 Manually open http://localhost:$FrontendPort in your browser" -ForegroundColor Yellow
    }
    
    Write-Host ""
    Write-Host "📊 Server Status:" -ForegroundColor Cyan
    
    # Monitor servers
    while ($true) {
        Start-Sleep -Seconds 5
        
        $backendState = Get-Job -Id $backendJob.Id
        $frontendState = Get-Job -Id $frontendJob.Id
        
        $timestamp = Get-Date -Format "HH:mm:ss"
        Write-Host "[$timestamp] Backend: $($backendState.State) | Frontend: $($frontendState.State)" -ForegroundColor Gray
        
        # Check if any server failed
        if ($backendState.State -eq "Failed") {
            Write-Host "❌ Backend server failed!" -ForegroundColor Red
            $backendJob | Receive-Job
            break
        }
        
        if ($frontendState.State -eq "Failed") {
            Write-Host "❌ Frontend server failed!" -ForegroundColor Red  
            $frontendJob | Receive-Job
            break
        }
    }
    
} catch {
    Write-Host "❌ Error: $($_.Exception.Message)" -ForegroundColor Red
} finally {
    Stop-Servers
}