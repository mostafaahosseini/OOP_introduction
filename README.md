در ادامه یک نمونه مستندات به سبک `README.md` برای **مرحله ۳ (Stage 3)** ارائه شده است. می‌توانید از این متن در مخزن (Repository) خود استفاده کنید تا توضیح دهید چگونه واسط `PaymentGateway` را معرفی کرده‌اید، درگاه‌های مشخصی را پیاده‌سازی کرده‌اید و به چندریختی (Polymorphism) دست یافته‌اید.

---

# مرحله ۳: وراثت و چندریختی با واسط PaymentGateway

## مرور کلی (Overview)

در این مرحله، ما ادغام درگاه پرداخت (Payment Gateway) خارجی را از منطق اصلی پرداخت جدا می‌کنیم؛ این جداسازی با معرفی یک واسط جدید به نام `PaymentGateway` انجام می‌شود. این تغییر در طراحی باعث می‌شود بتوانید درگاه‌های پرداخت مختلف (مثلاً Stripe یا PayPal) را بدون تغییر در کد پردازش یا اعتبارسنجی پرداخت‌های موجود، جابه‌جا کنید. همچنین این کار قدرت **چندریختی** را نشان می‌دهد که از اصول مهم برنامه‌نویسی شیءگرا است.

## اهداف (Goals)

1. **طراحی واسط (Interface Design)**  
   تعریف یک واسط به نام `PaymentGateway` که عملیات مشترک مربوط به پردازش پرداخت خارجی را مشخص می‌کند:
   - `processPayment(Payment payment)`
   - `refundPayment(String transactionId, double amount)`
   - `getTransactionStatus(String transactionId)`

2. **پیاده‌سازی درگاه‌های مشخص (Implement Specific Gateways)**  
   ایجاد دست‌کم دو کلاس که واسط `PaymentGateway` را پیاده‌سازی می‌کنند:
   - `StripeGateway`
   - `PayPalGateway`
   در صورت تمایل، یک کلاس انتزاعی `BaseGateway` نیز معرفی کنید تا کد مشترک (مثلاً ساخت آی‌دی تراکنش یا پیکربندی) در آن قرار گیرد.

3. **چندریختی (Polymorphism)**  
   - تغییر سیستم (به‌ویژه کلاس `PaymentProcessor`) به نحوی که در زمان اجرا (Runtime) بتواند هر شیء پیاده‌کننده‌ی `PaymentGateway` را بپذیرد.  
   - نمایش این موضوع که یک آبجکت `Payment` چگونه می‌تواند توسط درگاه‌های مختلف تنها از طریق یک تغییر پیکربندی ساده یا تزریق در سازنده (Constructor Injection) پردازش شود.

## جزئیات پیاده‌سازی (Implementation Details)

### 1. واسط PaymentGateway

```java
import java.util.Map;

/**
 * Defines the contract for external payment gateway integrations.
 * Implementing classes should handle communication with a third-party service.
 */
public interface PaymentGateway {

    /**
     * Processes a payment through this gateway.
     *
     * @param payment A concrete Payment object containing amount, currency, etc.
     * @return A map containing the status of the operation, transaction ID, or error details.
     */
    Map<String, String> processPayment(Payment payment);

    /**
     * Initiates a refund for a given transaction.
     *
     * @param transactionId The ID of the transaction to refund.
     * @param amount        The amount to refund.
     * @return A map containing refund status and new transaction info, if applicable.
     */
    Map<String, String> refundPayment(String transactionId, double amount);

    /**
     * Retrieves the current status of a transaction in the gateway.
     *
     * @param transactionId The ID of the transaction to check.
     * @return A string describing the transaction status (e.g., "completed", "pending", etc.).
     */
    String getTransactionStatus(String transactionId);
}
```

### 2. BaseGateway

اگر بخواهید منطق مشترکی مانند تولید آی‌دی تراکنش یا دسترسی به تنظیمات پیکربندی را بین چند درگاه به اشتراک بگذارید، می‌توانید یک کلاس انتزاعی با نام `BaseGateway` ایجاد کنید:

```java
import java.util.Map;

/**
 * Provides shared functionality for gateway implementations, such as config handling.
 */
public abstract class BaseGateway implements PaymentGateway {
    protected Map<String, String> config;

    public BaseGateway(Map<String, String> config) {
        this.config = config;
    }

    /**
     * Utility to generate a unique transaction ID using a given prefix.
     */
    protected String generateTransactionId(String prefix) {
        return prefix + System.currentTimeMillis();
    }
}
```

### 3. پیاده‌سازی درگاه‌های مشخص (Concrete Gateway Implementations)

#### 3.1 StripeGateway

```java
import java.util.Map;

/**
 * A concrete implementation of PaymentGateway for Stripe.
 */
public class StripeGateway extends BaseGateway {

    public StripeGateway(Map<String, String> config) {
        super(config);
    }

    @Override
    public Map<String, String> processPayment(Payment payment) {
        System.out.println("StripeGateway: Processing payment for " + payment.customerInfo.get("name"));
        System.out.println("StripeGateway: Using endpoint: " + config.getOrDefault("stripeEndpoint", "N/A"));

        String transactionId = generateTransactionId("STR_");
        return Map.of("status", "success", "transaction_id", transactionId);
    }

    @Override
    public Map<String, String> refundPayment(String transactionId, double amount) {
        System.out.println("StripeGateway: Refunding " + amount + " for transaction " + transactionId);
        return Map.of("status", "refunded", "refund_id", "REF_" + transactionId);
    }

    @Override
    public String getTransactionStatus(String transactionId) {
        System.out.println("StripeGateway: Checking status for transaction " + transactionId);
        return "completed";
    }
}
```

#### 3.2 PayPalGateway

```java
import java.util.Map;

/**
 * A concrete implementation of PaymentGateway for PayPal.
 */
public class PayPalGateway extends BaseGateway {

    public PayPalGateway(Map<String, String> config) {
        super(config);
    }

    @Override
    public Map<String, String> processPayment(Payment payment) {
        System.out.println("PayPalGateway: Processing payment for " + payment.customerInfo.get("name"));
        System.out.println("PayPalGateway: Using endpoint: " + config.getOrDefault("paypalEndpoint", "N/A"));

        String transactionId = generateTransactionId("PP_");
        return Map.of("status", "success", "transaction_id", transactionId);
    }

    @Override
    public Map<String, String> refundPayment(String transactionId, double amount) {
        System.out.println("PayPalGateway: Refunding " + amount + " for transaction " + transactionId);
        return Map.of("status", "refunded", "refund_id", "REF_" + transactionId);
    }

    @Override
    public String getTransactionStatus(String transactionId) {
        System.out.println("PayPalGateway: Checking status for transaction " + transactionId);
        return "completed";
    }
}
```

### 4. به‌روزرسانی کلاس PaymentProcessor

در این قسمت، کلاس `PaymentProcessor` (از مرحله ۲) را طوری تغییر می‌دهیم که به جای پردازش مستقیم درگاه خارجی، متکی به یک شیء از جنس `PaymentGateway` باشد:

```java
import java.util.Map;

/**
 * Orchestrates payment validation and delegates to a specific PaymentGateway.
 */
public class PaymentProcessor {

    private final PaymentGateway gateway;

    /**
     * Constructs the PaymentProcessor with a specified gateway (e.g., Stripe, PayPal).
     */
    public PaymentProcessor(PaymentGateway gateway) {
        this.gateway = gateway;
    }

    /**
     * Processes a Payment object by:
     * 1) Validating the Payment
     * 2) Invoking the gateway's processPayment method
     */
    public Map<String, String> processPayment(Payment payment) {
        // Validate domain logic first
        if (!payment.validatePayment()) {
            return Map.of("status", "failed", "message", "Validation error");
        }

        // Delegate to the gateway for external processing
        Map<String, String> result = gateway.processPayment(payment);
        // Optionally log or persist the transaction result here
        return result;
    }

    /**
     * Initiates a refund through the injected gateway.
     */
    public Map<String, String> refundPayment(String transactionId, double amount) {
        return gateway.refundPayment(transactionId, amount);
    }

    /**
     * Retrieves the status of a transaction from the injected gateway.
     */
    public String getTransactionStatus(String transactionId) {
        return gateway.getTransactionStatus(transactionId);
    }
}
```

> **نکته**  
> کلاس‌های فرزند `Payment` (مانند `CreditCardPayment`، `DigitalWalletPayment` و `BankTransferPayment`) از مرحله ۲ به همان شکل باقی می‌مانند، چراکه هنوز مسئولیت اعتبارسنجی خاص آن نوع پرداخت را بر عهده دارند.

### 5. نمونه اجرا در کلاس Main

مثال زیر **چندریختی در زمان اجرا** را نشان می‌دهد؛ ما بدون تغییر کد در کلاس‌های `PaymentProcessor` یا `Payment`، بین Stripe و PayPal جابه‌جا می‌شویم:

```java
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // Sample gateway configs
        Map<String, String> stripeConfig = Map.of(
                "stripeEndpoint", "https://api.stripe.com",
                "apiKey", "sk_test_123"
        );
        Map<String, String> paypalConfig = Map.of(
                "paypalEndpoint", "https://api.paypal.com",
                "clientId", "paypal_client_456"
        );

        // A typical Payment object from Stage 2 (e.g., CreditCardPayment)
        Map<String, String> customerInfo = Map.of("name", "John Doe", "email", "john@example.com");
        Map<String, String> cardDetails = Map.of("card_number", "123456789012", "expiry", "12/25", "cvv", "123");
        Payment creditCardPayment = new CreditCardPayment(100, "USD", customerInfo, cardDetails);

        // 1) Using StripeGateway
        PaymentGateway stripeGateway = new StripeGateway(stripeConfig);
        PaymentProcessor stripeProcessor = new PaymentProcessor(stripeGateway);

        Map<String, String> stripeResult = stripeProcessor.processPayment(creditCardPayment);
        System.out.println("Stripe Result: " + stripeResult);

        // 2) Switching to PayPalGateway at runtime
        PaymentGateway paypalGateway = new PayPalGateway(paypalConfig);
        PaymentProcessor paypalProcessor = new PaymentProcessor(paypalGateway);

        Map<String, String> paypalResult = paypalProcessor.processPayment(creditCardPayment);
        System.out.println("PayPal Result: " + paypalResult);

        // Demonstrating refund
        System.out.println("Refunding on PayPal...");
        Map<String, String> refundResult = paypalProcessor.refundPayment(paypalResult.get("transaction_id"), 50.0);
        System.out.println("Refund Result: " + refundResult);

        // Checking transaction status
        String status = paypalProcessor.getTransactionStatus(paypalResult.get("transaction_id"));
        System.out.println("PayPal Transaction Status: " + status);
    }
}
```

## مزایای این بازآرایی (Benefits of This Refactoring)

- **اصل باز-بسته (Open-Closed Principle)**  
  افزودن یک درگاه پرداخت جدید (مثلاً `CryptoGateway`) دیگر نیازی به تغییر در `PaymentProcessor` ندارد. فقط کافی است یک پیاده‌سازی جدید از `PaymentGateway` بنویسید.

- **اصل معکوس وابستگی (Dependency Inversion)**  
  ماژول سطح بالا (`PaymentProcessor`) به یک واسط (`PaymentGateway`) وابسته است، نه به پیاده‌سازی سطح پایین خاصی.

- **چندریختی (Polymorphism)**  
  در زمان اجرا می‌توانیم پیاده‌سازی‌های مختلف واسط `PaymentGateway` را جایگزین کنیم، بدون آنکه نیاز به دست‌کاری کد با `switch` یا شاخه‌های if-else داشته باشیم.
