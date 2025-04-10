در ادامه یک نمونه مستندات `README.md` برای **مرحله ۴ (Stage 4)** ارائه شده است. این مستند توضیح می‌دهد که چگونه **تزریق وابستگی (Dependency Injection)** و **مدیریت پیکربندی (Configuration Management)** در سیستم اعمال شده‌اند و منعکس‌کننده‌ی رویکرد کدنویسی اشاره‌شده در پیام قبلی است. بر اساس استانداردهای پروژه‌ی خود، می‌توانید ساختار، نام‌گذاری و قالب‌بندی را سفارشی کنید.

---

# مرحله ۴: بازآرایی پیشرفته و یکپارچه‌سازی (Advanced Refactoring & Integration)

## مرور کلی (Overview)

در **مرحله ۴**، سیستم پرداخت خود را با تمرکز بر دو بهبود عمده گسترش می‌دهیم:

1. **تزریق وابستگی (Dependency Injection - DI)**  
   اطمینان از این‌که کلاس‌های سطح بالای ما (مانند `PaymentProcessor`) وابستگی‌هایشان (در اینجا، اشیای `PaymentGateway`) را از یک سازوکار خارجی دریافت می‌کنند، به‌جای این‌که خودشان به‌صورت سخت‌کد شده یا با ساخت مستقیم بسازند.

2. **مدیریت پیکربندی (Configuration Management)**  
   بارگیری جزئیات حساس یا وابسته به محیط (مانند کلیدهای API، آدرس‌های Endpoints) از منابع خارجی مانند متغیرهای محیطی یا فایل‌های properties. این کار، پیکربندی را از کد منبع جدا می‌کند و استقرار در محیط‌های مختلف (Dev, Staging, Production) را ساده‌تر می‌سازد.

با اتمام این مرحله، ما با **اصل معکوس وابستگی (Dependency Inversion Principle)** همخوانی بیشتری پیدا می‌کنیم و اطمینان می‌یابیم که طراحی ما **ماژولار** و **قابل نگه‌داری** است.

---

## اهداف مرحله ۴ (Goals for Stage 4)

1. **جدا کردن وابستگی‌های خارجی (Isolate External Dependencies)**  
   - کلاس‌های درگاه پرداخت (`StripeGateway`, `PayPalGateway`) نباید به مقادیر جاسازی‌شده در کد وابسته باشند. در عوض، پیکربندی لازم (مثلاً Endpoints, API Keys) را از طریق پارامترهای تزریق‌شده یا یک مدیر پیکربندی (Configuration Manager) دریافت می‌کنند.

2. **بهبود مدیریت پیکربندی (Improve Configuration Management)**  
   - یک یا چند پیاده‌سازی از واسط `ConfigurationManager` فراهم کنید که بتواند جزئیات پیکربندی را از موارد زیر بارگیری کند:
     - متغیرهای محیطی (مثلاً `EnvConfigurationManager`)
     - فایل‌های `.properties` (مثلاً `FileConfigurationManager`)
   - هر `PaymentGateway` می‌تواند از مقادیر بارگذاری‌شده (مانند `endpoint` و `apiKey`) استفاده کند و این امکان را فراهم می‌آورد که استقرارها هم امن باشند و هم منعطف.

3. **مرزبندی شفاف ماژول‌ها (Clear Module Boundaries)**  
   - **پیکربندی (Configuration)** در ماژول ویژه‌ای (`com.example.config`) مدیریت می‌شود.  
   - **ادغام با درگاه پرداخت (Gateway Integration)** در ماژول ویژه‌ای (`com.example.gateway`) صورت می‌گیرد.  
   - **منطق دامنه (Domain Logic)** در کلاس‌های `Payment` (`com.example.payment`) باقی می‌ماند.  
   - **هماهنگی (Orchestration)** توسط `PaymentProcessor` (`com.example.processor`) انجام می‌شود، که از تزریق سازنده (Constructor Injection) برای دریافت `PaymentGateway` استفاده می‌کند.


## کلاس‌ها و واسط‌های کلیدی (Key Classes & Interfaces)

1. **ConfigurationManager**  
   - واسطی که متد `getConfig(String serviceName)` را تعریف می‌کند → یک Map از کلید/مقدارهای پیکربندی باز می‌گرداند.  
   - **EnvConfigurationManager** – بارگیری پیکربندی از متغیرهای محیطی (مثلاً `STRIPE_ENDPOINT`, `PAYPAL_ENDPOINT`).  
   - **FileConfigurationManager** – (اختیاری) بارگیری از یک فایل `.properties` محلی.

2. **GatewayFactory**  
   - یک کلاس فکتوری که از `ConfigurationManager` برای ساخت نمونه‌ی مناسب `PaymentGateway` بر اساس نام ورودی (مثلاً `"stripe"`, `"paypal"`) استفاده می‌کند.  
   - این ساختار تضمین می‌کند که در متد `Main` به‌صورت دستی درگاه‌ها را نمونه‌سازی نکنیم.

3. **PaymentGateway**  
   - واسط تعریف‌شده در مرحله ۳، با متدهای `processPayment()`, `refundPayment()`, و `getTransactionStatus()`.  
   - **StripeGateway** و **PayPalGateway** – از داده‌های پیکربندی که توسط `GatewayFactory` داده می‌شود استفاده می‌کنند تا Endpointها، کلیدهای API و... را تنظیم کنند.

4. **PaymentProcessor**  
   - یک شیء از جنس `PaymentGateway` را در سازنده می‌پذیرد.  
   - آبجکت `Payment` را اعتبارسنجی کرده و سپس عملیات پرداخت را به درگاه تزریق‌شده تفویض می‌کند.

5. **Main**  
   - نشان می‌دهد چگونه همه‌چیز را کنار هم قرار دهیم:  
     - انتخاب یک `ConfigurationManager`.  
     - ساخت یک `GatewayFactory`.  
     - ایجاد درگاه پرداخت مورد نظر (`PaymentGateway`).  
     - نمونه‌سازی `PaymentProcessor` با آن درگاه.  
     - ساخت یک آبجکت `Payment` (مثلاً `CreditCardPayment`) و پردازش آن.

---

## نمونه استفاده (Example Usage) - کد نمایشی (Pseudocode)

```java
// 1) انتخاب Configuration Manager
ConfigurationManager configManager = new EnvConfigurationManager();
// یا می‌توانید استفاده کنید از:
// ConfigurationManager configManager = new FileConfigurationManager("app.properties");

// 2) ساخت GatewayFactory
GatewayFactory factory = new GatewayFactory(configManager);

// 3) ایجاد یک PaymentGateway براساس نام
PaymentGateway gateway = factory.createGateway("stripe");

// 4) تزریق درگاه به PaymentProcessor
PaymentProcessor processor = new PaymentProcessor(gateway);

// 5) ساخت یک زیرکلاس از Payment (از مرحله ۲)
Payment payment = new CreditCardPayment(100, "USD", customerInfo, paymentDetails);

// 6) پردازش Payment
Map<String, String> result = processor.processPayment(payment);

// 7) رافع یا گرفتن وضعیت در صورت نیاز
processor.refundPayment(result.get("transaction_id"), 50.0);
String status = processor.getTransactionStatus(result.get("transaction_id"));
```

---

## مزایا (Benefits)

- **ماژولار بودن و انعطاف‌پذیری (Modularity & Flexibility)**  
  - تغییر از Stripe به PayPal یا افزودن یک درگاه جدید (مثلاً `CryptoGateway`) تنها به تغییرات مختصر در کد نیاز دارد—ایجاد یک کلاس جدید پیاده‌کننده‌ی `PaymentGateway` و به‌روزرسانی فکتوری در صورت نیاز.

- **امنیت و مقیاس‌پذیری (Security & Scalability)**  
  - کلیدهای API و Endpointها دیگر در کد منبع ذخیره نمی‌شوند و در نتیجه ریسک کاهش می‌یابد. محیط‌های مختلف (Dev, Test, Prod) می‌توانند مقادیر متفاوتی را بارگیری کنند.

- **اصل معکوس وابستگی و معماری تمیز (Dependency Inversion & Clean Architecture)**  
  - ماژول‌های سطح بالا (`PaymentProcessor`) به انتزاع‌ها (`PaymentGateway`, `ConfigurationManager`) وابسته هستند، نه به پیاده‌سازی‌های مشخص.

---

## فایل‌های کد به‌روزشده در مرحله ۴ (Code Files Updated in Stage 4)

1. **`EnvConfigurationManager`** یا **`FileConfigurationManager`**  
   - پیکربندی را از متغیرهای محیطی یا فایل‌های مبتنی بر properties بارگیری می‌کند.  
   - ارجاعات سخت‌کد شده به Endpoint یا کلیدهای API در کلاس‌های درگاه را حذف می‌کند.

2. **`GatewayFactory`**  
   - از `ConfigurationManager` مقداردهی شده استفاده می‌کند تا درگاه مناسب را بسازد.  
   - نیاز به ایجاد اشیای درگاه به‌صورت پراکنده در کد (مثلاً فراخوانی `new StripeGateway(...)`) را از بین می‌برد.

3. **`PaymentGateway`** (واسط بدون تغییر، اما اکنون کلاس‌های پیاده‌کننده مقادیر را از پیکربندی تزریق‌شده می‌گیرند.)

4. **`PaymentProcessor`** (نسبت به مرحله ۳ تغییر عمده‌ای نکرده، اما اطمینان حاصل می‌کند که دیگر هیچ ارجاعی به تنظیمات خاص درگاه باقی نمانده باشد.)

5. **`Main`**  
   - نشان می‌دهد چگونه یک `ConfigurationManager` را انتخاب کرده و درگاه را بدون ارجاعات مستقیم به پارامترهای پیکربندی بسازیم.

---

## نحوه اجرا (How to Run)

1. **راه‌اندازی پیکربندی (Set Up Configuration)**  
   - اگر از متغیرهای محیطی استفاده می‌کنید، مطمئن شوید متغیرهایی نظیر `STRIPE_ENDPOINT`, `STRIPE_API_KEY`, `PAYPAL_ENDPOINT` و غیره تعریف شده‌اند.  
   - یا اگر رویکرد فایل properties را ترجیح می‌دهید، مقادیری مانند `stripe.endpoint`, `stripe.apiKey`, `paypal.endpoint` و... را در فایل `.properties` بگنجانید.

2. **کامپایل و اجرا**  
   - `javac -d out ./com/example/**/*.java` (مسیرها را بسته به ساختار خود اصلاح کنید)  
   - `java -cp out com.example.Main`

3. **مشاهده خروجی (Observe Output)**  
   - در کنسول می‌توانید Endpoint درگاه انتخابی، شناسه تراکنش تولیدشده، و اطلاعات مربوط به بازگشت وجه (Refund) یا وضعیت تراکنش را مشاهده کنید.

---

## چک‌لیست کامیت (Commit Checklist)

- [x] **ConfigurationManager** و دست‌کم یک پیاده‌سازی مشخص (`EnvConfigurationManager` یا `FileConfigurationManager`).  
- [x] **GatewayFactory** برای ساخت اشیای درگاه با استفاده از داده‌های پیکربندی.  
- [x] **PaymentProcessor** دیگر از هیچ مقدار پیکربندی سخت‌کدشده استفاده نمی‌کند.  
- [x] **Main** گردش تزریق وابستگی را نشان می‌دهد (دیگر هیچ فراخوانی مستقیم `new StripeGateway(...)` با پارامترهای سخت‌کدشده وجود ندارد).

**پیام پیشنهادی برای کامیت**:

```
"Stage 4: Applied dependency injection and externalized configuration."
```

---

**تبریک!** شما مرحله ۴ را تکمیل کرده‌اید و رویکردی بالغ، قابل‌گسترش، و مناسب محیط تولید برای پردازش پرداخت ایجاد کرده‌اید؛ چرا که پیکربندی و وابستگی به درگاه خارجی را به‌طور کامل از منطق اصلی پرداخت جدا کرده‌اید.
