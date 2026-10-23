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
