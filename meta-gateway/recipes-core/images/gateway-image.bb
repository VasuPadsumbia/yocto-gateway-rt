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

# Include Broadcom firmware for Raspberry Pi devices
IMAGE_INSTALL:append = " linux-firmware-rpidistro-brcm43455 "

# Include additional packages for gateway functionality
IMAGE_INSTALL += " \
    raw-sender \
    udev \
    dhcpcd \
    wpa-supplicant \
    dropbear \
    wifi-config \
"
# remote access tools
IMAGE_INSTALL += " \
    openssh \
    nano \
    less \
    iproute2 \
    ethtool \
    net-tools \
"

# Real-time kernel
IMAGE_INSTALL += " \
    kernel-modules \
"
