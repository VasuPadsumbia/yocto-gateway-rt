SUMMARY = "Gateway RT image"
DESCRIPTION = "Image for Gateway RT"
LICENSE = "MIT"

inherit core-image

# Include common features for gateway image
IMAGE_FEATURES += " \
    ssh-server-openssh \
    splash \
    package-management \
    "

IMAGE_INSTALL:remove = " \
    dropbear \
"
# Custom recipes for Raspberry Pi Zero 2 W with real-time kernel
IMAGE_INSTALL += " \
    gateway-firmware-rpi \
    raw-sender \
"

# Include additional packages for gateway functionality
IMAGE_INSTALL += " \
    udev \
    dhcpcd \
    wpa-supplicant \
    wifi-config \
    packagegroup-core-ssh-openssh \
"

# remote access tools
IMAGE_INSTALL += " \
    openssh \
    nano \
    less \
    iproute2 \
"

# Real-time kernel
IMAGE_INSTALL += " \
    kernel-modules \
"
