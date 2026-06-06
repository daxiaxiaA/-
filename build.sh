#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
BUILD_DIR="$ROOT_DIR/build/classes"
RELEASE_DIR="$ROOT_DIR/release"
MAIN_CLASS="com.example.cryptotask.CryptoTaskApp"

rm -rf "$ROOT_DIR/build"
mkdir -p "$BUILD_DIR" "$RELEASE_DIR"

javac --release 17 -encoding UTF-8 -d "$BUILD_DIR" \
  $(find "$ROOT_DIR/src/main/java" -name '*.java' | sort)

jar --create --file "$RELEASE_DIR/CryptoTaskApp.jar" \
  --main-class "$MAIN_CLASS" \
  -C "$BUILD_DIR" .

echo "Built $RELEASE_DIR/CryptoTaskApp.jar"
