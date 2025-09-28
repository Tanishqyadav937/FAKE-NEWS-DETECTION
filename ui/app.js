/**
 * Fake News Detection API - Frontend JavaScript
 * Handles UI interactions, API calls, and state management
 */

class FakeNewsDetector {
    constructor() {
        this.API_BASE_URL = 'http://localhost:8080';
        this.isAnalyzing = false;
        
        // Initialize DOM elements
        this.initElements();
        
        // Bind event listeners
        this.bindEvents();
        
        // Check API health on load
        this.checkApiHealth();
    }

    /**
     * Initialize DOM element references
     */
    initElements() {
        // Input elements
        this.newsTextarea = document.getElementById('newsText');
        this.charCountSpan = document.getElementById('charCount');
        this.analyzeBtn = document.getElementById('analyzeBtn');
        
        // Results elements
        this.resultsCard = document.getElementById('resultsCard');
        this.loadingState = document.getElementById('loadingState');
        this.resultsContent = document.getElementById('resultsContent');
        this.errorState = document.getElementById('errorState');
        
        // Result content elements
        this.predictionBadge = document.getElementById('predictionBadge');
        this.confidenceScore = document.getElementById('confidenceScore');
        this.analysisText = document.getElementById('analysisText');
        this.textLength = document.getElementById('textLength');
        this.timestamp = document.getElementById('timestamp');
        
        // Error elements
        this.errorMessage = document.getElementById('errorMessage');
        this.retryBtn = document.getElementById('retryBtn');
        
        // API status elements
        this.apiStatus = document.getElementById('apiStatus');
    }

    /**
     * Bind event listeners
     */
    bindEvents() {
        // Character count update
        this.newsTextarea.addEventListener('input', () => {
            this.updateCharCount();
            this.validateInput();
        });

        // Analyze button click
        this.analyzeBtn.addEventListener('click', () => {
            if (!this.isAnalyzing) {
                this.analyzeNews();
            }
        });

        // Retry button click
        this.retryBtn.addEventListener('click', () => {
            this.analyzeNews();
        });

        // Enter key in textarea (Ctrl+Enter to analyze)
        this.newsTextarea.addEventListener('keydown', (e) => {
            if (e.ctrlKey && e.key === 'Enter') {
                if (!this.isAnalyzing) {
                    this.analyzeNews();
                }
            }
        });
    }

    /**
     * Update character count display
     */
    updateCharCount() {
        const text = this.newsTextarea.value;
        const count = text.length;
        this.charCountSpan.textContent = `${count} character${count !== 1 ? 's' : ''}`;
        
        // Add visual feedback for length
        if (count < 50) {
            this.charCountSpan.style.color = '#d97706'; // warning
        } else if (count > 5000) {
            this.charCountSpan.style.color = '#dc2626'; // danger
        } else {
            this.charCountSpan.style.color = '#6b7280'; // normal
        }
    }

    /**
     * Validate input and update button state
     */
    validateInput() {
        const text = this.newsTextarea.value.trim();
        const isValid = text.length >= 10; // Minimum 10 characters
        
        this.analyzeBtn.disabled = !isValid || this.isAnalyzing;
        
        if (!isValid && text.length > 0) {
            this.analyzeBtn.querySelector('.btn-text').textContent = 'Text too short';
        } else if (this.isAnalyzing) {
            this.analyzeBtn.querySelector('.btn-text').textContent = 'Analyzing...';
        } else {
            this.analyzeBtn.querySelector('.btn-text').textContent = 'Analyze Article';
        }
    }

    /**
     * Check API health status
     */
    async checkApiHealth() {
        try {
            const response = await fetch(`${this.API_BASE_URL}/health`, {
                method: 'GET',
                timeout: 5000
            });

            if (response.ok) {
                this.setApiStatus('online', '🟢 Online');
            } else {
                this.setApiStatus('offline', '🔴 Server Error');
            }
        } catch (error) {
            this.setApiStatus('offline', '🔴 Offline');
            console.warn('API health check failed:', error.message);
        }
    }

    /**
     * Set API status indicator
     */
    setApiStatus(status, text) {
        this.apiStatus.className = `status-indicator ${status}`;
        this.apiStatus.textContent = text;
    }

    /**
     * Main analysis function
     */
    async analyzeNews() {
        const text = this.newsTextarea.value.trim();
        
        if (!text || text.length < 10) {
            this.showError('Please enter at least 10 characters of text to analyze.');
            return;
        }

        try {
            // Set loading state
            this.setAnalyzing(true);
            this.showResults();
            this.showLoading();

            // Make API request
            const response = await fetch(`${this.API_BASE_URL}/detect`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ text: text })
            });

            if (!response.ok) {
                let errorMessage = `Server returned ${response.status}`;
                
                try {
                    const errorData = await response.json();
                    errorMessage = errorData.error || errorMessage;
                } catch (e) {
                    // Use default error message if parsing fails
                }
                
                throw new Error(errorMessage);
            }

            const result = await response.json();
            
            // Validate response structure
            if (!this.validateApiResponse(result)) {
                throw new Error('Invalid response format from API');
            }

            // Display results
            this.displayResults(result);
            
            // Update API status to online
            this.setApiStatus('online', '🟢 Online');

        } catch (error) {
            console.error('Analysis failed:', error);
            
            // Show appropriate error message
            let userMessage;
            if (error.message.includes('fetch')) {
                userMessage = 'Unable to connect to the API server. Please ensure the server is running on http://localhost:8080';
                this.setApiStatus('offline', '🔴 Connection Failed');
            } else {
                userMessage = error.message;
            }
            
            this.showError(userMessage);
        } finally {
            this.setAnalyzing(false);
        }
    }

    /**
     * Validate API response structure
     */
    validateApiResponse(response) {
        return response && 
               typeof response.prediction === 'string' &&
               typeof response.confidence === 'number' &&
               typeof response.analysis === 'string';
    }

    /**
     * Set analyzing state
     */
    setAnalyzing(analyzing) {
        this.isAnalyzing = analyzing;
        this.validateInput();
        
        // Update button icon during analysis
        const btnIcon = this.analyzeBtn.querySelector('.btn-icon');
        if (analyzing) {
            btnIcon.textContent = '⏳';
        } else {
            btnIcon.textContent = '🚀';
        }
    }

    /**
     * Show results card
     */
    showResults() {
        this.resultsCard.classList.remove('hidden');
        
        // Smooth scroll to results
        setTimeout(() => {
            this.resultsCard.scrollIntoView({
                behavior: 'smooth',
                block: 'start'
            });
        }, 100);
    }

    /**
     * Show loading state
     */
    showLoading() {
        this.loadingState.classList.remove('hidden');
        this.resultsContent.classList.add('hidden');
        this.errorState.classList.add('hidden');
    }

    /**
     * Display analysis results
     */
    displayResults(result) {
        // Hide loading and error states
        this.loadingState.classList.add('hidden');
        this.errorState.classList.add('hidden');
        
        // Show results content
        this.resultsContent.classList.remove('hidden');

        // Update prediction badge
        const prediction = result.prediction.toLowerCase();
        this.predictionBadge.textContent = result.prediction;
        this.predictionBadge.className = `prediction-badge ${prediction}`;
        
        // Add appropriate icon
        if (prediction === 'fake') {
            this.predictionBadge.innerHTML = `🚨 ${result.prediction}`;
        } else {
            this.predictionBadge.innerHTML = `✅ ${result.prediction}`;
        }

        // Update confidence score
        const confidencePercent = Math.round(result.confidence * 100);
        this.confidenceScore.textContent = `${confidencePercent}% confidence`;

        // Update analysis text
        this.analysisText.textContent = result.analysis;

        // Update metadata
        this.textLength.textContent = `${result.textLength || this.newsTextarea.value.length} characters`;
        
        // Format timestamp if provided
        let displayTime = 'Just now';
        if (result.timestamp) {
            try {
                const date = new Date(result.timestamp);
                displayTime = date.toLocaleString();
            } catch (e) {
                displayTime = result.timestamp;
            }
        }
        this.timestamp.textContent = displayTime;

        // Add subtle animation
        this.resultsContent.style.opacity = '0';
        this.resultsContent.style.transform = 'translateY(10px)';
        
        setTimeout(() => {
            this.resultsContent.style.transition = 'opacity 0.3s ease, transform 0.3s ease';
            this.resultsContent.style.opacity = '1';
            this.resultsContent.style.transform = 'translateY(0)';
        }, 50);
    }

    /**
     * Show error state
     */
    showError(message) {
        // Hide loading and results states
        this.loadingState.classList.add('hidden');
        this.resultsContent.classList.add('hidden');
        
        // Show error state
        this.errorState.classList.remove('hidden');
        this.errorMessage.textContent = message;
        
        // Show results card if hidden
        if (this.resultsCard.classList.contains('hidden')) {
            this.showResults();
        }
    }

    /**
     * Utility method to format large numbers
     */
    formatNumber(num) {
        if (num >= 1000000) {
            return (num / 1000000).toFixed(1) + 'M';
        } else if (num >= 1000) {
            return (num / 1000).toFixed(1) + 'K';
        }
        return num.toString();
    }
}

// Initialize the application when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    console.log('🔍 Fake News Detection UI initialized');
    new FakeNewsDetector();
});

// Add some helpful console messages for developers
console.log(`
🚀 Fake News Detection API Frontend
📡 API Endpoint: http://localhost:8080/detect
📝 GitHub: Built with Core Java + Vanilla JS
🎨 UI: Clean, responsive, accessible design

💡 Usage:
1. Start the Java server: .\\run.ps1
2. Open this HTML file in your browser
3. Paste news text and click "Analyze Article"

⌨️  Tip: Use Ctrl+Enter in the textarea to quickly analyze
`);

// Export for potential module use
window.FakeNewsDetector = FakeNewsDetector;