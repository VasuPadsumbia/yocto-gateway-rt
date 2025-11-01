#!/usr/bin/env bash
# Run KAS inside a container to isolate host

set -e
PROJECT_DIR=$(dirname "$(realpath "$0")")
IMAGE_NAME="ghcr.io/siemens/kas:latest"

docker run --rm -it \
    -v "$PROJECT_DIR":"$PROJECT_DIR" \
    -w "$PROJECT_DIR" \
    --network host \
    $IMAGE_NAME build kas/firmware.yml