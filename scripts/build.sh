#!/bin/bash
# Build script for Tetris Prime Android APK
# Uses apktool to decompile, apply modifications, and rebuild
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
SRC_DIR="$PROJECT_DIR/src"
TOOLS_DIR="$PROJECT_DIR/tools"
KEYSTORE="$PROJECT_DIR/debug.keystore"

# Version info
VERSION_NAME="${VERSION_NAME:-4.80.0}"
VERSION_CODE="${VERSION_CODE:-84}"

echo "=========================================="
echo "Building Tetris Prime v$VERSION_NAME (code: $VERSION_CODE)"
echo "=========================================="

# Step 1: Decompile base APK
echo "[1/6] Decompiling base APK..."
rm -rf "$PROJECT_DIR/decoded"
java -jar "$TOOLS_DIR/apktool.jar" d -f -o "$PROJECT_DIR/decoded" "$SRC_DIR/base.apk" 2>&1 | tail -3

# Step 2: Copy modified JS file
echo "[2/6] Applying JS modifications..."
cp "$SRC_DIR/2u67pfqx45e58.js" "$PROJECT_DIR/decoded/assets/www/_next/static/chunks/2u67pfqx45e58.js"

# Step 3: Update version strings in all www files
echo "[3/6] Updating version strings to $VERSION_NAME..."
find "$PROJECT_DIR/decoded/assets/www" -type f | while read -r f; do
    if grep -q '4\.64\.0' "$f" 2>/dev/null; then
        sed -i "s/4\.64\.0/$VERSION_NAME/g" "$f"
    fi
done

# Step 4: Update apktool.yml
echo "[4/6] Updating apktool.yml..."
YML="$PROJECT_DIR/decoded/apktool.yml"
sed -i "s/versionCode: 68/versionCode: $VERSION_CODE/" "$YML"
sed -i "s/versionName: 4\.64\.0/versionName: $VERSION_NAME/" "$YML"

# Step 5: Build APK
echo "[5/6] Building APK..."
mkdir -p "$PROJECT_DIR/build"
java -jar "$TOOLS_DIR/apktool.jar" b "$PROJECT_DIR/decoded" -o "$PROJECT_DIR/build/tetris-prime-unsigned.apk" 2>&1 | tail -3

# Step 6: Zipalign and sign
echo "[6/6] Zipaligning and signing..."
SDK="$TOOLS_DIR/sdk/android-14"
"$SDK/zipalign" -p -f 4 "$PROJECT_DIR/build/tetris-prime-unsigned.apk" "$PROJECT_DIR/build/tetris-prime-aligned.apk"
"$SDK/apksigner" sign \
    --ks "$KEYSTORE" \
    --ks-pass pass:android \
    --ks-key-alias tetris-prime \
    --key-pass pass:android \
    --out "$PROJECT_DIR/build/tetris-prime-$VERSION_NAME.apk" \
    "$PROJECT_DIR/build/tetris-prime-aligned.apk"

# Verify signature
echo ""
echo "=========================================="
echo "Build complete!"
echo "=========================================="
"$SDK/apksigner" verify --verbose "$PROJECT_DIR/build/tetris-prime-$VERSION_NAME.apk" 2>&1 | head -4
echo ""
"$SDK/aapt" dump badging "$PROJECT_DIR/build/tetris-prime-$VERSION_NAME.apk" 2>&1 | head -3
echo ""
echo "APK: build/tetris-prime-$VERSION_NAME.apk"
ls -la "$PROJECT_DIR/build/tetris-prime-$VERSION_NAME.apk"
