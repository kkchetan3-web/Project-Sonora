#!/usr/bin/env bash
set -uo pipefail
mkdir -p ci-logs
adb shell getprop ro.build.version.release | tee ci-logs/android-version.txt
adb shell getprop ro.product.model | tee ci-logs/device-model.txt
./scripts/ci-run.sh instrumentation ./gradlew --no-daemon --stacktrace :app:connectedDebugAndroidTest
status=$?
adb logcat -d -v threadtime > ci-logs/logcat.txt || true
adb shell screencap -p /sdcard/sonora-ci.png && adb pull /sdcard/sonora-ci.png ci-logs/screenshot.png || true
exit "$status"
