# OOP_introduction




در ادامه یک نمونه مستندات مرحله ۲ (Stage 2) به سبک README.md ارائه شده است که با الزامات تمرین و پیاده‌سازی فعلی شما همخوانی دارد. این مستند شامل یک مرور کلی از بازآرایی، معماری کلاس‌های جدید، و نمونه‌هایی از توضیحات به سبک Javadoc است. در صورت نیاز می‌توانید آن را مطابق سلیقه یا استاندارد تیم خود تنظیم کنید.

مرحله ۲: بازآرایی برای نهان‌سازی (Encapsulation) و انتزاع (Abstraction)
مرور کلی (Overview)
در این مرحله، ما یک کلاس انتزاعی به نام Payment معرفی کردیم و سه زیرکلاس مشخص (Concrete Subclasses) برای انواع مختلف پرداخت (CreditCardPayment، DigitalWalletPayment، BankTransferPayment) ساختیم. هدف اصلی ما کپسوله کردن ویژگی‌های مشترک پرداخت و انتقال منطق اعتبارسنجی از کلاس یکپارچه‌ی PaymentProcessor به هر زیرکلاس پرداخت بود. علاوه بر این، کلاس PaymentProcessor را ساده کردیم تا ساخت و اعتبارسنجی را به آبجکت‌های Payment واگذار کند.
اهداف بازآرایی (Refactoring Goals)
معرفی یک کلاس انتزاعی


فیلدهای مشترک: amount, currency, customerInfo, paymentDetails, timestamp


قرارداد مشترک (validatePayment()) برای کلاس‌های فرزند


ایجاد زیرکلاس‌های مشخص


CreditCardPayment، DigitalWalletPayment، BankTransferPayment


هر زیرکلاس قوانین اعتبارسنجی مختص خود را پیاده‌سازی می‌کند


جدا کردن منطق پردازش پرداخت


اکنون PaymentProcessor بر جریان کار تمرکز دارد و وظایف زیر را تفویض می‌کند:


ایجاد آبجکت (مثلاً ساخت زیرکلاس صحیح Payment)


اعتبارسنجی (فراخوانی validatePayment() در زیرکلاس مربوطه)


لاگ‌گیری تراکنش و رسیدگی به Endpointهای API خارجی در یک محل متمرکز


معماری کلاس (Class Architecture)
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


Payment: یک کلاس انتزاعی که ویژگی‌های مشترک را تعریف کرده و متد validatePayment() را به‌صورت قراردادی ارائه می‌کند.


CreditCardPayment، DigitalWalletPayment، BankTransferPayment: کلاس‌های مشخصی که متد validatePayment() را برای نوع پرداخت خودشان پیاده‌سازی می‌کنند.


PaymentProcessor: از رشته‌ی paymentType برای ایجاد زیرکلاس صحیح استفاده می‌کند؛ قبل از ادامه، متد validatePayment() را فراخوانی می‌کند و سپس شبیه‌سازی API خارجی و لاگ‌گیری را انجام می‌دهد.


جزئیات پیاده‌سازی (Implementation Details)
1. کلاس انتزاعی Payment
/**
 * نماینده‌ی یک پرداخت کلی با ویژگی‌های پایه (مبلغ، ارز، اطلاعات مشتری و غیره).
 * زیرکلاس‌ها باید پیاده‌سازی مخصوص خود از validatePayment() را ارائه کنند.
 */
public abstract class Payment {
    protected double amount;
    protected String currency;
    protected Map<String, String> customerInfo;
    protected Map<String, String> paymentDetails;
    protected Date timestamp;

    /**
     * یک شیء Payment را با فیلدهای مشترک می‌سازد.
     *
     * @param amount مبلغ پرداخت
     * @param currency کد ارز (مثلاً USD, EUR, GBP)
     * @param customerInfo یک Map شامل اطلاعات مشتری (مثلاً name, email)
     * @param paymentDetails یک Map شامل داده‌های اختصاصی پرداخت (مثلاً card_number)
     */
    public Payment(double amount, String currency, Map<String, String> customerInfo, Map<String, String> paymentDetails) {
        this.amount = amount;
        this.currency = currency;
        this.customerInfo = customerInfo;
        this.paymentDetails = paymentDetails;
        this.timestamp = new Date();
    }

    /**
     * بر اساس قواعد تجاری، جزئیات پرداخت را اعتبارسنجی می‌کند.
     * زیرکلاس‌های مشخص باید این متد را override کرده و اعتبارسنجی مختص خود را ارائه دهند.
     *
     * @return true اگر پرداخت معتبر باشد، در غیر این صورت false
     */
    public abstract boolean validatePayment();
}

2. CreditCardPayment (زیرکلاس مشخص)
/**
 * منطق اعتبارسنجی مختص تراکنش‌های کارت اعتباری را مدیریت می‌کند.
 */
public class CreditCardPayment extends Payment {

    /**
     * شیء CreditCardPayment را می‌سازد.
     */
    public CreditCardPayment(double amount, String currency, Map<String, String> customerInfo, Map<String, String> paymentDetails) {
        super(amount, currency, customerInfo, paymentDetails);
    }

    /**
     * جزئیات کارت اعتباری را اعتبارسنجی می‌کند:
     * - مبلغ باید مثبت باشد
     * - ارز باید یکی از USD, EUR, یا GBP باشد
     * - اطلاعات مشتری باید شامل email باشد
     * - طول شماره کارت باید دست‌کم ۱۲ کاراکتر باشد
     */
    @Override
    public boolean validatePayment() {
        if (amount <= 0) return false;
        if (!currency.matches("USD|EUR|GBP")) return false;
        if (!customerInfo.containsKey("email")) return false;
        return paymentDetails.getOrDefault("card_number", "").length() >= 12;
    }
}

3. DigitalWalletPayment (زیرکلاس مشخص)
/**
 * منطق اعتبارسنجی مختص تراکنش‌های کیف پول دیجیتال را مدیریت می‌کند.
 */
public class DigitalWalletPayment extends Payment {

    /**
     * شیء DigitalWalletPayment را می‌سازد.
     */
    public DigitalWalletPayment(double amount, String currency, Map<String, String> customerInfo, Map<String, String> paymentDetails) {
        super(amount, currency, customerInfo, paymentDetails);
    }

    /**
     * جزئیات کیف پول دیجیتال را اعتبارسنجی می‌کند:
     * - مبلغ باید مثبت باشد
     * - ارز باید یکی از USD, EUR, یا GBP باشد
     * - اطلاعات مشتری باید شامل email باشد
     * - باید شامل فیلد 'wallet_id' باشد
     */
    @Override
    public boolean validatePayment() {
        if (amount <= 0) return false;
        if (!currency.matches("USD|EUR|GBP")) return false;
        if (!customerInfo.containsKey("email")) return false;
        return paymentDetails.containsKey("wallet_id");
    }
}

4. BankTransferPayment (زیرکلاس مشخص)
/**
 * منطق اعتبارسنجی مختص تراکنش‌های انتقال بانکی را مدیریت می‌کند.
 */
public class BankTransferPayment extends Payment {

    /**
     * شیء BankTransferPayment را می‌سازد.
     */
    public BankTransferPayment(double amount, String currency, Map<String, String> customerInfo, Map<String, String> paymentDetails) {
        super(amount, currency, customerInfo, paymentDetails);
    }

    /**
     * جزئیات انتقال بانکی را اعتبارسنجی می‌کند:
     * - مبلغ باید مثبت باشد
     * - ارز باید یکی از USD, EUR, یا GBP باشد
     * - اطلاعات مشتری باید شامل email باشد
     * - باید شامل فیلد 'account_number' باشد
     */
    @Override
    public boolean validatePayment() {
        if (amount <= 0) return false;
        if (!currency.matches("USD|EUR|GBP")) return false;
        if (!customerInfo.containsKey("email")) return false;
        return paymentDetails.containsKey("account_number");
    }
}

5. کلاس PaymentProcessor (بازآرایی‌شده)
/**
 * روند پرداخت را سازماندهی می‌کند؛ از جمله انتخاب زیرکلاس صحیح از Payment،
 * اعتبارسنجی آن، و سپس پردازش و لاگ‌گیری تراکنش.
 */
public class PaymentProcessor {
    private Map<String, String> config;

    /**
     * شیء PaymentProcessor را با جزئیات پیکربندی (مانند مسیرهای endpoint) می‌سازد.
     *
     * @param config یک Map که کلیدهای آن مربوط به endpointهای انواع پرداخت است
     *               (مثلاً "credit_card_endpoint")
     */
    public PaymentProcessor(Map<String, String> config) {
        this.config = config;
    }

    /**
     * متد سطح بالا برای پردازش یک پرداخت بر اساس نوع پرداخت (paymentType).
     *
     * @param paymentType    نوع پرداخت (مثلاً "credit_card")
     * @param amount         مبلغ پرداخت
     * @param currency       کد ارز (مثلاً "USD")
     * @param customerInfo   شامل فیلدهای مختص مشتری مانند "email" یا "name"
     * @param paymentDetails فیلدهای اختصاصی نوع پرداخت، مانند "card_number"
     * @return یک Map شامل وضعیت و transaction ID در صورت موفقیت، در غیر این‌صورت پیام خطا
     */
    public Map<String, String> processPayment(String paymentType, double amount, String currency,
                                              Map<String, String> customerInfo, Map<String, String> paymentDetails) {
        // ساخت زیرکلاس مناسب از Payment
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

        // اعتبارسنجی پرداخت انتخاب‌شده
        if (!payment.validatePayment()) {
            return Map.of("status", "failed", "message", "Validation error");
        }

        // اجرای جریان پرداخت و لاگ‌گیری تراکنش
        Map<String, String> result = handleProcessing(paymentType, amount, currency, customerInfo, paymentDetails);
        logTransaction(paymentType, amount, currency, customerInfo, paymentDetails, result);
        return result;
    }

    /**
     * شبیه‌سازی اتصال به Endpoint خارجی API و تولید یک transaction ID.
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
     * جزئیات تراکنش پرداخت را در کنسول لاگ می‌کند
     * (می‌توان این بخش را برای استفاده از سیستم‌های لاگ‌گیری واقعی گسترش داد).
     */
    private void logTransaction(String paymentType, double amount, String currency,
                                Map<String, String> customerInfo, Map<String, String> paymentDetails,
                                Map<String, String> result) {
        String logEntry = String.format("%s - %s payment of %.2f %s for %s: %s",
                new Date(), paymentType, amount, currency, customerInfo.get("name"), result);
        System.out.println("LOG: " + logEntry);
    }
}

6. نمونه استفاده در کلاس Main
public class Main {
    public static void main(String[] args) {
        // یک Map پیکربندی که مسیرهای endpoint را برای هر نوع پرداخت مشخص می‌کند
        Map<String, String> config = Map.of(
                "credit_card_endpoint", "https://api.creditcard.com/process",
                "digital_wallet_endpoint", "https://api.digitalwallet.com/process",
                "bank_transfer_endpoint", "https://api.banktransfer.com/process"
        );

        // ساخت شیء PaymentProcessor با این تنظیمات
        PaymentProcessor processor = new PaymentProcessor(config);

        // اطلاعات نمونه کاربر و جزئیات پرداخت
        Map<String, String> customer = Map.of("name", "John Doe", "email", "john@example.com");
        Map<String, String> paymentDetails = Map.of("card_number", "123456789012", "expiry", "12/25", "cvv", "123");

        // پردازش یک پرداخت کارت اعتباری
        Map<String, String> result = processor.processPayment("credit_card", 100, "USD", customer, paymentDetails);
        System.out.println("Final Result: " + result);
    }
}

نحوه اجرا (How to Run)
کامپایل کد


اطمینان حاصل کنید جاوای 8+ و یک IDE سازگار (مانند IntelliJ یا Eclipse) در اختیار دارید.


اجرای کلاس Main


متد main در فایل Main.java نشان می‌دهد که چگونه پیکربندی را انجام دهید و یک پرداخت را پردازش کنید.


مشاهده خروجی در کنسول


در کنسول، گزارش‌های مربوط به اتصال به API، پردازش تراکنش و وضعیت نهایی را مشاهده خواهید کرد.


مزایای کلیدی این بازآرایی (Key Benefits of This Refactoring)
جدا کردن وظایف (Separation of Concerns): منطق اعتبارسنجی اکنون در هر زیرکلاس قرار دارد و از پیچیدگی در PaymentProcessor کاسته شده است.


قابلیت گسترش (Extensibility): برای افزودن نوع پرداخت جدید (مثلاً CryptoPayment)، تنها کافی است یک زیرکلاس جدید از Payment بسازید و در یک مکان (switch در PaymentProcessor) به‌روزرسانی انجام دهید.


نگه‌داری آسان (Maintainability): هر زیرکلاس پرداخت را می‌توان به‌صورت مستقل تست و به‌روزرسانی کرد.


مراحل بعدی (Next Steps)
در مراحل آینده، ما:
نیاز به switch در PaymentProcessor را با معرفی چندریختی (Polymorphism) یا الگوی Factory (مرحله ۳) حذف خواهیم کرد.


یک واسط PaymentGateway برای ادغام با سرویس‌های شخص ثالث پیاده‌سازی می‌کنیم.


تزریق وابستگی (Dependency Injection) و مدیریت پیکربندی بیرونی را یکپارچه می‌کنیم (مرحله ۴).



چک‌لیست کامیت برای مرحله ۲
تمام کلاس‌های جدید (Payment, CreditCardPayment, DigitalWalletPayment, BankTransferPayment)


بازآرایی PaymentProcessor و تفویض وظایف به زیرکلاس‌های Payment


افزودن توضیحات درون‌کدی و Javadoc برای هر کلاس


فایل README (این مستند) با توضیح طراحی و شیوه استفاده


پیام پیشنهادی برای کامیت:
"Stage 2: Added Payment abstraction and concrete subclasses"


این مستندات، مرحله ۲ را تکمیل می‌کند. اکنون کد شما به شکل دقیق‌تری از اصول نهان‌سازی (Encapsulation) و انتزاع (Abstraction) پیروی می‌کند و پایه‌ای مستحکم برای بهبودهای منطبق بر SOLID در مراحل بعدی فراهم شده است.





