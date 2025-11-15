#!/usr/bin/env bash
# Run KAS inside a container with a native Linux filesystem workspace.
# This avoids VFAT/showexec permission issues that break executable scripts.

set -euo pipefail

PROJECT_DIR=$(dirname "$(realpath "$0")")
IMAGE_NAME=${IMAGE_NAME:-ghcr.io/siemens/kas:latest}
CE=${CE:-}

# Pick container engine
if [[ -z "${CE}" ]]; then
    if command -v podman >/dev/null 2>&1; then
        CE=podman
    elif command -v docker >/dev/null 2>&1; then
        CE=docker
    else
        echo "Container engine not found. Install Docker or Podman (set CE=docker|podman)." >&2
        exit 1
    fi
fi

# Use a named volume for the Yocto/Kas work dir to ensure proper exec bits & symlinks
YOCTO_WORK_VOLUME=${YOCTO_WORK_VOLUME:-yocto-gateway-rt-work}

# Create volume if missing
if ! ${CE} volume inspect "${YOCTO_WORK_VOLUME}" >/dev/null 2>&1; then
    ${CE} volume create "${YOCTO_WORK_VOLUME}" >/dev/null
fi

# Map firmware.yml into the container; allow passing arbitrary kas args
ARGS=("$@")
if [[ ${#ARGS[@]} -eq 0 ]]; then
    ARGS=(build /config/firmware.yml)
else
    # Replace path to firmware.yml if user passed host-relative path
    for i in "${!ARGS[@]}"; do
        if [[ "${ARGS[$i]}" == "kas/firmware.yml" || "${ARGS[$i]}" == "./kas/firmware.yml" ]]; then
            ARGS[$i]="/config/firmware.yml"
        fi
    done
fi

# Ensure meta-gateway layer is available inside /work for relative 'path: meta-gateway'
EXTRA_MOUNTS=( -v "${PROJECT_DIR}/meta-gateway":/work/meta-gateway )
if [[ ! -d "${PROJECT_DIR}/meta-gateway" ]]; then
    # If layer was moved/renamed, mount the repo root so user can still reference local layers
    EXTRA_MOUNTS=( -v "${PROJECT_DIR}":/work/src )
fi

${CE} run --rm -it \
    -v "${YOCTO_WORK_VOLUME}":/work \
    -v "${PROJECT_DIR}/kas":/config:ro \
    "${EXTRA_MOUNTS[@]}" \
    -w /work \
    --network host \
    "${IMAGE_NAME}" \
    "${ARGS[@]}"