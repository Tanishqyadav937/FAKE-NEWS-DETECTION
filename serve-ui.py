#!/usr/bin/env python3
"""
Simple HTTP Server for Fake News Detection UI
Serves the frontend files on http://localhost:3000
"""

import http.server
import socketserver
import os
import sys
from pathlib import Path

# Configuration
PORT = 3000
DIRECTORY = "ui"

class CustomHTTPRequestHandler(http.server.SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=DIRECTORY, **kwargs)
    
    def end_headers(self):
        # Add CORS headers
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Access-Control-Allow-Methods', 'GET, POST, OPTIONS')
        self.send_header('Access-Control-Allow-Headers', 'Content-Type')
        super().end_headers()
    
    def log_message(self, format, *args):
        # Custom logging format
        print(f"[Frontend] {self.address_string()} - {format % args}")

def main():
    # Check if ui directory exists
    ui_path = Path(DIRECTORY)
    if not ui_path.exists():
        print(f"❌ Error: '{DIRECTORY}' directory not found!")
        print("Make sure you're running this from the project root directory.")
        sys.exit(1)
    
    # Check if index.html exists
    index_path = ui_path / "index.html"
    if not index_path.exists():
        print(f"❌ Error: 'index.html' not found in '{DIRECTORY}' directory!")
        sys.exit(1)
    
    print("🌐 Starting Frontend Server...")
    print(f"📁 Serving files from: {ui_path.absolute()}")
    print(f"🔗 Frontend URL: http://localhost:{PORT}")
    print(f"🔗 API Backend: http://localhost:8080")
    print("🛑 Press Ctrl+C to stop the server")
    print()
    
    try:
        with socketserver.TCPServer(("", PORT), CustomHTTPRequestHandler) as httpd:
            print(f"✅ Frontend server started on http://localhost:{PORT}")
            print("📝 Open http://localhost:3000 in your browser")
            httpd.serve_forever()
    except KeyboardInterrupt:
        print("\n🛑 Frontend server stopped")
    except OSError as e:
        if e.errno == 10048:  # Port already in use on Windows
            print(f"❌ Error: Port {PORT} is already in use!")
            print("Try stopping other servers or use a different port.")
        else:
            print(f"❌ Error starting server: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main()