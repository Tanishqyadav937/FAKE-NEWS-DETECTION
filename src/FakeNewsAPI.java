import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.google.gson.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.*;

/**
 * Fake News Detection API - Core Java Implementation
 * 
 * This is a simple HTTP server that provides a /detect endpoint
 * for fake news detection. Currently returns stub/random predictions.
 */
public class FakeNewsAPI {
    
    private static final int PORT = 8080;
    private static final Gson gson = new Gson();
    
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        
        // API endpoints
        server.createContext("/detect", new DetectHandler());
        server.createContext("/health", new HealthHandler());
        
        // Enable CORS for all endpoints
        server.createContext("/", new CorsHandler());
        
        server.setExecutor(null); // Use default executor
        
        System.out.println("🚀 Fake News Detection API Server started!");
        System.out.println("📍 Server running on http://localhost:" + PORT);
        System.out.println("🔍 Detect endpoint: POST /detect");
        System.out.println("❤️  Health check: GET /health");
        System.out.println("🛑 Press Ctrl+C to stop the server");
        
        server.start();
    }

    /**
     * Main detection endpoint handler
     * POST /detect
     * Input: {"text": "news article content"}
     * Output: {"prediction": "Fake|Real", "confidence": 0.0-1.0, "analysis": "..."}
     */
    static class DetectHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Add CORS headers
            addCorsHeaders(exchange);
            
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }
            
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    // Read and parse request body
                    String requestBody = readRequestBody(exchange);
                    JsonObject inputJson = JsonParser.parseString(requestBody).getAsJsonObject();
                    
                    if (!inputJson.has("text")) {
                        sendErrorResponse(exchange, 400, "Missing 'text' field in request body");
                        return;
                    }
                    
                    String newsText = inputJson.get("text").getAsString().trim();
                    
                    if (newsText.isEmpty()) {
                        sendErrorResponse(exchange, 400, "Text field cannot be empty");
                        return;
                    }
                    
                    // Perform fake news detection (stub implementation)
                    DetectionResult result = performDetection(newsText);
                    
                    // Send success response
                    String response = gson.toJson(result);
                    exchange.getResponseHeaders().add("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, response.length());
                    
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes("UTF-8"));
                    }
                    
                    // Log the request
                    System.out.println("✅ Detection request processed: " + 
                        newsText.substring(0, Math.min(50, newsText.length())) + "...");
                    
                } catch (JsonSyntaxException e) {
                    sendErrorResponse(exchange, 400, "Invalid JSON format");
                } catch (Exception e) {
                    System.err.println("❌ Error processing request: " + e.getMessage());
                    sendErrorResponse(exchange, 500, "Internal server error");
                }
            } else {
                sendErrorResponse(exchange, 405, "Method not allowed. Use POST.");
            }
        }
    }

    /**
     * Health check endpoint handler
     * GET /health
     */
    static class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            
            if ("GET".equals(exchange.getRequestMethod())) {
                JsonObject health = new JsonObject();
                health.addProperty("status", "healthy");
                health.addProperty("timestamp", new Date().toString());
                health.addProperty("service", "Fake News Detection API");
                
                String response = gson.toJson(health);
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.length());
                
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes("UTF-8"));
                }
            } else {
                sendErrorResponse(exchange, 405, "Method not allowed. Use GET.");
            }
        }
    }

    /**
     * CORS handler for preflight requests
     */
    static class CorsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            exchange.sendResponseHeaders(404, -1);
        }
    }

    /**
     * Stub implementation of fake news detection
     * TODO: Replace with actual ML model integration
     */
    private static DetectionResult performDetection(String newsText) {
        Random rand = new Random();
        
        // Simple heuristic-based detection (for demonstration)
        String lowerText = newsText.toLowerCase();
        
        // Check for common fake news indicators
        String[] fakeIndicators = {
            "breaking:", "shocking", "you won't believe", "doctors hate",
            "secret", "conspiracy", "they don't want you to know",
            "miracle cure", "instant", "guaranteed"
        };
        
        String[] realIndicators = {
            "according to", "research shows", "study found",
            "expert says", "data indicates", "report states"
        };
        
        int fakeScore = 0;
        int realScore = 0;
        
        for (String indicator : fakeIndicators) {
            if (lowerText.contains(indicator)) {
                fakeScore++;
            }
        }
        
        for (String indicator : realIndicators) {
            if (lowerText.contains(indicator)) {
                realScore++;
            }
        }
        
        // Determine prediction
        String prediction;
        double confidence;
        String analysis;
        
        if (fakeScore > realScore) {
            prediction = "Fake";
            confidence = 0.6 + (rand.nextDouble() * 0.3); // 0.6-0.9
            analysis = "Text contains indicators commonly found in fake news articles";
        } else if (realScore > fakeScore) {
            prediction = "Real";
            confidence = 0.6 + (rand.nextDouble() * 0.3); // 0.6-0.9
            analysis = "Text shows characteristics of legitimate news reporting";
        } else {
            prediction = rand.nextBoolean() ? "Real" : "Fake";
            confidence = 0.5 + (rand.nextDouble() * 0.3); // 0.5-0.8
            analysis = "Insufficient indicators detected, prediction based on general patterns";
        }
        
        return new DetectionResult(prediction, confidence, analysis, newsText.length());
    }

    /**
     * Helper method to read request body
     */
    private static String readRequestBody(HttpExchange exchange) throws IOException {
        StringBuilder body = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(exchange.getRequestBody(), "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                body.append(line);
            }
        }
        return body.toString();
    }

    /**
     * Helper method to add CORS headers
     */
    private static void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
    }

    /**
     * Helper method to send error responses
     */
    private static void sendErrorResponse(HttpExchange exchange, int statusCode, String message) 
            throws IOException {
        JsonObject error = new JsonObject();
        error.addProperty("error", message);
        error.addProperty("status", statusCode);
        
        String response = gson.toJson(error);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.length());
        
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes("UTF-8"));
        }
    }

    /**
     * Data class for detection results
     */
    static class DetectionResult {
        private String prediction;
        private double confidence;
        private String analysis;
        private int textLength;
        private String timestamp;

        public DetectionResult(String prediction, double confidence, String analysis, int textLength) {
            this.prediction = prediction;
            this.confidence = Math.round(confidence * 100.0) / 100.0; // Round to 2 decimal places
            this.analysis = analysis;
            this.textLength = textLength;
            this.timestamp = new Date().toString();
        }

        // Getters for JSON serialization
        public String getPrediction() { return prediction; }
        public double getConfidence() { return confidence; }
        public String getAnalysis() { return analysis; }
        public int getTextLength() { return textLength; }
        public String getTimestamp() { return timestamp; }
    }
}