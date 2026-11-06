#!/usr/bin/env bash
set -uo pipefail
mkdir -p ci-logs
adb shell getprop ro.build.version.release | tee ci-logs/android-version.txt
adb shell getprop ro.product.model | tee ci-logs/device-model.txt
./scripts/ci-run.sh instrumentation ./gradlew --no-daemon --stacktrace :app:connectedDebugAndroidTest
status=$?
python3 - <<'PYTEST'
import glob, xml.etree.ElementTree as ET
files = glob.glob('app/build/outputs/androidTest-results/connected/**/*.xml', recursive=True)
if not files:
    print('::warning::No instrumentation XML reports found')
for path in files:
    try:
        root = ET.parse(path).getroot()
        for case in root.iter('testcase'):
            failure = case.find('failure')
            error = case.find('error')
            problem = failure if failure is not None else error
            text = (f"{case.get('classname')}.{case.get('name')}: " + ((problem.text or problem.get('message') or 'FAILED')[:4000] if problem is not None else 'PASSED'))
            text = text.replace('%', '%25').replace('\r', '%0D').replace('\n', '%0A')
            print(('::error::' if problem is not None else '::notice::') + text)
    except Exception as exc:
        print(f'::warning::Could not parse instrumentation result: {exc}')
PYTEST
adb logcat -d -v threadtime > ci-logs/logcat.txt || true
adb shell screencap -p /sdcard/sonora-ci.png && adb pull /sdcard/sonora-ci.png ci-logs/screenshot.png || true
exit "$status"
