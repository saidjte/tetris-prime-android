# Worklog — Tetris Prime Android

## 2026-10-23: النشر إلى GitHub

### الخطوات المنفّذة

#### 1. إعداد المستودع
- إنشاء مستودع Git محلي في `tetris-prime-android/`
- بنية المشروع:
  - `src/base.apk` — الـ APK الأصلي من المستخدم
  - `src/2u67pfqx45e58.js` — كود اللعبة المُعدّل (v4.80.0)
  - `tools/apktool.jar` — أداة فك/بناء APK
  - `tools/sdk/android-14/` — أدوات Android (aapt, zipalign, apksigner)
  - `scripts/build.sh` — سكربت البناء
  - `scripts/verify.sh` — سكربت التحقق
  - `.github/workflows/build.yml` — CI workflow
  - `debug.keystore` — مفتاح التوقيع
  - `version.txt` — معلومات الإصدار

#### 2. سكربت البناء (`scripts/build.sh`)
- يستخدم apktool لفك الـ APK الأصلي
- يطبّق التعديلات (نسخ ملف JS المُعدّل)
- يحدّث versionCode و versionName في جميع الملفات
- يبني APK جديد
- يوقّع بـ debug.keystore (APK Signature Scheme v2 + v3)

#### 3. سكربت التحقق (`scripts/verify.sh`)
يتحقق من:
- versionCode صحيح (≥ 1)
- versionName غير فارغ
- المعماريات (WebView app — يعمل على جميع المعماريات)
- التوقيع صحيح (apksigner verify)

#### 4. GitHub Actions CI
- يعمل عند كل push إلى main
- يستخدم JDK 21
- يبني APK باستخدام scripts/build.sh
- يتحقق باستخدام scripts/verify.sh
- يرفع APK و المصدر كـ artifacts

#### 5. إنشاء Release (يدوي عبر REST API)
- **مهم**: CI لا ينشئ Release — الإنشاء يدوي إجبارياً
- تم إنشاء Release عبر:
  ```bash
  curl -X POST "https://api.github.com/repos/saidjte/tetris-prime-android/releases" \
      -H "Authorization: token $TOKEN" \
      -d '{"tag_name":"v4.80.0","name":"Tetris Prime v4.80.0",...}'
  ```
- تم رفع APK و المصدر عبر:
  ```bash
  curl -X POST "$UPLOAD_URL?name=tetris-prime-4.80.0.apk" \
      -H "Authorization: token $TOKEN" \
      --data-binary @build/tetris-prime-4.80.0.apk
  ```

#### 6. المصدر عبر git archive
```bash
git archive --format=zip --prefix=tetris-prime-4.80.0/ HEAD -o build/tetris-prime-src-4.80.0.zip
```

### الدروس المستفادة

1. **CI لا ينشئ Release — الإنشاء يدوي إجبارياً**
   - GitHub Actions يبني artifacts فقط، لا Releases
   - يجب إنشاء Release يدوياً عبر REST API بعد نجاح CI
   - يجب رفع الملفات إلى Release يدوياً عبر API

2. **build-tools.zip كبير جداً (58MB)**
   - تجاوز حد GitHub الموصى به (50MB)
   - الحل: حذف الملف من المستودع وإضافته إلى .gitignore
   - الأدوات الفعلية (aapt, zipalign, apksigner) صغيرة بما يكفي

3. **WebView/Cordova app لا يحتاج معمارية محددة**
   - التطبيق يعمل على جميع المعماريات (arm64-v8a, armeabi-v7a, x86, x86_64)
   - لا توجد مكتبات أصلية — يستخدم Android WebView
   - سكربت التحقق يتكيف مع هذا

4. **التوقيع بنفس المفتاح ضروري للتحديث**
   - debug.keystore ثابت في المستودع
   - بصمة التوقيع ثابتة عبر جميع الإصدارات
   - المستخدمون يمكنهم التحديث فوق النسخة القديمة مباشرة

### الروابط

- **المستودع**: https://github.com/saidjte/tetris-prime-android
- **Release**: https://github.com/saidjte/tetris-prime-android/releases/tag/v4.80.0
- **APK مباشر**: https://github.com/saidjte/tetris-prime-android/releases/download/v4.80.0/tetris-prime-4.80.0.apk
- **المصدر مباشر**: https://github.com/saidjte/tetris-prime-android/releases/download/v4.80.0/tetris-prime-src-4.80.0.zip

### التحقق من الروابط
- APK: HTTP 200 ✓ (11,234,909 bytes)
- المصدر: HTTP 200 ✓

### صفحة المعاينة
تم تحديث صفحة المعاينة لاستخدام روابط GitHub Release مباشرة.

## 2026-10-23: Kotlin CI Workflow

### الخطوات المنفّذة

#### 1. إنشاء GitHub Actions workflow للبناء Kotlin
- الملف: `.github/workflows/build-kotlin.yml`
- يعمل عند كل push إلى `kotlin-src/` أو `workflow_dispatch`
- يستخدم JDK 17 + Gradle 8.2

#### 2. خطوات الـ CI
1. Checkout الكود
2. تثبيت JDK 17
3. منح صلاحية تنفيذ لـ gradlew
4. بناء Debug APK (`./gradlew assembleDebug`)
5. إعادة تسمية APK إلى `tetris-prime-kotlin-1.0.0.apk`
6. رفع APK كـ artifact
7. إنشاء أرشيف مصدر عبر `git archive`
8. رفع المصدر كـ artifact

#### 3. إصلاحات الأخطاء
- **خطأ 1**: `resource mipmap/ic_launcher not found`
  - السبب: AndroidManifest.xml ي参照 إلى أيقونة غير موجودة
  - الحل: حذف `android:icon="@mipmap/ic_launcher"` من الـ manifest

- **خطأ 2**: `Type mismatch: inferred type is CharArray? but Char? was expected`
  - السبب: نوع grid في Board.kt خاطئ
  - الحل: تغيير من `Array<CharArray?>` إلى `Array<Char?>`

- **خطأ 3**: `Type mismatch: inferred type is Int but Long was expected`
  - السبب: `Math.max(50, ...)` تُرجع Int وليس Long
  - الحل: استخدام `Math.max(50L, ...toLong())`

#### 4. درس مستفاد: GitHub Actions و subdirectories
- **CI لا يقرأ workflows من مجلدات فرعية**
- يجب وضع ملفات workflow في `.github/workflows/` في **جذر المستودع** فقط
- وضع workflow في `kotlin-src/.github/workflows/` لا يعمل

### النتيجة
- ✅ Build APK: نجح
- ✅ Artifact APK: 4.77 MB (tetris-prime-kotlin-apk)
- ✅ Artifact Source: 94 MB (tetris-prime-kotlin-src)
- 🔗 Build URL: https://github.com/saidjte/tetris-prime-android/actions/runs/39954428780

### ملاحظة
الـ artifacts متاحة للتحميل من صفحة الـ CI run فقط (تتطلب تسجيل دخول GitHub).
لإنشاء Release عام بروابط تحميل مباشرة، يجب إنشاؤه يدوياً عبر REST API.

## 2026-10-23: المرحلة 1 — محرك اللعبة الكامل

### ما تم إنجازه

#### Board.kt (مُحدّث)
- ✅ تصحيح الارتفاع من 20 إلى **22 صف** (مطابق للأصل)
- ✅ دعم علامات خاصة: `G` (قمامة)، `C` (جبن)
- ✅ **T-Spin detection** (قاعدة الـ3 أركان): NONE / MINI / FULL
- ✅ **Perfect Clear detection** (board.isEmpty())
- ✅ **Danger zone detection** (highestFilledRow >= 15)
- ✅ إضافة صفوف القمامة (addGarbageRow)
- ✅ ملء خلايا الجبن (fillCheese) لنمط cheese
- ✅ نسخ اللوحة (copy)

#### BagRandomizer.kt (مُحدّث)
- ✅ **Fnv1aHash** — دالة hash مطابقة للأصل (seed=0x811c9dc5, prime=0x1000193)
- ✅ **dailyGoal()** — حساب هدف التحدي اليومي (9000 + hash%25 × 250)
- ✅ **SeededRandom** — مولّد أرقام شبه عشوائية محدد (LCG)
- ✅ **SeededBagRandomizer** — 7-bag محدد للتحديات اليومية الحتمية

#### GameEngine.kt (مُحدّث بالكامل)
- ✅ **صيغة الجاذبية الرسمية**: `max(30, round(1000 × (0.8-(level-1)×0.007)^(level-1)))`
- ✅ **Lock delay** (500ms) مع max 15 resets
- ✅ **T-Spin detection** (3-corner rule + lastActionWasRotate)
- ✅ **Perfect Clear** (+2000 × level)
- ✅ **Back-to-Back** (1.5× multiplier) للـ Tetris/T-Spin المتتالي
- ✅ **Combo system** (+50 × combo × level)
- ✅ **جداول النقاط الكاملة**:
  - عادي: [0, 100, 300, 500, 800]
  - T-Spin: [0, 800, 1200, 1600]
  - T-Spin Mini: نصف النقاط
- ✅ **9 أنماط لعب** (CLASSIC, SPRINT, ULTRA, ZEN, CHEESE, MISSION, DAILY, BATTLE, ADVENTURE)
- ✅ **نظام العد التنازلي** (3-2-1-GO)
- ✅ **تتبع الإحصائيات**: tetrises, tspins, perfectClears, maxCombo, maxB2b, piecesPlaced
- ✅ Soft drop hold (setSoftDropping)
- ✅ DAS/ARR للحركة المتكررة

#### GameView.kt (مُحدّث)
- ✅ عرض الحقل المرئي فقط (صفوف 2-21)
- ✅ **توهج الخطر** (أحمر عند امتلاء اللوحة)
- ✅ ألوان القمامة والجبن
- ✅ Ghost piece محسّن

#### MainActivity.kt (مُحدّث)
- ✅ **DAS=150ms / ARR=35ms** للحركة المتكررة
- ✅ **Soft drop hold** (اضغط واستمر)
- ✅ عرض Combo
- ✅ عرض الحالة (countdown/paused/game over)

### التحقق
- ✅ CI Build: نجح
- ✅ Kotlin compilation: نجح
- 🔗 Build URL: https://github.com/saidjte/tetris-prime-android/actions/runs/40012086926

### الميكانيكيات المنفّذة (مطابقة للأصل)
1. SRS rotation + wall kicks ✅
2. 7-bag randomizer ✅
3. Official gravity formula ✅
4. Lock delay (500ms, max 15 resets) ✅
5. T-Spin detection (3-corner rule) ✅
6. Perfect Clear detection ✅
7. Back-to-Back (1.5×) ✅
8. Combo system ✅
9. Full scoring tables ✅
10. 9 game modes ✅
11. Countdown system ✅
12. Seeded RNG for daily challenges ✅
13. Danger zone detection ✅
