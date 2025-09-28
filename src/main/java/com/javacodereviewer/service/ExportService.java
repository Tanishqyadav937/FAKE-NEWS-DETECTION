package com.javacodereviewer.service;

import com.javacodereviewer.model.CodeReviewResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;

/**
 * Service class for exporting code review reports
 */
public class ExportService {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Exports a code review result to a file
     */
    public void exportReport(CodeReviewResult result, File outputFile) throws IOException {
        String extension = getFileExtension(outputFile.getName());
        
        switch (extension.toLowerCase()) {
            case "html":
                exportAsHTML(result, outputFile);
                break;
            case "txt":
            default:
                exportAsText(result, outputFile);
                break;
        }
    }
    
    private void exportAsText(CodeReviewResult result, File outputFile) throws IOException {
        StringBuilder report = new StringBuilder();
        
        // Header
        report.append("=".repeat(80)).append("\n");
        report.append("AI-POWERED JAVA CODE REVIEW REPORT\n");
        report.append("=".repeat(80)).append("\n");
        report.append("Generated: ").append(result.getReviewTime().format(DATE_FORMATTER)).append("\n");
        report.append("AI Provider: ").append(result.getApiProvider()).append("\n");
        report.append("=".repeat(80)).append("\n\n");
        
        // Summary
        report.append("SUMMARY\n");
        report.append("-".repeat(40)).append("\n");
        report.append(result.getSummary()).append("\n\n");
        
        // Statistics
        report.append("REVIEW STATISTICS\n");
        report.append("-".repeat(40)).append("\n");
        report.append("Total Issues Found: ").append(result.getTotalIssues()).append("\n");
        report.append("Errors: ").append(result.getErrors().size()).append("\n");
        report.append("Warnings: ").append(result.getWarnings().size()).append("\n");
        report.append("Suggestions: ").append(result.getSuggestions().size()).append("\n");
        report.append("Good Practices: ").append(result.getGoodPractices().size()).append("\n\n");
        
        // Errors
        if (!result.getErrors().isEmpty()) {
            report.append("❌ ERRORS\n");
            report.append("-".repeat(40)).append("\n");
            for (int i = 0; i < result.getErrors().size(); i++) {
                report.append(String.format("%d. %s\n", i + 1, result.getErrors().get(i)));
            }
            report.append("\n");
        }
        
        // Warnings
        if (!result.getWarnings().isEmpty()) {
            report.append("⚠️ WARNINGS\n");
            report.append("-".repeat(40)).append("\n");
            for (int i = 0; i < result.getWarnings().size(); i++) {
                report.append(String.format("%d. %s\n", i + 1, result.getWarnings().get(i)));
            }
            report.append("\n");
        }
        
        // Suggestions
        if (!result.getSuggestions().isEmpty()) {
            report.append("💡 SUGGESTIONS\n");
            report.append("-".repeat(40)).append("\n");
            for (int i = 0; i < result.getSuggestions().size(); i++) {
                report.append(String.format("%d. %s\n", i + 1, result.getSuggestions().get(i)));
            }
            report.append("\n");
        }
        
        // Good Practices
        if (!result.getGoodPractices().isEmpty()) {
            report.append("✅ GOOD PRACTICES\n");
            report.append("-".repeat(40)).append("\n");
            for (int i = 0; i < result.getGoodPractices().size(); i++) {
                report.append(String.format("%d. %s\n", i + 1, result.getGoodPractices().get(i)));
            }
            report.append("\n");
        }
        
        // Original Code
        report.append("ORIGINAL CODE\n");
        report.append("-".repeat(40)).append("\n");
        report.append(result.getOriginalCode()).append("\n");
        
        // Footer
        report.append("\n").append("=".repeat(80)).append("\n");
        report.append("End of Report\n");
        report.append("=".repeat(80)).append("\n");
        
        Files.writeString(outputFile.toPath(), report.toString());
    }
    
    private void exportAsHTML(CodeReviewResult result, File outputFile) throws IOException {
        StringBuilder html = new StringBuilder();
        
        html.append("<!DOCTYPE html>\n");
        html.append("<html lang=\"en\">\n");
        html.append("<head>\n");
        html.append("    <meta charset=\"UTF-8\">\n");
        html.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        html.append("    <title>Java Code Review Report</title>\n");
        html.append("    <style>\n");
        html.append(getHTMLStyles());
        html.append("    </style>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        
        // Header
        html.append("    <header>\n");
        html.append("        <h1>AI-Powered Java Code Review Report</h1>\n");
        html.append("        <div class=\"meta\">\n");
        html.append("            <p><strong>Generated:</strong> ").append(result.getReviewTime().format(DATE_FORMATTER)).append("</p>\n");
        html.append("            <p><strong>AI Provider:</strong> ").append(result.getApiProvider()).append("</p>\n");
        html.append("        </div>\n");
        html.append("    </header>\n");
        
        // Summary
        html.append("    <section class=\"summary\">\n");
        html.append("        <h2>Summary</h2>\n");
        html.append("        <p>").append(escapeHtml(result.getSummary())).append("</p>\n");
        html.append("    </section>\n");
        
        // Statistics
        html.append("    <section class=\"stats\">\n");
        html.append("        <h2>Review Statistics</h2>\n");
        html.append("        <div class=\"stats-grid\">\n");
        html.append("            <div class=\"stat-item\">\n");
        html.append("                <span class=\"stat-number\">").append(result.getTotalIssues()).append("</span>\n");
        html.append("                <span class=\"stat-label\">Total Issues</span>\n");
        html.append("            </div>\n");
        html.append("            <div class=\"stat-item error\">\n");
        html.append("                <span class=\"stat-number\">").append(result.getErrors().size()).append("</span>\n");
        html.append("                <span class=\"stat-label\">Errors</span>\n");
        html.append("            </div>\n");
        html.append("            <div class=\"stat-item warning\">\n");
        html.append("                <span class=\"stat-number\">").append(result.getWarnings().size()).append("</span>\n");
        html.append("                <span class=\"stat-label\">Warnings</span>\n");
        html.append("            </div>\n");
        html.append("            <div class=\"stat-item suggestion\">\n");
        html.append("                <span class=\"stat-number\">").append(result.getSuggestions().size()).append("</span>\n");
        html.append("                <span class=\"stat-label\">Suggestions</span>\n");
        html.append("            </div>\n");
        html.append("        </div>\n");
        html.append("    </section>\n");
        
        // Issues sections
        if (!result.getErrors().isEmpty()) {
            html.append("    <section class=\"issues errors\">\n");
            html.append("        <h2>❌ Errors</h2>\n");
            html.append("        <ul>\n");
            for (String error : result.getErrors()) {
                html.append("            <li>").append(escapeHtml(error)).append("</li>\n");
            }
            html.append("        </ul>\n");
            html.append("    </section>\n");
        }
        
        if (!result.getWarnings().isEmpty()) {
            html.append("    <section class=\"issues warnings\">\n");
            html.append("        <h2>⚠️ Warnings</h2>\n");
            html.append("        <ul>\n");
            for (String warning : result.getWarnings()) {
                html.append("            <li>").append(escapeHtml(warning)).append("</li>\n");
            }
            html.append("        </ul>\n");
            html.append("    </section>\n");
        }
        
        if (!result.getSuggestions().isEmpty()) {
            html.append("    <section class=\"issues suggestions\">\n");
            html.append("        <h2>💡 Suggestions</h2>\n");
            html.append("        <ul>\n");
            for (String suggestion : result.getSuggestions()) {
                html.append("            <li>").append(escapeHtml(suggestion)).append("</li>\n");
            }
            html.append("        </ul>\n");
            html.append("    </section>\n");
        }
        
        if (!result.getGoodPractices().isEmpty()) {
            html.append("    <section class=\"issues good-practices\">\n");
            html.append("        <h2>✅ Good Practices</h2>\n");
            html.append("        <ul>\n");
            for (String practice : result.getGoodPractices()) {
                html.append("            <li>").append(escapeHtml(practice)).append("</li>\n");
            }
            html.append("        </ul>\n");
            html.append("    </section>\n");
        }
        
        // Original Code
        html.append("    <section class=\"code\">\n");
        html.append("        <h2>Original Code</h2>\n");
        html.append("        <pre><code>").append(escapeHtml(result.getOriginalCode())).append("</code></pre>\n");
        html.append("    </section>\n");
        
        html.append("</body>\n");
        html.append("</html>\n");
        
        Files.writeString(outputFile.toPath(), html.toString());
    }
    
    private String getHTMLStyles() {
        return """
            body {
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                line-height: 1.6;
                margin: 0;
                padding: 20px;
                background-color: #f5f5f5;
            }
            header {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                padding: 30px;
                border-radius: 10px;
                margin-bottom: 30px;
                text-align: center;
            }
            header h1 {
                margin: 0 0 10px 0;
                font-size: 2.5em;
            }
            .meta {
                opacity: 0.9;
            }
            section {
                background: white;
                margin: 20px 0;
                padding: 25px;
                border-radius: 8px;
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            }
            h2 {
                color: #333;
                border-bottom: 2px solid #eee;
                padding-bottom: 10px;
            }
            .stats-grid {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
                gap: 20px;
                margin-top: 20px;
            }
            .stat-item {
                text-align: center;
                padding: 20px;
                border-radius: 8px;
                background: #f8f9fa;
            }
            .stat-item.error { background: #ffe6e6; }
            .stat-item.warning { background: #fff3cd; }
            .stat-item.suggestion { background: #d1ecf1; }
            .stat-number {
                display: block;
                font-size: 2em;
                font-weight: bold;
                color: #333;
            }
            .stat-label {
                color: #666;
                font-size: 0.9em;
            }
            .issues ul {
                list-style: none;
                padding: 0;
            }
            .issues li {
                padding: 10px;
                margin: 5px 0;
                border-left: 4px solid #ddd;
                background: #f9f9f9;
            }
            .errors li { border-left-color: #dc3545; }
            .warnings li { border-left-color: #ffc107; }
            .suggestions li { border-left-color: #17a2b8; }
            .good-practices li { border-left-color: #28a745; }
            .code pre {
                background: #2d3748;
                color: #e2e8f0;
                padding: 20px;
                border-radius: 8px;
                overflow-x: auto;
                font-family: 'Courier New', monospace;
            }
            """;
    }
    
    private String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&#39;");
    }
    
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filename.length() - 1) {
            return filename.substring(lastDotIndex + 1);
        }
        return "txt";
    }
}
