# Fenix Exchange Service

Fenix Exchange Service is a Java library that facilitates working with exchange rates, including currency conversion and retrieving a list of supported currencies.

## Installation via JitPack

To use Fenix Exchange Service in your project, follow these steps:

### 1. Add the JitPack repository to `pom.xml` (Maven):

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

### 2. Add the dependency for Fenix Exchange Service:

```xml
<dependency>
    <groupId>com.github.username</groupId>
    <artifactId>fenix-exchange-service</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle (if you use Gradle):

Add the JitPack repository to `settings.gradle`:

```gradle
maven { url 'https://jitpack.io' }
```

Then add the dependency to `build.gradle`:

```gradle
dependencies {
    implementation 'com.github.username:fenix-exchange-service:1.0.0'
}
```

## How to Use the Library

### Public Methods
Fenix Exchange Service provides two public methods you can use:

1. **Currency Conversion**

   The `exchange` method allows converting an amount from one currency to another based on the latest exchange rates.

   ```java
   @Autowired
   private FenixExchangeService fenixExchangeService;

   public void convertCurrency() {
       ExchangeAmount result = fenixExchangeService.exchange("EUR", "RSD", 50.0);
       System.out.println("Converted amount: " + result.getAmount());
   }
   ```

   **Parameters:**
    - `fromCurrency` - The currency code to convert from (e.g., "USD").
    - `toCurrency` - The currency code to convert to (e.g., "EUR").
    - `amount` - The amount to be converted. Must be greater than 0.

   **Result:**
    - An `ExchangeAmount` object containing:
        - Conversion date (`exchangeDate`)
        - Source currency (`fromCurrency`)
        - Target currency (`toCurrency`)
        - Amount in the target currency (`amount`)

2. **List of Supported Currencies**

   The `currencies` method returns a list of all supported currency codes from the exchange rates.

   ```java
   @Autowired
   private FenixExchangeService fenixExchangeService;

   public void listCurrencies() {
       List<String> currencies = fenixExchangeService.currencies();
       System.out.println("Supported currencies: " + currencies);
   }
   ```

   **Result:**
    - A `List<String>` containing ISO currency codes (e.g., ["USD", "EUR", "JPY"]).

## Support
For questions or assistance, please open an issue on the GitHub repository. 😊

