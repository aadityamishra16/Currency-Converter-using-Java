## 💱 Currency Converter
<div align="center">
https://img.shields.io/badge/Java-11+-blue.svg?style=for-the-badge&logo=java
https://img.shields.io/badge/License-MIT-green.svg?style=for-the-badge
https://img.shields.io/badge/GUI-Swing-orange.svg?style=for-the-badge
https://img.shields.io/badge/API-Gemini%2520AI-purple.svg?style=for-the-badge
https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=for-the-badge

A beautiful and intelligent currency converter desktop application built with Java Swing,
powered by Google's Gemini AI for real-time exchange rates.

Features • Quick Start • Screenshots • Tech Stack • Contributing

</div>
📋 Table of Contents
Features

Screenshots

Quick Start

How to Use

Tech Stack

API Integration

Project Structure

Configuration

Troubleshooting

Contributing

License

Contact

✨ Features
🎨 Beautiful UI
Stunning purple-to-orange gradient background

Clean, modern, and intuitive interface

Responsive design that adapts to screen size

Professional typography and spacing

🤖 AI-Powered
Google Gemini AI integration for intelligent exchange rate retrieval

Real-time rates fetched on demand

Natural language processing for accurate currency identification

📊 Core Functionality
30+ world currencies supported (USD, EUR, GBP, INR, JPY, etc.)

Instant conversion with side-by-side amount/result display

Swap currencies with a single click

Refresh rates to get the latest exchange values

Offline support with built-in fallback rates

💪 Performance
Asynchronous API calls (non-blocking UI using SwingWorker)

Automatic fallback when API is unavailable

Fast response times (1-2 seconds for conversions)

Lightweight and memory efficient

🖥️ Screenshots
<div align="center">
Main Interface
https://screenshots/main.png

Conversion Example
https://screenshots/conversion.png

</div>
🚀 Quick Start
Prerequisites
Before you begin, ensure you have the following installed:

Java JDK 11 or higher

Git (for cloning the repository)

Google Gemini API Key (free - get it from Google AI Studio)

Installation
1. Clone the Repository
bash
git clone https://github.com/YOUR_USERNAME/currency-converter.git
cd currency-converter
2. Get Your Gemini API Key
bash
# Open this URL in your browser
echo "Open: https://aistudio.google.com/"
echo "Sign in with your Google account"
echo "Click 'Get API Key' in the left sidebar"
echo "Click 'Create API Key'"
echo "Copy your API key (starts with AIza)"
3. Add Your API Key
bash
# Open CurrencyService.java with your preferred editor
# Replace YOUR_GEMINI_API_KEY_HERE with your actual key

# Using nano (Linux/Mac)
nano src/CurrencyService.java

# Using notepad (Windows)
notepad src/CurrencyService.java

# Using VS Code
code src/CurrencyService.java
Find this line:

java
private static final String GEMINI_API_KEY = "YOUR_GEMINI_API_KEY_HERE";
Replace with:

java
private static final String GEMINI_API_KEY = "AIzaSy...your-actual-key-here...";
4. Compile and Run
bash
# Navigate to src directory
cd src

# Compile all Java files
javac *.java

# Run the application
java Main
That's it! 🎉 The application should now launch with a beautiful gradient interface.
