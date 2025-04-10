# OOP_introduction


## تعریف problem

### کد فعلی چه ساختاری دارد و چگونه کار می کند ؟

کلاس جاوای ارائه شده، PaymentProcessor، شبیه‌سازی پایه‌ای از پردازش سه نوع پرداخت را پیاده‌سازی می‌کند: کارت اعتباری، کیف پول دیجیتال و انتقال بانکی. این کلاس از یک متد مرکزی به نام processPayment() استفاده می‌کند که ابتدا پارامترهای ورودی—مانند مقدار، ارز و اطلاعات خاص مشتری/پرداخت—را اعتبارسنجی می‌کند، سپس با استفاده از یک دستور switch برای فراخوانی تابع پردازش مناسب بر اساس نوع پرداخت branching می‌کند. هر روش پرداخت شبیه‌سازی تعامل با یک API خارجی است و یک شناسه تراکنش بر اساس زمان‌سنجی جاری تولید می‌کند. علاوه بر این، کلاس شامل قابلیت ثبت لاگ و متد main است که اجرای نمونه‌ای را با استفاده از پیکربندی ساختگی و داده‌های آزمایشی نشان می‌دهد. در حالی که کد از نظر عملکردی صحیح و ساختار آن واضح است، مسئولیت‌های متعددی—مانند اعتبارسنجی، ثبت لاگ و شبیه‌سازی API—در داخل یک کلاس واحد به طور فشرده ترکیب شده است، که این موضوع باعث می‌شود تست، مقیاس‌پذیری و نگهداری آن در طول زمان دشوار شود.
ابتدا بعضی از مشکلات کد را بررسی کرده و بعد به اصول solide که violate شده اند با جزییات میپردازیم

## code smells

 1. نام کلاس: PaymentProcessor

```java
public class PaymentProcessor {
```

این کلاس بیشتر از پردازش payment ها انجام می‌دهد و شامل اعتبارسنجی، ثبت لاگ و منطق برای انواع مختلف پرداخت است که با اصل "یک کار انجام بده" مغایرت دارد.

 2. نام متد: processPayment

```java
public Map<String, String> processPayment(...)
```

نام متد عمومی است و نشان نمی‌دهد که مسیریابی، اعتبارسنجی و ثبت لاگ انجام می‌دهد. این یک "Long Method" و "Divergent Change" است که باعث شکنندگی می‌شود.

 3. لیست پارامترهای متد: processPayment(...)

```java
public Map<String, String> processPayment(String paymentType, double amount, String currency,
                                          Map<String, String> customerInfo, Map<String, String> paymentDetails)
```

پارامترهای زیاد و استفاده از `Map<String, String>`اعث پیچیدگی و سختی در نگهداری می‌شود.

یک "Long Parameter List" است که به مدل‌های داده‌ای بهتر نیاز دارد.

 4. استفاده از Map خام Map <String, String>

```java
Map<String, String> customerInfo, Map<String, String> paymentDetails
```

استفاده از `Map`‌های خام بدون ساختار خاص موجب احتمال خطا و نقص ایمنی نوع می‌شود. این یک "Primitive Obsession" است و بهتر است داده‌ها در اشیاء کپسوله شوند.

 5. رشته‌های سخت‌کد شده برای انواع پرداخت‌ها

```java
switch (paymentType) {
    case "credit_card":
    case "digital_wallet":
    case "bank_transfer":
```

این رشته‌ها سخت‌کد شده‌اند و در صورت استفاده مجدد شکننده می‌شوند.

این یک "Magic String" است که استفاده از `enum`‌ها می‌تواند بهتر باشد.

 6. کد تکراری در processCreditCard، processDigitalWallet و processBankTransfer  
نمونه کد:

```java
System.out.println("Connecting to [API] at " + config.get("[endpoint]"));
String transactionId = "[Prefix]" + new Date().getTime();
System.out.println("Processing [type] payment for " + customerInfo.get("name"));
```

این سه متد مشابه هم هستند و فقط تفاوت‌های جزئی دارند.

این یک "Duplicate Code" است که باعث بالا رفتن هزینه‌های نگهداری و احتمال خطا می‌شود.

 7. ترکیب مسئولیت‌ها: ثبت لاگ در داخل منطق کسب‌وکار

```java
System.out.println("Processing credit card payment for " + customerInfo.get("name"));
...
System.out.println("LOG: " + logEntry);
```

چاپ لاگ‌ها به منطق کسب‌وکار وابسته است که باعث می‌شود در صورت تغییر رفتار لاگ، نیاز به تغییر متدهای زیادی باشد. این یک "Shotgun Surgery" است و باعث "Low Cohesion" می‌شود.

 8. متد validatePayment – انفجار منطق شرطی

```java
switch (paymentType) {
    case "credit_card":
        ...
    case "digital_wallet":
        ...
    case "bank_transfer":
        ...
```

استفاده از دستورات شرطی برای اعتبارسنجی که با اضافه شدن انواع جدید پیچیده‌تر می‌شود. این یک "Conditional Complexity" و "Feature Envy" است که نیاز به الگوی `Strategy` یا پلی‌مورفیسم دارد.

 9. تولید timestamp: new Date().getTime()

```java
String transactionId = "CC" + new Date().getTime();
```

روش تولید شناسه تراکنش تکراری و ساده‌انگارانه است.

این یک "Inappropriate Intimacy" + "Duplicated Code" است که با استخراج یک متد مشترک قابل بهبود است.

 10. نوع بازگشتی تمام متدهای پرداخت: Map<String, String>

```java
return Map.of("status", "success", "transaction_id", transactionId);
```

استفاده از `Map`‌های خام برای پاسخ ساختاریافته باعث کاهش وضوح می‌شود. یک "Primitive Obsession" است و استفاده از کلاس‌های مشخص مانند `PaymentResult` یا `TransactionResult` بهتر خواهد بود.

## SOLID Principlas violations :

### تحلیل تفصیلی تخلفات و اصلاحات اصول SOLID در کد `PaymentProcessor`:

---

### 1. **تخلف از اصل مسئولیت واحد (SRP)**

**مسئله:**

- کلاس `PaymentProcessor` مسئولیت‌های متعددی را انجام می‌دهد: اعتبارسنجی پرداخت، انتخاب روش پرداخت، پردازش تراکنش و ثبت لاگ تراکنش.
- این مسئولیت‌ها از یکدیگر مستقل هستند، به این معنی که ممکن است هر کدام به دلایل مختلف تغییر کنند (مثلاً ثبت لاگ ممکن است به دلیل نیاز به حسابرسی تغییر کند، پردازش پرداخت ممکن است به دلیل قوانین جدید تجاری تغییر کند).

**راه‌حل:**

- کلاس را به چندین کلاس کوچکتر و تخصصی تقسیم کنید:
  - **InputValidator**: مسئول اعتبارسنجی پرداخت.
  - **PaymentMethodChooser**: مسئول انتخاب روش پرداخت.
  - **TransactionLogger**: مسئول ثبت لاگ تراکنش.
  - **PaymentHandler**: مسئول پردازش پرداخت‌ها.

این تفکیک اطمینان می‌دهد که هر کلاس یک دلیل برای تغییر دارد و کد قابل نگهداری و آزمایش‌پذیرتر می‌شود.

**توضیح:**

- با تفکیک مسئولیت‌ها، هر جزء روی یک جنبه از منطق تمرکز می‌کند. این با اصل SRP هماهنگ است و تغییر یا گسترش قسمت‌های مختلف سیستم را بدون تأثیرگذاری بر سایر بخش‌ها آسان‌تر می‌کند.

---

### 2. **تخلف از اصل باز و بسته (OCP)**

**مسئله:**

- متد `processPayment` از دستور `switch` برای پردازش انواع مختلف پرداخت استفاده می‌کند. اگر یک روش پرداخت جدید (مثلاً پرداخت‌های کریپتو) اضافه شود، متد `processPayment` باید تغییر کند.
- این تخلف از اصل **باز و بسته** است زیرا کلاس برای تغییرات بسته نیست و باید برای افزودن ویژگی‌ها (اضافه کردن روش‌های پرداخت جدید) تغییر کند.

**راه‌حل:**

- از پلی‌مورفیسم استفاده کنید تا کلاس برای گسترش باز و برای تغییر بسته باشد.
- یک واسط انتزاعی یا کلاس پایه (`PaymentProcessor` trait) با متد `process` ایجاد کنید.
- پردازشگرهای پرداخت خاص برای هر نوع پرداخت ایجاد کنید، مانند `CreditCardProcessor`، `DigitalWalletProcessor`، و `BankTransferProcessor`.
- متد `processPayment` باید به پردازشگر مناسب ارجاع دهد بدون اینکه برای افزودن روش پرداخت جدید نیاز به تغییر داشته باشد.

```java
interface PaymentProcessor {
    Map<String, String> processPayment(double amount, String currency, Map<String, String> customerInfo, Map<String, String> paymentDetails);
}

class CreditCardProcessor implements PaymentProcessor {
    public Map<String, String> processPayment(double amount, String currency, Map<String, String> customerInfo, Map<String, String> paymentDetails) {
        // Logic for credit card processing
    }
}
```

**توضیح:**

- این اطمینان می‌دهد که افزودن یک روش پرداخت جدید نیاز به تغییر در متد `processPayment` ندارد و بدین ترتیب با اصل OCP هماهنگ است.

---

### 3. **تخلف از اصل جانشینی لیسکوف (LSP)**

**مسئله:**

- متدهای `processCreditCard`، `processDigitalWallet` و `processBankTransfer` مشابه هم هستند اما تحت یک واسط مشترک قرار نگرفته‌اند. این باعث می‌شود که جایگزینی یکی از آن‌ها با دیگری بدون تغییر در منطق موجود دشوار شود.

**راه‌حل:**

- یک واسط مشترک به نام `PaymentProcessor` تعریف کنید که تمام روش‌های پرداخت آن را پیاده‌سازی کنند. هر کلاس (مثلاً `CreditCardProcessor`، `DigitalWalletProcessor`) باید این واسط را پیاده‌سازی کند و منطق پردازش پرداخت خاص خود را فراهم کند.

```java
interface PaymentProcessor {
    Map<String, String> processPayment(double amount, String currency, Map<String, String> customerInfo, Map<String, String> paymentDetails);
}
```

**توضیح:**

- این اطمینان می‌دهد که هر `PaymentProcessor` را می‌توان در کد جایگزین کرد و رفتار ثابت می‌ماند، که با اصل LSP هماهنگ است.

---

### 4. **تخلف از اصل جداسازی واسط (ISP)**

**مسئله:**

- کلاس به‌طور مستقیم یک واسط پیاده‌سازی نمی‌کند، اما اگر بخواهیم یک واسط بسازیم، این واسط یک "واسط بزرگ و چرب" خواهد بود که شامل متدهای اعتبارسنجی، ثبت لاگ، پردازش پرداخت و انتخاب روش است.
- برخی کلاس‌ها ممکن است فقط نیاز به زیرمجموعه‌ای از این عملیات‌ها داشته باشند و مجبور کردن آنها به پیاده‌سازی متدهای غیر ضروری، تخلف از اصل ISP است.

**راه‌حل:**

- واسط را به واسط‌های کوچکتر و تخصصی تقسیم کنید:
  - **Validatable**: مسئول اعتبارسنجی ورودی‌ها.
  - **Loggable**: مسئول ثبت تراکنش‌ها.
  - **Processable**: مسئول پردازش منطق پردازش پرداخت.
  - **MethodChoosable**: مسئول انتخاب روش پرداخت.

```java
interface Validatable {
    boolean validate(Map<String, String> paymentDetails);
}

interface Loggable {
    void log(Map<String, String> transactionDetails);
}

interface Processable {
    Map<String, String> process(Map<String, String> paymentDetails);
}
```

**توضیح:**

- این اطمینان می‌دهد که کلاس‌ها فقط واسط‌هایی را پیاده‌سازی می‌کنند که به آنها نیاز دارند و از متدهای خالی یا غیر ضروری جلوگیری می‌شود. این به کلاس‌ها کمک می‌کند تا متمرکز بمانند و با اصل ISP هم‌راستا باشد.

---

### 5. **تخلف از اصل معکوس وابستگی (DIP)**

**مسئله:**

- کلاس `PaymentProcessor` به‌طور محکم به پیاده‌سازی‌های خاص پردازش پرداخت (مانند `CreditCardProcessor`، `DigitalWalletProcessor` و غیره) وابسته است.
- ماژول‌های سطح بالا مانند `PaymentProcessor` نباید به پیاده‌سازی‌های سطح پایین وابسته باشند، بلکه باید به انتزاع‌ها (واسط‌ها) وابسته باشند.

**راه‌حل:**

- به‌جای ایجاد مستقیم `CreditCardProcessor` یا `DigitalWalletProcessor`، کلاس `PaymentProcessor` باید به واسط `PaymentProcessor` وابسته باشد و پیاده‌سازی‌های خاص از طریق وابستگی (از طریق سازنده یا فریمورک تزریق وابستگی) وارد شوند.

```java
class PaymentProcessor {
    private final PaymentProcessor creditCardProcessor;
    private final PaymentProcessor digitalWalletProcessor;
    private final PaymentProcessor bankTransferProcessor;

    public PaymentProcessor(PaymentProcessor creditCardProcessor, PaymentProcessor digitalWalletProcessor, PaymentProcessor bankTransferProcessor) {
        this.creditCardProcessor = creditCardProcessor;
        this.digitalWalletProcessor = digitalWalletProcessor;
        this.bankTransferProcessor = bankTransferProcessor;
    }

    public Map<String, String> processPayment(String paymentType, double amount, String currency, Map<String, String> customerInfo, Map<String, String> paymentDetails) {
        switch (paymentType) {
            case "credit_card":
                return creditCardProcessor.processPayment(amount, currency, customerInfo, paymentDetails);
            case "digital_wallet":
                return digitalWalletProcessor.processPayment(amount, currency, customerInfo, paymentDetails);
            case "bank_transfer":
                return bankTransferProcessor.processPayment(amount, currency, customerInfo, paymentDetails);
            default:
                return Map.of("status", "failed", "message", "Unknown payment type");
        }
    }
}
```

**توضیح:**

- با تزریق وابستگی‌ها از طریق سازنده یا فریمورک DI، کلاس `PaymentProcessor` از پیاده‌سازی‌های خاص جدا می‌شود و به اصل DIP پایبند می‌ماند.

---

### **خلاصه تغییرات:**

| اصل     | مسئله شناسایی‌شده                                                      | اصلاح پیشنهادی                                                                                            |
| ------- | ---------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------- |
| **SRP** | پردازش، اعتبارسنجی، انتخاب روش و ثبت لاگ                               | تقسیم به کلاس‌های سرویس جداگانه (InputValidator، PaymentMethodChooser، TransactionLogger، PaymentHandler) |
| **OCP** | نیاز به تغییر برای هر روش پرداخت جدید                                  | استفاده از پلی‌مورفیسم (واسط `PaymentProcessor`) و گسترش آن با پیاده‌سازی‌های خاص                         |
| **LSP** | متدهای `processCreditCard`، `processDigitalWallet` و غیره مشترک نیستند | ایجاد یک واسط مشترک `PaymentProcessor` برای یکپارچه‌سازی آنها                                             |
| **ISP** | یک واسط بزرگ که متدهای غیر ضروری را پیاده‌سازی می‌کند                  | تقسیم به واسط‌های متمرکز مانند `Validatable`، `Loggable`، `Processable`                                   |
| **DIP** | وابستگی محکم به کلاس‌های سطح پایین                                     | تزریق وابستگی‌ها (استفاده از واسط‌ها برای روش‌های پرداخت)                                                 |

با اعمال این اصول SOLID، کد انعطاف‌پذیرتر، قابل نگهداری‌تر و قابل گسترش‌تر می‌شود و مسئولیت‌ها به‌طور واضح جدا می‌شوند.

