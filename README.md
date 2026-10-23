# Tetris Prime — Android

لعبة تتريس بواجهة زجاجية ومؤثرات نيون: SRS كامل، T-Spin، تحدي يومي، مواجهة الذكاء الاصطناعي، تعليق صوتي حقيقي و١٦ لغة.

## البناء

```bash
# البناء المحلي
VERSION_NAME=4.80.0 VERSION_CODE=84 ./scripts/build.sh

# التحقق
./scripts/verify.sh build/tetris-prime-4.80.0.apk
```

## الإصدارات

الإصدارات تُنشأ يدوياً عبر GitHub REST API (لا يعتمد على CI).

| الإصدار | versionCode | التاريخ |
|---------|-------------|---------|
| 4.80.0 | 84 | 2026-10-23 |

## التوقيع

APK موقّع بمفتاح `debug.keystore`:
- alias: `tetris-prime`
- storepass: `android`
- keypass: `android`

## البنية

- `src/base.apk` — الـ APK الأصلي من المستخدم
- `src/2u67pfqx45e58.js` — كود اللعبة المُعدّل
- `tools/apktool.jar` — أداة فك/بناء APK
- `tools/sdk/android-14/` — أدوات Android (aapt, zipalign, apksigner)
- `scripts/build.sh` — سكربت البناء
- `scripts/verify.sh` — سكربت التحقق
- `.github/workflows/build.yml` — CI workflow
