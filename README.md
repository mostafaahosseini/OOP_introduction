# OOP_introduction




در کد ارائه‌شده برای کلاس Java با نام PaymentProcessor، سه نوع پرداخت اصلی—کارت اعتباری، کیف پول دیجیتال و انتقال بانکی—مدیریت می‌شوند. این کلاس از متد مرکزی processPayment() استفاده می‌کند که ابتدا پارامترهای ورودی (مانند مبلغ، ارز، اطلاعات مشتری/پرداخت) را اعتبارسنجی می‌کند، سپس با استفاده از ساختار switch بر اساس paymentType تصمیم می‌گیرد که کدام متد پردازش‌کننده را فراخوانی کند. هر روش پرداخت، تعامل با یک API خارجی را شبیه‌سازی کرده و یک شناسه تراکنش بر اساس زمان جاری ایجاد می‌کند. همچنین این کلاس شامل قابلیت لاگ‌گیری و یک متد main() نمونه است که اجرای کد با پیکربندی و داده‌های آزمایشی را نشان می‌دهد. هرچند این کد از نظر عملکردی صحیح و دارای ساختاری روشن است، اما وظایف متعددی—از جمله اعتبارسنجی، لاگ‌گیری و شبیه‌سازی API—را در یک کلاس ترکیب کرده و در نتیجه آزمایش، مقیاس‌دهی و نگهداری در طول زمان را دشوار می‌کند.

لیست بوی بدی (Code Smells) و توضیحات
1. نام کلاس: PaymentProcessor
public class PaymentProcessor {

چرایی: اگرچه در نگاه اول توصیفی به نظر می‌رسد، اما این کلاس فقط پرداخت را پردازش نمی‌کند؛ بلکه اعتبارسنجی، لاگ‌گیری، و منطق چند نوع پرداخت مختلف را هم انجام می‌دهد که نقض اصل «یک کلاس، یک مسئولیت» است.
 چرا بوی بد است: این یک نمونه از بوی بد «God Class» محسوب می‌شود. وجود مسئولیت‌های متعدد در یک کلاس واحد باعث می‌شود نگهداری یا گسترش آن دشوار شود.

2. نام متد: processPayment
public Map<String, String> processPayment(...)

چرایی: نام متد بسیار کلی به نظر می‌رسد و نشان نمی‌دهد که در واقع وظیفه‌ی مسیریابی، اعتبارسنجی و لاگ‌گیری را هم بر عهده دارد.
 چرا بوی بد است: اینجا با بوی «Long Method» و «Divergent Change» مواجهیم. زیرا هر تغییری در منطق نوع پرداخت یا اعتبارسنجی بر این متد تأثیر می‌گذارد و باعث می‌شود این متد بزرگ و به‌شدت مرتبط با جنبه‌های گوناگون باشد.

3. فهرست پارامترهای متد: processPayment(...)
public Map<String, String> processPayment(String paymentType, double amount, String currency,
                                          Map<String, String> customerInfo, Map<String, String> paymentDetails)

چرایی: این متد پارامترهای زیادی دارد، از جمله چندین Map<String, String>. این باعث پیچیدگی در خواندن، درک و نگهداری کد می‌شود.
 چرا بوی بد است: به آن «Long Parameter List» می‌گویند. بهتر است از مدل‌های داده‌ای صحیح (مانند کلاس‌های Customer، Payment، و PaymentInfo) استفاده شود.

4. استفاده از Mapهای خام (Map<String, String>)
Map<String, String> customerInfo, Map<String, String> paymentDetails

چرایی: استفاده از Map خام بدون ساختار مشخص می‌تواند کد را در برابر خطا آسیب‌پذیر کند؛ هیچ ایمنی نوع (type safety) یا شفافیتی درباره کلیدهای مورد انتظار وجود ندارد.
 چرا بوی بد است: این نمونه‌ای از بوی بد «Primitive Obsession» است. بهتر است داده‌های ساخت‌یافته در آبجکت‌های مشخص کپسوله شوند تا به جای مجموعه‌های اولیه، مدل‌های معنادارتری داشته باشیم.

5. ثابت‌نویسی رشته برای انواع پرداخت (Hardcoded Strings)
switch (paymentType) {
    case "credit_card":
    case "digital_wallet":
    case "bank_transfer":

چرایی: این رشته‌ها در کد پخش شده‌اند و اگر جای دیگری بخواهند استفاده شوند، شکننده می‌شوند.
 چرا بوی بد است: «Magic String» به حساب می‌آید. استفاده از enumها یا مقادیر ثابت، خوانایی و نگهداری را افزایش می‌دهد.

6. تکرار کد در متدهای processCreditCard, processDigitalWallet و processBankTransfer
System.out.println("Connecting to [API] at " + config.get("[endpoint]"));
String transactionId = "[Prefix]" + new Date().getTime();
System.out.println("Processing [type] payment for " + customerInfo.get("name"));

چرایی: این سه متد تقریباً منطق مشابهی دارند و فقط در رشته‌ها و نام endpoint تفاوت جزئی دیده می‌شود.
 چرا بوی بد است: «Duplicate Code»؛ تکرار کد هزینه نگهداری را بالا می‌برد و ریسک بروز باگ را افزایش می‌دهد. این منطق می‌تواند در متد یا کلاس مشترکی انتزاع شود.

7. ادغام دغدغه‌ها: قرار دادن لاگ‌گیری در منطق تجاری
System.out.println("Processing credit card payment for " + customerInfo.get("name"));
...
System.out.println("LOG: " + logEntry);

چرایی: چاپ پیام‌های لاگ با System.out.println، لاگ‌گیری را به‌شدت به منطق تجاری گره می‌زند.
 چرا بوی بد است: از نوع «Shotgun Surgery» است؛ اگر رفتار لاگ‌گیری تغییر کند، باید در جای‌جای کد آن را تغییر دهیم. همچنین «Low Cohesion» وقتی رخ می‌دهد که وظایف نامرتبط (مثل لاگ‌گیری) در کنار وظایف اصلی کلاس حضور داشته باشند.

8. متد validatePayment – انفجار منطق شرطی
switch (paymentType) {
    case "credit_card":
        ...
    case "digital_wallet":
        ...
    case "bank_transfer":
        ...

چرایی: این متد از شرط‌های مختلف برای انواع پرداخت استفاده می‌کند. با اضافه شدن هر نوع جدید، کد رشد می‌کند و نگهداری آن سخت‌تر می‌شود.
 چرا بوی بد است: «Conditional Complexity» و «Feature Envy». نیاز به چندریختی (polymorphism) یا الگوی Strategy دارد تا هر نوع پرداخت، اعتبارسنجی مختص به خود را انجام دهد.

9. تولید Timestamp: new Date().getTime()
String transactionId = "CC" + new Date().getTime();

چرایی: این روش تولید شناسه تراکنش ساده‌لوحانه است و در هر نوع پرداخت تکرار می‌شود.
 چرا بوی بد است: «Inappropriate Intimacy» همراه با «Duplicated Code». با استخراج متدی مشترک مثل generateTransactionId(prefix) وضوح کد بیشتر می‌شود و تکرار کم.

10. نوع بازگشتی تمام متدهای پرداخت: Map<String, String>
return Map.of("status", "success", "transaction_id", transactionId);

چرایی: مجدداً از یک Map خام به عنوان پاسخ ساختاری استفاده شده است. قرارداد مشخصی برای فیلدهای success/failure وجود ندارد.
 چرا بوی بد است: نمونه دیگر «Primitive Obsession». استفاده از کلاسی مثل PaymentResult یا TransactionResult منظور را شفاف می‌کند و ایمنی کد را بالا می‌برد.

تشریح اصلاحات و نقض اصول SOLID در کد PaymentProcessor
1. نقض اصل Single Responsibility Principle (SRP)
مشکل: کلاس PaymentProcessor کارهای متعدد انجام می‌دهد: اعتبارسنجی پرداخت، انتخاب متد پرداخت، پردازش تراکنش و لاگ‌گیری تراکنش.


راه‌حل: کلاس را به چند کلاس کوچک‌تر تقسیم کنید که هرکدام مسئولیت واحدی داشته باشند:


InputValidator: مسئول اعتبارسنجی ورودی


PaymentMethodChooser: مسئول انتخاب متد پرداخت


TransactionLogger: مسئول لاگ‌گیری تراکنش


PaymentHandler: مسئول پردازش پرداخت


این جداسازی باعث می‌شود هر کلاس تنها یک دلیل برای تغییر داشته باشد و نگهداری و تست کد آسان‌تر شود.


توضیح: با شکستن کلاس به بخش‌های کوچک‌تر، هر جزء روی یک جنبه خاص از منطق تمرکز می‌کند. این کار با SRP منطبق است و توسعه یا تغییر در هر بخش بدون تحت تأثیر قرار دادن بخش‌های دیگر انجام می‌شود.

2. نقض اصل Open-Closed Principle (OCP)
مشکل: متد processPayment برای انواع پرداخت از switch استفاده می‌کند. اگر نوع پرداخت جدیدی اضافه شود (مثلاً پرداخت رمز‌ارز)، متد باید تغییر کند که نقض اصل OCP است.


راه‌حل: چندریختی را به کار ببرید تا کلاس برای گسترش باز باشد اما برای تغییر بسته بماند. یک واسط یا کلاس انتزاعی (مانند PaymentProcessor trait) با متد processPayment بسازید و سپس برای هر نوع پرداخت یک کلاس پیاده‌سازی بسازید (مثلاً CreditCardProcessor, DigitalWalletProcessor, BankTransferProcessor). به‌جای ویرایش processPayment برای افزودن روش جدید، تنها کافیست یک پیاده‌سازی جدید بیفزایید.


interface PaymentProcessor {
    Map<String, String> processPayment(double amount, String currency, Map<String, String> customerInfo, Map<String, String> paymentDetails);
}

class CreditCardProcessor implements PaymentProcessor {
    public Map<String, String> processPayment(double amount, String currency, Map<String, String> customerInfo, Map<String, String> paymentDetails) {
        // منطق پردازش کارت اعتباری
    }
}

توضیح: این طراحی تضمین می‌کند که افزودن یک روش پرداخت جدید، نیاز به دستکاری متد processPayment اصلی ندارد و با اصل OCP همخوانی دارد.

3. نقض اصل Liskov Substitution Principle (LSP)
مشکل: متدهای processCreditCard, processDigitalWallet, و processBankTransfer خیلی شبیه هم هستند اما تحت یک واسط یا کلاس مشترک قرار نگرفته‌اند. جایگزینی یک روش با روش دیگر نیازمند تغییر در منطق موجود است.


راه‌حل: یک واسط مشترک PaymentProcessor تعریف کنید تا همه‌ی روش‌های پرداخت آن را پیاده کنند. هرکلاس (CreditCardProcessor, DigitalWalletProcessor) منطق مختص خود را پیاده‌سازی کند.


interface PaymentProcessor {
    Map<String, String> processPayment(double amount, String currency, Map<String, String> customerInfo, Map<String, String> paymentDetails);
}

توضیح: با این کار، هر پیاده‌سازی از PaymentProcessor را می‌توان در کد جایگزین کرد بدون اینکه رفتار کلی بهم بخورد و LSP رعایت می‌شود.

4. نقض اصل Interface Segregation Principle (ISP)
مشکل: کلاس فعلی مستقیماً یک واسط پیاده نمی‌کند، اما اگر قرار بود یک واسط بسازیم، ممکن بود متدهای مختلفی مانند اعتبارسنجی، لاگ‌گیری، پردازش پرداخت و انتخاب متد در یک واسط بزرگ گنجانده شوند. این باعث می‌شود کلاس‌هایی که به همه‌ی متدها نیاز ندارند مجبور به پیاده‌سازی آن‌ها شوند.


راه‌حل: واسط را به چند واسط کوچک‌تر تقسیم کنید:

 interface Validatable {
    boolean validate(Map<String, String> paymentDetails);
}

interface Loggable {
    void log(Map<String, String> transactionDetails);
}

interface Processable {
    Map<String, String> process(Map<String, String> paymentDetails);
}
 توضیح: با این کار، کلاس‌ها فقط متدهایی را پیاده می‌کنند که واقعاً نیاز دارند و از متدهای بلااستفاده پرهیز می‌شود، در نتیجه اصل ISP رعایت می‌شود.



5. نقض اصل Dependency Inversion Principle (DIP)
مشکل: کلاس PaymentProcessor به پیاده‌سازی‌های مشخصی برای پرداخت (مانند CreditCardProcessor, DigitalWalletProcessor) وابسته است.


راه‌حل: ماژول‌های سطح بالا مثل PaymentProcessor نباید به جزئیات پیاده‌سازی سطح پایین متصل باشند، بلکه به واسط‌ها متکی باشند و پیاده‌سازی‌ها از طریق سازنده یا فریمورک DI تزریق شوند:


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

توضیح: با تزریق وابستگی از طریق سازنده یا یک فریمورک DI، کلاس PaymentProcessor از پیاده‌سازی‌های خاص جدا می‌شود و اصل DIP رعایت خواهد شد.

خلاصه تغییرات (Summary of Changes)
اصل
مشکل شناسایی‌شده
راه‌حل پیشنهادی
SRP
انجام اعتبارسنجی، لاگ‌گیری، انتخاب متد و پردازش در یک کلاس
تقسیم به کلاس‌های خدمت‌رسان جداگانه (InputValidator, PaymentMethodChooser, TransactionLogger, PaymentHandler)
OCP
نیاز به تغییر در هر بار افزودن روش پرداخت جدید
استفاده از چندریختی (واسط PaymentProcessor) و پیاده‌سازی‌های خاص برای هر روش پرداخت
LSP
متدهایی نظیر processCreditCard, processDigitalWallet و ... همگن نیستند
تعریف واسط مشترک PaymentProcessor تا همه روش‌های پرداخت آن را پیاده کنند
ISP
واسط بالقوه بزرگی که متدهای اعتبارسنجی، لاگ‌گیری، پردازش و انتخاب را با هم دارد
شکستن واسط به واسط‌های کوچک‌تر مانند Validatable, Loggable, Processable
DIP
وابستگی ماژول سطح بالا به کلاس‌های پیاده‌سازی خاص
تزریق وابستگی از طریق سازنده یا فریمورک DI و استفاده از واسط برای انواع پرداخت


این تحلیل، مسائل عمده‌ای را که در کد PaymentProcessor مشاهده می‌شود، پوشش می‌دهد؛ از گستردگی مسئولیت گرفته تا تکرار کد و نقض اصول پنج‌گانه SOLID. انجام اصلاحات پیشنهادی می‌تواند ساختار را ماژولارتر، نگه‌دارپذیرتر و آماده‌ی گسترش در آینده کند.
