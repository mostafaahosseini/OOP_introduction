# مرحله ۴: بازآرایی پیشرفته و یکپارچه‌سازی (Advanced Refactoring & Integration)

## مرور کلی (Overview)

در **مرحله ۴**، سیستم پرداخت خود را با تمرکز بر دو بهبود عمده گسترش می‌دهیم:

1. **تزریق وابستگی (Dependency Injection - DI)**  
   اطمینان از این‌که کلاس‌های سطح بالای ما (مانند `PaymentProcessor`) وابستگی‌هایشان (در اینجا، اشیای `PaymentGateway`) را از یک سازوکار خارجی دریافت می‌کنند، به‌جای ساخت مستقیم یا استفاده از مقادیر سخت‌کد شده.

2. **مدیریت پیکربندی (Configuration Management)**  
   بارگیری جزئیات حساس یا وابسته به محیط (مانند کلیدهای API، آدرس‌های Endpoints) از منابع خارجی مانند متغیرهای محیطی یا فایل‌های properties. این امر پیکربندی را از کد منبع جدا کرده و امکان استقرار آسان‌تر در محیط‌های مختلف (Dev, Staging, Production) را فراهم می‌آورد.

این مرحله باعث بهبود انطباق با **اصل معکوس وابستگی (Dependency Inversion Principle)** می‌شود و طراحی سیستم را **ماژولار** و **قابل نگه‌داری** می‌سازد.

---

## اهداف مرحله ۴ (Goals for Stage 4)

1. **جدا کردن وابستگی‌های خارجی (Isolate External Dependencies)**
   - کلاس‌های درگاه پرداخت مانند `StripeGateway` و `PayPalGateway` نباید به مقادیر ثابت در کد وابسته باشند. در عوض، اطلاعات پیکربندی لازم مانند Endpoints و API Keys باید از طریق تزریق وابستگی یا یک مدیر پیکربندی دریافت شود.

2. **بهبود مدیریت پیکربندی (Improve Configuration Management)**
   - پیاده‌سازی یک یا چند واسط برای مدیریت پیکربندی به‌منظور بارگذاری تنظیمات از منابعی مانند:
     - متغیرهای محیطی (مثلاً `EnvConfigurationManager`)
     - فایل‌های `.properties` (مثلاً `FileConfigurationManager`)
   - فراهم آوردن قابلیت استفاده مجدد از مقادیر پیکربندی در کلاس‌های مختلف مربوط به درگاه‌های پرداخت.

3. **مرزبندی شفاف ماژول‌ها (Clear Module Boundaries)**
   - **پیکربندی (Configuration):** مدیریت در ماژول خاص (مثلاً `com.example.config`).
   - **ادغام با درگاه پرداخت (Gateway Integration):** مدیریت در ماژول مجزا (مثلاً `com.example.gateway`).
   - **منطق دامنه (Domain Logic):** قرارگیری کلاس‌های مربوط به `Payment` در ماژول مخصوص (مثلاً `com.example.payment`).
   - **هماهنگی (Orchestration):** انجام آن توسط `PaymentProcessor` (مثلاً در ماژول `com.example.processor`) با استفاده از تزریق سازنده.

---

## کلاس‌ها و واسط‌های کلیدی (Key Classes & Interfaces)

1. **ConfigurationManager**  
   - واسطی با متد `getConfig(String serviceName)` که یک Map از کلید/مقدارهای پیکربندی را برمی‌گرداند.
   - **EnvConfigurationManager:** بارگیری پیکربندی از متغیرهای محیطی (مثلاً `STRIPE_ENDPOINT`, `PAYPAL_ENDPOINT`).
   - **FileConfigurationManager:** (اختیاری) بارگیری پیکربندی از یک فایل `.properties` محلی.

2. **GatewayFactory**  
   - کلاسی فکتوری که از `ConfigurationManager` برای ساخت نمونه مناسب `PaymentGateway` بر اساس نام ورودی (مانند `"stripe"` یا `"paypal"`) استفاده می‌کند.
   - این ساختار باعث می‌شود نیاز به نمونه‌سازی مستقیم در کلاس `Main` از بین برود.

3. **PaymentGateway**  
   - همان واسط تعریف‌شده در مرحله ۳ که شامل متدهای `processPayment()`, `refundPayment()`, و `getTransactionStatus()` می‌باشد.
   - کلاس‌های پیاده‌کننده مانند **StripeGateway** و **PayPalGateway** مقادیر پیکربندی خود را از طریق تزریق دریافت می‌کنند.

4. **PaymentProcessor**  
   - یک شیء از جنس `PaymentGateway` را در سازنده دریافت می‌کند.
   - وظیفه اعتبارسنجی آبجکت‌های `Payment` را بر عهده داشته و سپس عملیات پرداخت را به درگاه تزریق‌شده تفویض می‌کند.

5. **Main**  
   - نمونه‌ای از نحوه‌ی کنار هم قرار دادن اجزای سیستم:
     - انتخاب یک `ConfigurationManager`.
     - ساخت `GatewayFactory`.
     - دریافت و ساخت درگاه پرداخت مناسب (`PaymentGateway`).
     - نمونه‌سازی `PaymentProcessor` با استفاده از درگاه به‌دست‌آمده.
     - ایجاد آبجکت `Payment` (مثلاً `CreditCardPayment`) و پردازش آن.

---

## نمونه استفاده (Example Usage) - کد نمایشی (Pseudocode)

```java
// 1) انتخاب Configuration Manager
ConfigurationManager configManager = new EnvConfigurationManager();
// یا به صورت اختیاری:
// ConfigurationManager configManager = new FileConfigurationManager("app.properties");

// 2) ساخت GatewayFactory
GatewayFactory factory = new GatewayFactory(configManager);

// 3) ایجاد یک PaymentGateway بر اساس نام درگاه مورد نظر
PaymentGateway gateway = factory.createGateway("stripe");

// 4) تزریق درگاه به PaymentProcessor
PaymentProcessor processor = new PaymentProcessor(gateway);

// 5) ساخت آبجکت Payment (مثلاً CreditCardPayment) با اطلاعات مشتری و پرداخت
Payment payment = new CreditCardPayment(100, "USD", customerInfo, paymentDetails);

// 6) پردازش Payment
Map<String, String> result = processor.processPayment(payment);

// 7) در صورت نیاز، انجام بازگشت وجه یا دریافت وضعیت تراکنش
processor.refundPayment(result.get("transaction_id"), 50.0);
String status = processor.getTransactionStatus(result.get("transaction_id"));
