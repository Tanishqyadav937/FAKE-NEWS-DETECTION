package com.javacodereviewer.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.javacodereviewer.model.CodeReviewResult;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Service class for integrating with AI APIs for code review
 */
public class AIReviewService {
    
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String HUGGINGFACE_API_URL = "https://api-inference.huggingface.co/models/codellama/CodeLlama-7b-Instruct-hf";
    
    private final OkHttpClient httpClient;
    private final Gson gson;
    
    public AIReviewService() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }
    
    /**
     * Reviews Java code using the specified AI provider
     */
    public CodeReviewResult reviewCode(String code, String provider) throws Exception {
        String response = callAIAPI(code, provider);
        return parseAIResponse(response, provider, code);
    }
    
    private String callAIAPI(String code, String provider) throws IOException {
        String apiKey = getAPIKey(provider);
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new RuntimeException("API key not configured for " + provider + ". Please set it in settings.");
        }
        
        switch (provider) {
            case "OpenAI GPT-4":
                return callOpenAI(code, apiKey);
            case "Hugging Face Code Llama":
                return callHuggingFace(code, apiKey);
            case "Anthropic Claude":
                return callClaude(code, apiKey);
            default:
                throw new IllegalArgumentException("Unsupported AI provider: " + provider);
        }
    }
    
    private String callOpenAI(String code, String apiKey) throws IOException {
        String prompt = buildPrompt(code);
        
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", "gpt-4");
        requestBody.addProperty("temperature", 0.3);
        requestBody.addProperty("max_tokens", 2000);
        
        JsonObject message = new JsonObject();
        message.addProperty("role", "user");
        message.addProperty("content", prompt);
        requestBody.add("messages", gson.toJsonTree(new JsonObject[]{message}));
        
        RequestBody body = RequestBody.create(
            requestBody.toString(),
            MediaType.get("application/json; charset=utf-8")
        );
        
        Request request = new Request.Builder()
                .url(OPENAI_API_URL)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("OpenAI API call failed: " + response.code() + " " + response.message());
            }
            
            String responseBody = response.body().string();
            JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
            return jsonResponse.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString();
        }
    }
    
    private String callHuggingFace(String code, String apiKey) throws IOException {
        String prompt = buildPrompt(code);
        
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("inputs", prompt);
        requestBody.addProperty("parameters", gson.toJson(new JsonObject() {{
            addProperty("max_new_tokens", 1000);
            addProperty("temperature", 0.3);
            addProperty("return_full_text", false);
        }}));
        
        RequestBody body = RequestBody.create(
            requestBody.toString(),
            MediaType.get("application/json; charset=utf-8")
        );
        
        Request request = new Request.Builder()
                .url(HUGGINGFACE_API_URL)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Hugging Face API call failed: " + response.code() + " " + response.message());
            }
            
            String responseBody = response.body().string();
            JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
            return jsonResponse.getAsJsonArray("generated_text")
                    .get(0).getAsString();
        }
    }
    
    private String callClaude(String code, String apiKey) throws IOException {
        // Claude API implementation would go here
        // For now, return a mock response
        return "Claude API integration not yet implemented. Please use OpenAI or Hugging Face.";
    }
    
    private String buildPrompt(String code) {
        return String.format("""
            Please review the following Java code and provide a comprehensive analysis. 
            Format your response as JSON with the following structure:
            
            {
                "summary": "Brief overview of the code quality and main issues",
                "errors": ["List of actual errors or bugs"],
                "warnings": ["List of potential issues or code smells"],
                "suggestions": ["List of improvement suggestions"],
                "goodPractices": ["List of good practices already followed"]
            }
            
            Focus on:
            - Syntax errors and compilation issues
            - Security vulnerabilities
            - Performance issues
            - Code style and best practices
            - Design patterns and architecture
            - Error handling
            - Documentation and comments
            
            Java Code to Review:
            ```java
            %s
            ```
            
            Please provide a detailed analysis in the JSON format specified above.
            """, code);
    }
    
    private CodeReviewResult parseAIResponse(String response, String provider, String originalCode) {
        CodeReviewResult result = new CodeReviewResult();
        result.setApiProvider(provider);
        result.setOriginalCode(originalCode);
        
        try {
            // Try to parse as JSON first
            JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
            
            result.setSummary(jsonResponse.get("summary").getAsString());
            
            if (jsonResponse.has("errors")) {
                jsonResponse.getAsJsonArray("errors").forEach(item -> 
                    result.addError(item.getAsString()));
            }
            
            if (jsonResponse.has("warnings")) {
                jsonResponse.getAsJsonArray("warnings").forEach(item -> 
                    result.addWarning(item.getAsString()));
            }
            
            if (jsonResponse.has("suggestions")) {
                jsonResponse.getAsJsonArray("suggestions").forEach(item -> 
                    result.addSuggestion(item.getAsString()));
            }
            
            if (jsonResponse.has("goodPractices")) {
                jsonResponse.getAsJsonArray("goodPractices").forEach(item -> 
                    result.addGoodPractice(item.getAsString()));
            }
            
        } catch (Exception e) {
            // If JSON parsing fails, treat the entire response as summary
            result.setSummary("AI Response (Raw):\n" + response);
            result.addWarning("Could not parse structured response from AI. Raw response provided above.");
        }
        
        return result;
    }
    
    private String getAPIKey(String provider) {
        // In a real application, these would be loaded from configuration
        // For now, return environment variables or placeholder values
        switch (provider) {
            case "OpenAI GPT-4":
                return System.getenv("OPENAI_API_KEY");
            case "Hugging Face Code Llama":
                return System.getenv("HUGGINGFACE_API_KEY");
            case "Anthropic Claude":
                return System.getenv("CLAUDE_API_KEY");
            default:
                return null;
        }
    }
}
