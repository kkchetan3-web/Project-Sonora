#!/usr/bin/env bash
# Capture full task output as an artifact and surface failure details in check annotations.
set -uo pipefail
mkdir -p ci-logs
name="$1"; shift
"$@" 2>&1 | tee "ci-logs/$name.log"
result=${PIPESTATUS[0]}
if (( result != 0 )); then
    python3 - "$name" <<'PY'
import pathlib, sys
name = sys.argv[1]
text = pathlib.Path('ci-logs', name + '.log').read_text(errors='replace')[-24000:]
text = text.replace('%', '%25').replace('\r', '%0D').replace('\n', '%0A')
print(f'::error title={name} failed::{text}')
PY
fi
exit "$result"
