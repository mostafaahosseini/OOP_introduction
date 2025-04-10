

# بازآرایی برای نهان‌سازی (Encapsulation) و انتزاع (Abstraction)

## مرور کلی (Overview)

در این مرحله، ما یک کلاس **انتزاعی** به نام `Payment` معرفی کردیم و سه **زیرکلاس مشخص (Concrete Subclasses)** برای انواع مختلف پرداخت (`CreditCardPayment`، `DigitalWalletPayment`، `BankTransferPayment`) ساختیم. هدف اصلی ما کپسوله کردن ویژگی‌های مشترک پرداخت و انتقال منطق اعتبارسنجی از کلاس یکپارچه‌ی `PaymentProcessor` به هر زیرکلاس پرداخت بود. علاوه بر این، کلاس `PaymentProcessor` را ساده کردیم تا ساخت و اعتبارسنجی را به آبجکت‌های `Payment` واگذار کند.

## اهداف بازآرایی (Refactoring Goals)

1. **معرفی یک کلاس انتزاعی**  
   - فیلدهای مشترک: `amount`, `currency`, `customerInfo`, `paymentDetails`, `timestamp`  
   - قرارداد مشترک (`validatePayment()`) برای کلاس‌های فرزند

2. **ایجاد زیرکلاس‌های مشخص**  
   - `CreditCardPayment`, `DigitalWalletPayment`, `BankTransferPayment`  
   - هر زیرکلاس قوانین اعتبارسنجی مختص خود را پیاده‌سازی می‌کند

3. **جدا کردن منطق پردازش پرداخت**  
   - اکنون `PaymentProcessor` بر جریان کار تمرکز دارد و وظایف زیر را تفویض می‌کند:
     1. **ایجاد آبجکت** (مثلاً ساخت زیرکلاس صحیح `Payment`)  
     2. **اعتبارسنجی** (فراخوانی `validatePayment()` در زیرکلاس مربوطه)  
     3. **لاگ‌گیری تراکنش و رسیدگی به Endpointهای API خارجی** در یک محل متمرکز

## معماری کلاس (Class Architecture)

```
                   ┌──────────────────┐
                   │   Payment (abstract) 
                   │  - amount
                   │  - currency
                   │  - customerInfo
                   │  - paymentDetails
                   │  - timestamp
                   │
                   │  + validatePayment(): boolean
                   └──────────┬───────┘
                              │
 ┌────────────────────────────┴──────────────────────────┐
 │                                                       │
 ▼                                                       ▼
CreditCardPayment                                DigitalWalletPayment
- Specific validatePayment()                     - Specific validatePayment()

 ▼
BankTransferPayment
- Specific validatePayment()

```

- **Payment**: یک کلاس انتزاعی که ویژگی‌های مشترک را تعریف کرده و متد `validatePayment()` را به‌صورت قراردادی ارائه می‌کند.  
- **CreditCardPayment**, **DigitalWalletPayment**, **BankTransferPayment**: کلاس‌های مشخصی که متد `validatePayment()` را برای نوع پرداخت خودشان پیاده‌سازی می‌کنند.  
- **PaymentProcessor**: از رشته‌ی `paymentType` برای ایجاد زیرکلاس صحیح استفاده می‌کند؛ قبل از ادامه، متد `validatePayment()` را فراخوانی می‌کند و سپس شبیه‌سازی API خارجی و لاگ‌گیری را انجام می‌دهد.

## جزئیات پیاده‌سازی (Implementation Details)

### 1. کلاس انتزاعی `Payment`

```java
/**
 * Represents a general payment with basic attributes (amount, currency, customer info, etc.).
 * Subclasses must provide their own implementation of validatePayment().
 */
public abstract class Payment {
    protected double amount;
    protected String currency;
    protected Map<String, String> customerInfo;
    protected Map<String, String> paymentDetails;
    protected Date timestamp;

    /**
     * Constructs a Payment object with common fields.
     *
     * @param amount        The payment amount.
     * @param currency      The currency code (e.g., USD, EUR, GBP).
     * @param customerInfo  A map containing customer information (e.g., name, email).
     * @param paymentDetails A map containing payment-specific data (e.g., card number).
     */
    public Payment(double amount, String currency, Map<String, String> customerInfo, Map<String, String> paymentDetails) {
        this.amount = amount;
        this.currency = currency;
        this.customerInfo = customerInfo;
        this.paymentDetails = paymentDetails;
        this.timestamp = new Date();
    }

    /**
     * Validates the payment details based on business rules.
     * Concrete subclasses must override this to provide type-specific validation.
     *
     * @return true if the payment is valid; false otherwise.
     */
    public abstract boolean validatePayment();
}
```

### 2. `CreditCardPayment` (زیرکلاس مشخص)

```java
/**
 * Handles validation specific to credit card transactions.
 */
public class CreditCardPayment extends Payment {

    /**
     * Constructs a CreditCardPayment object.
     */
    public CreditCardPayment(double amount, String currency, Map<String, String> customerInfo, Map<String, String> paymentDetails) {
        super(amount, currency, customerInfo, paymentDetails);
    }

    /**
     * Validates credit card details:
     * - Amount must be positive
     * - Currency must be USD, EUR, or GBP
     * - Customer info must contain an email
     * - Card number length must be at least 12 characters
     */
    @Override
    public boolean validatePayment() {
        if (amount <= 0) return false;
        if (!currency.matches("USD|EUR|GBP")) return false;
        if (!customerInfo.containsKey("email")) return false;
        return paymentDetails.getOrDefault("card_number", "").length() >= 12;
    }
}
```

### 3. `DigitalWalletPayment` (زیرکلاس مشخص)

```java
/**
 * Handles validation specific to digital wallet transactions.
 */
public class DigitalWalletPayment extends Payment {

    /**
     * Constructs a DigitalWalletPayment object.
     */
    public DigitalWalletPayment(double amount, String currency, Map<String, String> customerInfo, Map<String, String> paymentDetails) {
        super(amount, currency, customerInfo, paymentDetails);
    }

    /**
     * Validates digital wallet details:
     * - Amount must be positive
     * - Currency must be USD, EUR, or GBP
     * - Customer info must contain an email
     * - Must contain a 'wallet_id' field
     */
    @Override
    public boolean validatePayment() {
        if (amount <= 0) return false;
        if (!currency.matches("USD|EUR|GBP")) return false;
        if (!customerInfo.containsKey("email")) return false;
        return paymentDetails.containsKey("wallet_id");
    }
}
```

### 4. `BankTransferPayment` (زیرکلاس مشخص)

```java
/**
 * Handles validation specific to bank transfer transactions.
 */
public class BankTransferPayment extends Payment {

    /**
     * Constructs a BankTransferPayment object.
     */
    public BankTransferPayment(double amount, String currency, Map<String, String> customerInfo, Map<String, String> paymentDetails) {
        super(amount, currency, customerInfo, paymentDetails);
    }

    /**
     * Validates bank transfer details:
     * - Amount must be positive
     * - Currency must be USD, EUR, or GBP
     * - Customer info must contain an email
     * - Must contain an 'account_number' field
     */
    @Override
    public boolean validatePayment() {
        if (amount <= 0) return false;
        if (!currency.matches("USD|EUR|GBP")) return false;
        if (!customerInfo.containsKey("email")) return false;
        return paymentDetails.containsKey("account_number");
    }
}
```

### 5. کلاس `PaymentProcessor` (بازآرایی‌شده)

```java
/**
 * Orchestrates the payment process by choosing the right Payment subclass,
 * validating it, and then handling processing and logging.
 */
public class PaymentProcessor {
    private Map<String, String> config;

    /**
     * Constructs a PaymentProcessor with configuration details such as API endpoints.
     *
     * @param config A map where keys are payment type endpoints (e.g., "credit_card_endpoint")
     */
    public PaymentProcessor(Map<String, String> config) {
        this.config = config;
    }

    /**
     * High-level method to process a payment based on paymentType.
     *
     * @param paymentType    The type of payment (e.g., "credit_card")
     * @param amount         The amount to pay
     * @param currency       The currency code (e.g., "USD")
     * @param customerInfo   Contains customer-specific fields like "email" or "name"
     * @param paymentDetails Contains payment-type-specific fields like "card_number"
     * @return A map with the status and transaction ID if successful, otherwise an error message.
     */
    public Map<String, String> processPayment(String paymentType, double amount, String currency,
                                              Map<String, String> customerInfo, Map<String, String> paymentDetails) {
        Payment payment;
        switch (paymentType) {
            case "credit_card":
                payment = new CreditCardPayment(amount, currency, customerInfo, paymentDetails);
                break;
            case "digital_wallet":
                payment = new DigitalWalletPayment(amount, currency, customerInfo, paymentDetails);
                break;
            case "bank_transfer":
                payment = new BankTransferPayment(amount, currency, customerInfo, paymentDetails);
                break;
            default:
                return Map.of("status", "failed", "message", "Unknown payment type");
        }

        if (!payment.validatePayment()) {
            return Map.of("status", "failed", "message", "Validation error");
        }

        Map<String, String> result = handleProcessing(paymentType, amount, currency, customerInfo, paymentDetails);
        logTransaction(paymentType, amount, currency, customerInfo, paymentDetails, result);
        return result;
    }

    /**
     * Simulates a connection to an external API endpoint and generates a transaction ID.
     */
    private Map<String, String> handleProcessing(String type, double amount, String currency,
                                                 Map<String, String> customerInfo, Map<String, String> paymentDetails) {
        String endpointKey = type + "_endpoint";
        String endpoint = config.get(endpointKey);
        String transactionId = type.substring(0, 2).toUpperCase() + new Date().getTime();

        System.out.println("Connecting to API at " + endpoint);
        System.out.println("Processing " + type + " payment for " + customerInfo.get("name"));

        return Map.of("status", "success", "transaction_id", transactionId);
    }

    /**
     * Logs the payment transaction details to the console (or could be extended for real logging systems).
     */
    private void logTransaction(String paymentType, double amount, String currency,
                                Map<String, String> customerInfo, Map<String, String> paymentDetails,
                                Map<String, String> result) {
        String logEntry = String.format("%s - %s payment of %.2f %s for %s: %s",
                new Date(), paymentType, amount, currency, customerInfo.get("name"), result);
        System.out.println("LOG: " + logEntry);
    }
}
```

### 6. نمونه استفاده در کلاس `Main`

```java
public class Main {
    public static void main(String[] args) {
        Map<String, String> config = Map.of(
                "credit_card_endpoint", "https://api.creditcard.com/process",
                "digital_wallet_endpoint", "https://api.digitalwallet.com/process",
                "bank_transfer_endpoint", "https://api.banktransfer.com/process"
        );

        PaymentProcessor processor = new PaymentProcessor(config);

        Map<String, String> customer = Map.of("name", "John Doe", "email", "john@example.com");
        Map<String, String> paymentDetails = Map.of("card_number", "123456789012", "expiry", "12/25", "cvv", "123");

        Map<String, String> result = processor.processPayment("credit_card", 100, "USD", customer, paymentDetails);
        System.out.println("Final Result: " + result);
    }
}
```

## مزایای کلیدی این بازآرایی (Key Benefits of This Refactoring)

- **جدا کردن وظایف (Separation of Concerns)**: منطق اعتبارسنجی اکنون در هر زیرکلاس قرار دارد و از پیچیدگی در `PaymentProcessor` کاسته شده است.  
- **قابلیت گسترش (Extensibility)**: برای افزودن نوع پرداخت جدید (مثلاً `CryptoPayment`)، تنها کافی است یک زیرکلاس جدید از `Payment` بسازید و در یک مکان (switch در `PaymentProcessor`) به‌روزرسانی انجام دهید.  
- **نگه‌داری آسان (Maintainability)**: هر زیرکلاس پرداخت را می‌توان به‌صورت مستقل تست و به‌روزرسانی کرد.

