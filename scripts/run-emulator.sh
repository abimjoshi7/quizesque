#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
APP_ID="com.abimatwork.quizesque"
BOOT_TIMEOUT_SECONDS="${BOOT_TIMEOUT_SECONDS:-300}"

sdk_dir="${ANDROID_SDK_ROOT:-${ANDROID_HOME:-}}"
if [[ -z "$sdk_dir" && -f "$ROOT_DIR/local.properties" ]]; then
  sdk_dir="$(sed -n 's/^sdk\.dir=//p' "$ROOT_DIR/local.properties" | head -n 1)"
fi
if [[ -z "$sdk_dir" ]]; then
  echo "Android SDK not found. Set ANDROID_SDK_ROOT or ANDROID_HOME, or configure local.properties." >&2
  exit 1
fi

adb="$sdk_dir/platform-tools/adb"
emulator="$sdk_dir/emulator/emulator"
if [[ ! -x "$adb" || ! -x "$emulator" ]]; then
  echo "Expected adb and emulator under $sdk_dir; check your Android SDK installation." >&2
  exit 1
fi

cd "$ROOT_DIR"
"$ROOT_DIR/gradlew" --no-daemon assembleDebug
apk="$ROOT_DIR/app/build/outputs/apk/debug/app-debug.apk"

"$adb" start-server >/dev/null
serial="$("$adb" devices | awk 'NR > 1 && $2 == "device" && $1 ~ /^emulator-/ { print $1; exit }')"
if [[ -z "$serial" ]]; then
  avd_name="${AVD_NAME:-}"
  if [[ -z "$avd_name" ]]; then
    avd_name="$("$emulator" -list-avds | head -n 1)"
  fi
  if [[ -z "$avd_name" ]]; then
    echo "No running emulator or configured AVD found. Create an AVD in Android Studio, or set AVD_NAME." >&2
    exit 1
  fi

  echo "Starting Android emulator: $avd_name"
  nohup "$emulator" -avd "$avd_name" >/dev/null 2>&1 </dev/null &
  start_time="$(date +%s)"
  while :; do
    serial="$("$adb" devices | awk 'NR > 1 && $2 == "device" && $1 ~ /^emulator-/ { print $1; exit }')"
    if [[ -n "$serial" ]] && [[ "$("$adb" -s "$serial" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')" == "1" ]]; then
      break
    fi
    if (( $(date +%s) - start_time >= BOOT_TIMEOUT_SECONDS )); then
      echo "Emulator did not finish booting within ${BOOT_TIMEOUT_SECONDS}s." >&2
      exit 1
    fi
    sleep 2
  done
else
  echo "Using running emulator: $serial"
  start_time="$(date +%s)"
  until [[ "$("$adb" -s "$serial" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')" == "1" ]]; do
    if (( $(date +%s) - start_time >= BOOT_TIMEOUT_SECONDS )); then
      echo "Emulator did not finish booting within ${BOOT_TIMEOUT_SECONDS}s." >&2
      exit 1
    fi
    sleep 2
  done
fi

"$adb" -s "$serial" install -r "$apk"
"$adb" -s "$serial" shell am force-stop "$APP_ID"
"$adb" -s "$serial" shell monkey -p "$APP_ID" -c android.intent.category.LAUNCHER 1 >/dev/null

echo "QUIZesque is installed and open on $serial. APK: $apk"
