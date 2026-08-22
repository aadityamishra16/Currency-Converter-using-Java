# Currency Converter

A desktop currency converter built with Java Swing. It converts between 30+ world currencies using exchange rates fetched from the Gemini API, with an offline fallback so the app still works without an internet connection or API key.


## Features

- Clean, gradient-styled Swing interface
- Convert between 30+ currencies (USD, EUR, GBP, JPY, INR, and more)
- Live exchange rates fetched via the Gemini AI API
- Automatic fallback to built-in offline rates if the API key is missing or the request fails
- Swap button to instantly reverse the "from" and "to" currencies
- Refresh button to re-fetch the latest rates
- Displays the current exchange rate alongside the converted result

## How It Works

The app has three main files:

| File | Responsibility |
|---|---|
| `Main.java` | Entry point. Sets up the look and feel and launches the GUI. |
| `CurrencyConverterGUI.java` | The Swing interface — input fields, currency dropdowns, buttons, and layout. |
| `CurrencyService.java` | Handles fetching exchange rates (via Gemini API or fallback) and performing conversions. |

On startup, the app tries to fetch live rates from the Gemini API. If no API key is set, or the request fails for any reason (no internet, invalid key, rate limit, etc.), it automatically falls back to a built-in offline rate table so the converter still works.

## Prerequisites

- **Java Development Kit (JDK) 8 or higher** installed and added to your system `PATH`
- (Optional) A free **Gemini API key** from [Google AI Studio](https://aistudio.google.com/) if you want live exchange rates instead of the offline fallback

Check your Java installation:

```bash
java -version
javac -version
```

## Setup

1. **Clone or download this repository**

   ```bash
   git clone https://github.com/your-username/currency-converter.git
   cd currency-converter
   ```

2. **(Optional) Add your Gemini API key**

   Open `CurrencyService.java` and replace the placeholder with your own key:

   ```java
   private static final String GEMINI_API_KEY = "your-api-key";
   ```

   You can get a free key from [Google AI Studio](https://aistudio.google.com/).

   > If you skip this step, the app will still run — it will simply use the built-in offline exchange rates instead of live ones.

## Running the App

Compile all the Java files:

```bash
javac *.java
```

Run the application:

```bash
java Main
```

The GUI window should open automatically.

## Usage

1. Enter an amount in the **Amount** field.
2. Select the currency you're converting **from** and **to** using the dropdown menus.
3. Click **Convert** (or press Enter) to see the result.
4. Use the **Swap** button to quickly reverse the two currencies.
5. Use the **Refresh** button to fetch updated exchange rates at any time.

## Supported Currencies

USD, EUR, GBP, JPY, CAD, AUD, CHF, CNY, INR, BRL, KRW, MXN, SGD, NZD, SEK, NOK, DKK, PLN, THB, VND, MYR, IDR, PHP, RUB, ZAR, AED, SAR, TRY, EGP, PKR, BDT, LKR, NPR

## License

This project is open source and available under the [MIT License](LICENSE).
