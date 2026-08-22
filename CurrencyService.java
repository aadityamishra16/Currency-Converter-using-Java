import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CurrencyService {
    // 🔑 YOUR GEMINI API KEY FROM https://aistudio.google.com
    private static final String GEMINI_API_KEY = "your-api-key";
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash-exp:generateContent?key=" + GEMINI_API_KEY;
    
    private Map<String, Double> exchangeRates;
    private String baseCurrency;
    private boolean isReady = false;
    private String lastError = "";
    
    // Current fallback rates (updated as of 2024)
    private static final Map<String, Double> FALLBACK_RATES = new HashMap<>();
    static {
        FALLBACK_RATES.put("USD", 1.0);
        FALLBACK_RATES.put("EUR", 0.92);
        FALLBACK_RATES.put("GBP", 0.79);
        FALLBACK_RATES.put("JPY", 149.50);
        FALLBACK_RATES.put("CAD", 1.37);
        FALLBACK_RATES.put("AUD", 1.54);
        FALLBACK_RATES.put("CHF", 0.88);
        FALLBACK_RATES.put("CNY", 7.24);
        FALLBACK_RATES.put("INR", 83.45);
        FALLBACK_RATES.put("BRL", 5.05);
        FALLBACK_RATES.put("KRW", 1335.0);
        FALLBACK_RATES.put("MXN", 17.20);
        FALLBACK_RATES.put("SGD", 1.35);
        FALLBACK_RATES.put("NZD", 1.67);
        FALLBACK_RATES.put("SEK", 10.50);
        FALLBACK_RATES.put("NOK", 10.70);
        FALLBACK_RATES.put("DKK", 6.90);
        FALLBACK_RATES.put("PLN", 4.02);
        FALLBACK_RATES.put("THB", 36.20);
        FALLBACK_RATES.put("VND", 24800.0);
        FALLBACK_RATES.put("MYR", 4.75);
        FALLBACK_RATES.put("IDR", 15700.0);
        FALLBACK_RATES.put("PHP", 56.20);
        FALLBACK_RATES.put("RUB", 93.50);
        FALLBACK_RATES.put("ZAR", 18.80);
        FALLBACK_RATES.put("AED", 3.67);
        FALLBACK_RATES.put("SAR", 3.75);
        FALLBACK_RATES.put("TRY", 32.20);
        FALLBACK_RATES.put("EGP", 48.50);
        FALLBACK_RATES.put("PKR", 278.0);
        FALLBACK_RATES.put("BDT", 117.0);
        FALLBACK_RATES.put("LKR", 298.0);
        FALLBACK_RATES.put("NPR", 133.0);
    }
    
    public CurrencyService() {
        this.exchangeRates = new HashMap<>();
        this.baseCurrency = "USD";
    }
    
    public boolean fetchRates(String baseCurrency) {
        try {
            // Check if API key is set
            if (GEMINI_API_KEY.equals("YOUR_GEMINI_API_KEY_HERE")) {
                System.err.println("⚠️ WARNING: Please replace 'YOUR_GEMINI_API_KEY_HERE' with your actual Gemini API key");
                loadFallbackRates();
                return true;
            }
            
            System.out.println("🔄 Fetching exchange rates from Gemini AI...");
            
            // Use Gemini to get exchange rates
            String prompt = "Provide current exchange rates for " + baseCurrency + 
                           " against these currencies: USD, EUR, GBP, JPY, CAD, AUD, CHF, CNY, INR, BRL, KRW, MXN, SGD, NZD, SEK, NOK, DKK, PLN, THB, VND, MYR, IDR, PHP, RUB, ZAR, AED, SAR, TRY, EGP, PKR, BDT, LKR, NPR. " +
                           "Return ONLY a JSON object with currency codes as keys and rates as numbers. " +
                           "Example: {\"USD\":1.0, \"EUR\":0.92, \"INR\":83.45}. " +
                           "No explanations, no markdown, just the JSON object.";
            
            String response = callGeminiAPI(prompt);
            
            if (response != null && !response.isEmpty()) {
                // Parse the JSON response to extract rates
                Map<String, Double> rates = parseGeminiResponse(response);
                if (!rates.isEmpty()) {
                    this.exchangeRates = rates;
                    this.baseCurrency = baseCurrency;
                    this.isReady = true;
                    System.out.println("✅ Rates fetched successfully from Gemini!");
                    System.out.println("💱 1 USD = " + rates.get("INR") + " INR");
                    return true;
                } else {
                    System.err.println("⚠️ Could not parse rates from Gemini response");
                }
            }
            
            // If Gemini fails, use fallback
            System.err.println("⚠️ Falling back to offline rates");
            loadFallbackRates();
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Error fetching rates from Gemini: " + e.getMessage());
            loadFallbackRates();
            return true;
        }
    }
    
    private String callGeminiAPI(String prompt) throws Exception {
        String urlString = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash-exp:generateContent?key=" + GEMINI_API_KEY;
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(15000);
        
        // Build the request body
        String jsonInput = "{"
            + "\"contents\": [{"
            + "\"parts\": [{"
            + "\"text\": \"" + prompt.replace("\"", "\\\"") + "\""
            + "}]"
            + "}]"
            + "}";
        
        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonInput.getBytes());
            os.flush();
        }
        
        int responseCode = conn.getResponseCode();
        
        if (responseCode == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            conn.disconnect();
            
            // Parse the Gemini response to extract the text
            String response = content.toString();
            return extractTextFromGeminiResponse(response);
            
        } else {
            // Read error response
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            String inputLine;
            StringBuilder errorContent = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                errorContent.append(inputLine);
            }
            in.close();
            conn.disconnect();
            
            System.err.println("❌ Gemini API Error: " + responseCode + " - " + errorContent.toString());
            return null;
        }
    }
    
    private String extractTextFromGeminiResponse(String response) {
        try {
            // Look for the "text" field in the response
            Pattern pattern = Pattern.compile("\"text\"\\s*:\\s*\"([^\"]*)\"");
            Matcher matcher = pattern.matcher(response);
            if (matcher.find()) {
                return matcher.group(1);
            }
            
            // Alternative: look for content after "candidates"
            Pattern altPattern = Pattern.compile("\"candidates\"\\s*:\\s*\\[\\s*\\{[^}]*\"content\"[^}]*\"parts\"[^}]*\"text\"\\s*:\\s*\"([^\"]*)\"");
            Matcher altMatcher = altPattern.matcher(response);
            if (altMatcher.find()) {
                return altMatcher.group(1);
            }
            
            return response;
        } catch (Exception e) {
            System.err.println("Error parsing Gemini response: " + e.getMessage());
            return response;
        }
    }
    
    private Map<String, Double> parseGeminiResponse(String response) {
        Map<String, Double> rates = new HashMap<>();
        try {
            // Clean the response - remove markdown code blocks if present
            String cleaned = response.replaceAll("```json\\s*", "").replaceAll("```\\s*", "");
            
            // Look for JSON-like patterns: "CURRENCY": number
            Pattern pattern = Pattern.compile("\"([A-Z]{3})\"\\s*:\\s*([\\d.]+)");
            Matcher matcher = pattern.matcher(cleaned);
            
            while (matcher.find()) {
                String currency = matcher.group(1);
                double rate = Double.parseDouble(matcher.group(2));
                if (rate > 0) {
                    rates.put(currency, rate);
                }
            }
            
            // If we found at least 5 currencies, consider it successful
            if (rates.size() >= 5) {
                return rates;
            }
            
        } catch (Exception e) {
            System.err.println("Error parsing Gemini response: " + e.getMessage());
        }
        
        return rates;
    }
    
    private void loadFallbackRates() {
        exchangeRates.clear();
        exchangeRates.putAll(FALLBACK_RATES);
        isReady = true;
        this.baseCurrency = "USD";
        System.out.println("📊 Using fallback exchange rates (offline mode)");
        System.out.println("💱 1 USD = " + exchangeRates.get("INR") + " INR");
    }
    
    public double convert(double amount, String fromCurrency, String toCurrency) {
        if (exchangeRates.isEmpty()) {
            return -1;
        }
        
        try {
            if (!fromCurrency.equals(baseCurrency)) {
                Double fromRate = exchangeRates.get(fromCurrency);
                if (fromRate == null) return -1;
                double inBase = amount / fromRate;
                Double toRate = exchangeRates.get(toCurrency);
                if (toRate == null) return -1;
                return inBase * toRate;
            } else {
                Double toRate = exchangeRates.get(toCurrency);
                if (toRate == null) return -1;
                return amount * toRate;
            }
        } catch (Exception e) {
            return -1;
        }
    }
    
    public Map<String, Double> getExchangeRates() {
        return new HashMap<>(exchangeRates);
    }
    
    public String getBaseCurrency() {
        return baseCurrency;
    }
    
    public String[] getAvailableCurrencies() {
        if (exchangeRates.isEmpty()) {
            return new String[0];
        }
        return exchangeRates.keySet().toArray(new String[0]);
    }
    
    public boolean isReady() {
        return isReady && !exchangeRates.isEmpty();
    }
    
    public String getLastError() {
        return lastError;
    }
}