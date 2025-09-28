package com.javacodereviewer.controller;

import com.javacodereviewer.model.CodeReviewResult;
import com.javacodereviewer.service.AIReviewService;
import com.javacodereviewer.service.ExportService;
import com.javacodereviewer.util.UIUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Main controller for the Java Code Reviewer application
 */
public class MainController implements Initializable {

    @FXML private TextArea codeInputArea;
    @FXML private VBox resultsContainer;
    @FXML private Button reviewButton;
    @FXML private Button loadFileButton;
    @FXML private Button clearButton;
    @FXML private Button exportButton;
    @FXML private Button settingsButton;
    @FXML private ComboBox<String> apiProviderCombo;
    @FXML private Label statusLabel;
    @FXML private ProgressBar progressBar;

    private AIReviewService reviewService;
    private ExportService exportService;
    private CodeReviewResult lastReviewResult;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize services
        reviewService = new AIReviewService();
        exportService = new ExportService();
        
        // Setup API provider combo box
        apiProviderCombo.setItems(FXCollections.observableArrayList(
            "OpenAI GPT-4",
            "Hugging Face Code Llama",
            "Anthropic Claude"
        ));
        apiProviderCombo.getSelectionModel().selectFirst();
        
        // Setup initial UI state
        updateUIState(false);
    }

    @FXML
    private void loadFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Java File");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Java Files", "*.java")
        );
        
        Stage stage = (Stage) loadFileButton.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        
        if (selectedFile != null) {
            try {
                String content = Files.readString(selectedFile.toPath());
                codeInputArea.setText(content);
                statusLabel.setText("File loaded: " + selectedFile.getName());
            } catch (IOException e) {
                showError("Error loading file", "Could not read file: " + e.getMessage());
            }
        }
    }

    @FXML
    private void clearCode() {
        codeInputArea.clear();
        resultsContainer.getChildren().clear();
        lastReviewResult = null;
        statusLabel.setText("Code cleared");
    }

    @FXML
    private void reviewCode() {
        String code = codeInputArea.getText().trim();
        if (code.isEmpty()) {
            showError("No Code", "Please enter or load Java code to review.");
            return;
        }

        String selectedProvider = apiProviderCombo.getSelectionModel().getSelectedItem();
        if (selectedProvider == null) {
            showError("No API Provider", "Please select an AI provider.");
            return;
        }

        // Start review in background thread
        Task<CodeReviewResult> reviewTask = new Task<CodeReviewResult>() {
            @Override
            protected CodeReviewResult call() throws Exception {
                updateMessage("Analyzing code...");
                return reviewService.reviewCode(code, selectedProvider);
            }
        };

        reviewTask.setOnSucceeded(event -> {
            lastReviewResult = reviewTask.getValue();
            displayResults(lastReviewResult);
            updateUIState(false);
            statusLabel.setText("Review completed successfully");
        });

        reviewTask.setOnFailed(event -> {
            updateUIState(false);
            Throwable exception = reviewTask.getException();
            showError("Review Failed", "Error during code review: " + exception.getMessage());
        });

        updateUIState(true);
        Thread reviewThread = new Thread(reviewTask);
        reviewThread.setDaemon(true);
        reviewThread.start();
    }

    @FXML
    private void exportReport() {
        if (lastReviewResult == null) {
            showError("No Results", "Please review code first before exporting.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Review Report");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Text Files", "*.txt"),
            new FileChooser.ExtensionFilter("HTML Files", "*.html")
        );

        Stage stage = (Stage) exportButton.getScene().getWindow();
        File selectedFile = fileChooser.showSaveDialog(stage);

        if (selectedFile != null) {
            try {
                exportService.exportReport(lastReviewResult, selectedFile);
                statusLabel.setText("Report exported to: " + selectedFile.getName());
            } catch (IOException e) {
                showError("Export Failed", "Could not export report: " + e.getMessage());
            }
        }
    }

    @FXML
    private void openSettings() {
        // TODO: Implement settings dialog
        showInfo("Settings", "Settings dialog will be implemented in future version.");
    }

    private void displayResults(CodeReviewResult result) {
        resultsContainer.getChildren().clear();
        
        // Add summary
        Label summaryLabel = new Label("Review Summary");
        summaryLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2c3e50;");
        resultsContainer.getChildren().add(summaryLabel);
        
        Label summaryText = new Label(result.getSummary());
        summaryText.setWrapText(true);
        summaryText.setStyle("-fx-padding: 5px; -fx-background-color: #ecf0f1; -fx-background-radius: 5px;");
        resultsContainer.getChildren().add(summaryText);
        
        // Add issues by category
        if (!result.getErrors().isEmpty()) {
            addIssueSection("❌ Errors", result.getErrors(), "#e74c3c");
        }
        
        if (!result.getWarnings().isEmpty()) {
            addIssueSection("⚠️ Warnings", result.getWarnings(), "#f39c12");
        }
        
        if (!result.getSuggestions().isEmpty()) {
            addIssueSection("💡 Suggestions", result.getSuggestions(), "#3498db");
        }
        
        if (!result.getGoodPractices().isEmpty()) {
            addIssueSection("✅ Good Practices", result.getGoodPractices(), "#27ae60");
        }
    }

    private void addIssueSection(String title, List<String> issues, String color) {
        Label sectionLabel = new Label(title);
        sectionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + color + ";");
        resultsContainer.getChildren().add(sectionLabel);
        
        for (String issue : issues) {
            Label issueLabel = new Label("• " + issue);
            issueLabel.setWrapText(true);
            issueLabel.setStyle("-fx-padding: 2px 0px 2px 10px;");
            resultsContainer.getChildren().add(issueLabel);
        }
    }

    private void updateUIState(boolean isReviewing) {
        reviewButton.setDisable(isReviewing);
        loadFileButton.setDisable(isReviewing);
        exportButton.setDisable(isReviewing || lastReviewResult == null);
        progressBar.setVisible(isReviewing);
        
        if (isReviewing) {
            reviewButton.setText("Reviewing...");
        } else {
            reviewButton.setText("Review Code");
        }
    }

    private void showError(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private void showInfo(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
