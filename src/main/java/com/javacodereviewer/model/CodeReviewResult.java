package com.javacodereviewer.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Model class representing the result of a code review
 */
public class CodeReviewResult {
    private String summary;
    private List<String> errors;
    private List<String> warnings;
    private List<String> suggestions;
    private List<String> goodPractices;
    private LocalDateTime reviewTime;
    private String apiProvider;
    private String originalCode;

    public CodeReviewResult() {
        this.errors = new ArrayList<>();
        this.warnings = new ArrayList<>();
        this.suggestions = new ArrayList<>();
        this.goodPractices = new ArrayList<>();
        this.reviewTime = LocalDateTime.now();
    }

    public CodeReviewResult(String summary, String apiProvider, String originalCode) {
        this();
        this.summary = summary;
        this.apiProvider = apiProvider;
        this.originalCode = originalCode;
    }

    // Getters and Setters
    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    public List<String> getGoodPractices() {
        return goodPractices;
    }

    public void setGoodPractices(List<String> goodPractices) {
        this.goodPractices = goodPractices;
    }

    public LocalDateTime getReviewTime() {
        return reviewTime;
    }

    public void setReviewTime(LocalDateTime reviewTime) {
        this.reviewTime = reviewTime;
    }

    public String getApiProvider() {
        return apiProvider;
    }

    public void setApiProvider(String apiProvider) {
        this.apiProvider = apiProvider;
    }

    public String getOriginalCode() {
        return originalCode;
    }

    public void setOriginalCode(String originalCode) {
        this.originalCode = originalCode;
    }

    // Helper methods
    public void addError(String error) {
        this.errors.add(error);
    }

    public void addWarning(String warning) {
        this.warnings.add(warning);
    }

    public void addSuggestion(String suggestion) {
        this.suggestions.add(suggestion);
    }

    public void addGoodPractice(String practice) {
        this.goodPractices.add(practice);
    }

    public int getTotalIssues() {
        return errors.size() + warnings.size() + suggestions.size();
    }

    public boolean hasIssues() {
        return !errors.isEmpty() || !warnings.isEmpty() || !suggestions.isEmpty();
    }
}
