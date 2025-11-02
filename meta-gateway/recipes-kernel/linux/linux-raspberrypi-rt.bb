DESCRIPTION = "Linux kernel recipe for Raspberry Pi with real-time patches"
SECTION = "kernel"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"

inherit kernel
inherit deploy

LINUX_VERSION = "6.6"
LINUX_QORIQ_BRANCH = "rpi-6.6.y"
LINUX_QORIQ_SRC = "git://github.com/raspberrypi/linux.git;protocol=https"

SRC_URI = "${LINUX_QORIQ_SRC};branch=${LINUX_QORIQ_BRANCH} \
          file://defconfig \
          file://bcm2837-rpi-zero-2w.dts \
"

SRCREV = "bba53a117a4a5c29da892962332ff1605990e17a"

S = "${WORKDIR}/git"

KERNEL_IMAGETYPE = "zImage"
#KERNEL_DEVICETREE = "bcm2837-rpi-zero-2w.dts"
KERNEL_DEFCONFIG = "defconfig"
KERNEL_CONFIG_COMMAND = "oe_runmake -C ${S} O=${B} olddefconfig"
COMPATIBLE_MACHINE = "raspberrypi0-2w"

do_configure:prepend() {
    mkdir -p ${B}
    install -m 0644 ${THISDIR}/files/defconfig ${S}/.config
    install -m 0644 ${THISDIR}/files/bcm2837-rpi-zero-2w.dts ${S}/arch/arm/boot/dts/
}
do_compile:prepend() {
    bbnote " Building kernel with real-time patches ${MACHINE}"
}


