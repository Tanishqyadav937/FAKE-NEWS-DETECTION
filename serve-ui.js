#!/usr/bin/env node

/**
 * Simple Node.js HTTP Server for Fake News Detection UI
 * Alternative to Python server - serves frontend on http://localhost:3000
 */

const http = require('http');
const fs = require('fs');
const path = require('path');
const url = require('url');

// Configuration
const PORT = process.env.PORT || 3000;
const UI_DIR = 'ui';

// MIME types for different file extensions
const MIME_TYPES = {
    '.html': 'text/html',
    '.css': 'text/css',
    '.js': 'application/javascript',
    '.json': 'application/json',
    '.png': 'image/png',
    '.jpg': 'image/jpeg',
    '.gif': 'image/gif',
    '.svg': 'image/svg+xml',
    '.ico': 'image/x-icon'
};

function getMimeType(filePath) {
    const ext = path.extname(filePath).toLowerCase();
    return MIME_TYPES[ext] || 'text/plain';
}

function serveFile(res, filePath) {
    fs.readFile(filePath, (err, data) => {
        if (err) {
            res.writeHead(404, { 'Content-Type': 'text/plain' });
            res.end('404 - File Not Found');
            return;
        }

        const mimeType = getMimeType(filePath);
        res.writeHead(200, {
            'Content-Type': mimeType,
            'Access-Control-Allow-Origin': '*',
            'Access-Control-Allow-Methods': 'GET, POST, OPTIONS',
            'Access-Control-Allow-Headers': 'Content-Type'
        });
        res.end(data);
    });
}

function handleRequest(req, res) {
    const parsedUrl = url.parse(req.url, true);
    let pathname = parsedUrl.pathname;

    // Default to index.html for root path
    if (pathname === '/') {
        pathname = '/index.html';
    }

    // Remove leading slash and resolve file path
    const filePath = path.join(UI_DIR, pathname.substring(1));
    
    // Security check - prevent directory traversal
    const resolvedPath = path.resolve(filePath);
    const resolvedUIDir = path.resolve(UI_DIR);
    
    if (!resolvedPath.startsWith(resolvedUIDir)) {
        res.writeHead(403, { 'Content-Type': 'text/plain' });
        res.end('403 - Forbidden');
        return;
    }

    // Log the request
    const timestamp = new Date().toLocaleTimeString();
    console.log(`[${timestamp}] ${req.method} ${req.url} -> ${filePath}`);

    // Check if file exists
    fs.stat(resolvedPath, (err, stats) => {
        if (err) {
            res.writeHead(404, { 'Content-Type': 'text/plain' });
            res.end('404 - File Not Found');
            return;
        }

        if (stats.isFile()) {
            serveFile(res, resolvedPath);
        } else {
            res.writeHead(404, { 'Content-Type': 'text/plain' });
            res.end('404 - Not a file');
        }
    });
}

function main() {
    // Check if UI directory exists
    if (!fs.existsSync(UI_DIR)) {
        console.error(`❌ Error: '${UI_DIR}' directory not found!`);
        console.error('Make sure you\'re running this from the project root directory.');
        process.exit(1);
    }

    // Check if index.html exists
    const indexPath = path.join(UI_DIR, 'index.html');
    if (!fs.existsSync(indexPath)) {
        console.error(`❌ Error: 'index.html' not found in '${UI_DIR}' directory!`);
        process.exit(1);
    }

    console.log('🌐 Starting Frontend Server...');
    console.log(`📁 Serving files from: ${path.resolve(UI_DIR)}`);
    console.log(`🔗 Frontend URL: http://localhost:${PORT}`);
    console.log(`🔗 API Backend: http://localhost:8080`);
    console.log('🛑 Press Ctrl+C to stop the server');
    console.log('');

    // Create HTTP server
    const server = http.createServer(handleRequest);

    // Handle server errors
    server.on('error', (err) => {
        if (err.code === 'EADDRINUSE') {
            console.error(`❌ Error: Port ${PORT} is already in use!`);
            console.error('Try stopping other servers or use a different port.');
        } else {
            console.error(`❌ Error starting server: ${err.message}`);
        }
        process.exit(1);
    });

    // Start the server
    server.listen(PORT, () => {
        console.log(`✅ Frontend server started on http://localhost:${PORT}`);
        console.log(`📝 Open http://localhost:${PORT} in your browser`);
    });

    // Handle graceful shutdown
    process.on('SIGINT', () => {
        console.log('\n🛑 Frontend server stopped');
        server.close(() => {
            process.exit(0);
        });
    });

    process.on('SIGTERM', () => {
        console.log('\n🛑 Frontend server stopped');
        server.close(() => {
            process.exit(0);
        });
    });
}

if (require.main === module) {
    main();
}