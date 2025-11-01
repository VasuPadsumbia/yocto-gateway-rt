DESCRIPTION = "Linux kernel recipe for Raspberry Pi with real-time patches"
SECTION = "kernel"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=801f80980df4efcf6cde18aa51f0b2f1"

inherit kernel
inherit deploy

LINUX_VERSION = "6.6"
LINUX_QORIQ_BRANCH = "rpi-6.6.y"
LINUX_QORIQ_SRC = "git://github.com/raspberrypi/linux.git;protocol=https"

SRC_URI = "${LINUX_QORIQ_SRC};branch=${LINUX_QORIQ_BRANCH} \
          file://defconfig \
          file://bcm2836-rpi-zero-2w.dts \
"

SRCREV = "bba53a117a4a5c29da892962332ff1605990e17a"

S = "${THISDIR}/git"

KERNEL_IMAGETYPE = "zImage"
KERNEL_DEVICETREE = "bcm2836-rpi-zero-2w.dtb"
KERNEL_DEFCONFIG = "defconfig"

COMPATIBLE_MACHINE = "raspberrypi0-2w"

do_compile:prepend() {
    bbnote " Building kernel with real-time patches ${MACHINE}"
}


