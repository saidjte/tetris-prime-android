# Tetris Prime — Kotlin Native

لعبة تتريس مكتوبة بلغة Kotlin Native لأندرويد.

## البنية

- `app/src/main/java/com/tetrisprime/kotlin/`
  - `Piece.kt` — تعريف القطع وأنماط الدوران (SRS)
  - `Board.kt` — لوحة اللعب (10×20)
  - `BagRandomizer.kt` — مولّد عشوائي 7-bag
  - `GameEngine.kt` — محرك اللعبة (الحركة، الدوران، الإسقاط، النقاط)
  - `GameView.kt` — عرض اللعبة (Canvas rendering)
  - `MainActivity.kt` — النشاط الرئيسي + أزرار التحكم
- `app/src/main/res/layout/activity_main.xml` — تخطيط الواجهة
- `build.gradle` — إعداد Gradle

## المميزات المنفّذة

- ✅ محرك لعبة كامل (SRS، wall kicks، 7-bag randomizer)
- ✅ 7 أنواع قطع (I, O, T, S, Z, J, L)
- ✅ دوران مع wall kicks
- ✅ Hold piece
- ✅ Ghost piece (ظل القطعة)
- ✅ Soft drop / Hard drop
- ✅ نظام نقاط (100/300/500/800 + combo)
- ✅ مستويات (كل 10 خطوط)
- ✅ واجهة لمس (6 أزرار)

## البناء

```bash
cd kotlin-src
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

## TODO (مميزات مستقبلية)

- [ ] T-Spin detection
- [ ] Perfect Clear
- [ ] Back-to-Back bonus
- [ ] تأثيرات الجسيمات
- [ ] الثيمات
- [ ] المعلق الصوتي
- [ ] الموسيقى التصويرية
- [ ] شاشة القائمة الرئيسية
- [ ] الإعدادات
