SUMMARY = "Gateway RT image"
DESCRIPTION = "Image for Gateway RT"
LICENSE = "MIT"

inherit core-image

# Use standard RPi boot recipes
# IMAGE_INSTALL:append = " rpi-bootfiles rpi-config rpi-cmdline"

# Ensure firmware is deployed before WIC
do_image_wic[depends] += "gateway-firmware-rpi:do_deploy"

# Boot files now come directly from gateway-firmware-rpi deploy dir (top-level)
IMAGE_BOOT_FILES:gateway-rpi0-2w-64 = " \
    start.elf;start.elf \
    start_cd.elf;start_cd.elf \
    start_db.elf;start_db.elf \
    start_x.elf;start_x.elf \
    fixup.dat;fixup.dat \
    fixup_cd.dat;fixup_cd.dat \
    fixup_db.dat;fixup_db.dat \
    fixup_x.dat;fixup_x.dat \
    config.txt;config.txt \
    cmdline.txt;cmdline.txt \
    Image \
"

# Ensure no unwanted overlays are requested
IMAGE_BOOT_FILES:gateway-rpi0-2w-64:remove = " \
    overlay_map.dtb \
    overlays/overlay_map.dtb \
    overlay_map.dtb;overlays/overlay_map.dtb \
"

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
