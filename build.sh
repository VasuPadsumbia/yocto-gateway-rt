#!/bin/bash
set -e
CLEAN_BUILD=${1:-false}
BUILD_DIR=build

if [ ! -d "$BUILD_DIR" ]; then
    echo "[INFO] Clean kas build from scratch."
    kas build kas/firmware.yml
else
    if [ "$CLEAN_BUILD" = true ]; then
        echo "[INFO] Clean bitbake build with existing build directory."
        kas build --clean bitbake kas/firmware.yml
    else
        echo "[INFO] Incremental kas build."
        kas build --incremental kas/firmware.yml
    fi
fi
