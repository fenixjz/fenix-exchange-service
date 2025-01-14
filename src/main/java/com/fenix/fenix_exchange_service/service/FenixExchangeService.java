package com.fenix.fenix_exchange_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fenix.fenix_exchange_service.model.ExchangeAmount;
import com.fenix.fenix_exchange_service.model.ExchangeRatesResponse;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.fenix.fenix_exchange_service.service.FenixConstant.BASE_URL;
import static com.fenix.fenix_exchange_service.service.FenixConstant.RATES_FILE_PATH;

@Service
@RequiredArgsConstructor
public class FenixExchangeService {

    /**
     * Converts an amount from one currency to another using exchange rates.
     *
     * @param fromCurrency The currency code to convert from (e.g., "USD").
     * @param toCurrency   The currency code to convert to (e.g., "EUR").
     * @param amount       The amount to be converted. Must be greater than 0.
     * @return An ExchangeAmount object containing the converted amount,
     *         exchange date, and details about the currencies involved.
     * @throws IllegalArgumentException If the amount is null, less than or equal to 0,
     *                                  or if the currency codes are invalid.
     * @throws RuntimeException         If the exchange rates file is missing or corrupted.
     */
    public ExchangeAmount exchange(String fromCurrency, String toCurrency, Double amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0.");
        }

        ExchangeRatesResponse json = json2object();
        Map<String, Double> rates = json.getRates();

        fromCurrency = fromCurrency.toUpperCase();
        toCurrency = toCurrency.toUpperCase();
        if (!rates.containsKey(fromCurrency) || !rates.containsKey(toCurrency)) {
            throw new IllegalArgumentException("Invalid currency code provided.");
        }

        Double fromRate = rates.get(fromCurrency);
        Double toRate = rates.get(toCurrency);

        Double usdAmount = amount / fromRate;
        Double convertedAmount = usdAmount * toRate;

        ExchangeAmount exchangeAmount = new ExchangeAmount();
        exchangeAmount.setExchangeDate(json.getDate());
        exchangeAmount.setFromCurrency(fromCurrency);
        exchangeAmount.setToCurrency(toCurrency);
        exchangeAmount.setAmount(convertedAmount);
        return exchangeAmount;
    }

    /**
     * Retrieves a list of all available currency codes from the exchange rates.
     *
     * @return A list of currency codes (e.g., ["USD", "EUR", "JPY"]). If no rates are available,
     *         returns an empty list.
     * @throws RuntimeException If the exchange rates file is missing or corrupted.
     */
    public List<String> currencies() {
        ExchangeRatesResponse json = json2object();
        if (json.getRates() == null || json.getRates().isEmpty()) {
            return Collections.emptyList(); // Ako nema kurseva, vrati praznu listu
        }
        return new ArrayList<>(json.getRates().keySet());
    }

    /**
     * Fetches the latest exchange rates from the external API and saves them as a JSON file.
     * The JSON file is stored in the location specified by RATES_FILE_PATH.
     *
     * @throws RuntimeException If the API request fails or if the JSON cannot be saved.
     */
    void saveRates() {
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder()
                .url(BASE_URL)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                saveJsonToFile(responseBody);
                System.out.println("JSON successfully saved to rates.json");
            } else {
                System.err.println("Request failed: " + response.code());
            }
        } catch (IOException e) {
            System.err.println("Error while fetching rates: " + e.getMessage());
        }
    }

    /**
     * Reads the saved exchange rates JSON file and converts it into an ExchangeRatesResponse object.
     *
     * @return An ExchangeRatesResponse object containing exchange rate data.
     * @throws RuntimeException If the JSON file is missing or if an error occurs during parsing.
     */
    private ExchangeRatesResponse json2object() {
        File file = new File(RATES_FILE_PATH);
        if (!file.exists()) {
            throw new RuntimeException("File not found: " + RATES_FILE_PATH);
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(file, ExchangeRatesResponse.class);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Saves a formatted JSON string to a file. If the parent directory does not exist,
     * it will be created.
     *
     * @param jsonContent The JSON string to save.
     * @throws RuntimeException If the JSON string cannot be saved due to an I/O error.
     */
    private void saveJsonToFile(String jsonContent) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Object json = objectMapper.readValue(jsonContent, Object.class);
            ObjectWriter writer = objectMapper.writerWithDefaultPrettyPrinter();
            String formattedJson = writer.writeValueAsString(json);

            File file = new File(RATES_FILE_PATH);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                boolean created = parentDir.mkdirs();
                if (!created) {
                    System.err.println("Failed to create directory: " + parentDir.getAbsolutePath());
                    return;
                }
            }

            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write(formattedJson);
                fileWriter.flush();
            }

            System.out.println("Formatted JSON successfully saved to " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error while saving formatted JSON to file: " + e.getMessage());
        }
    }
}
