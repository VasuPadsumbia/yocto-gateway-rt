DESCRIPTION = "Linux kernel recipe for Raspberry Pi with real-time patches"
SECTION = "kernel"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"

PN = "linux-raspberrypi-rt"
PROVIDES = "virtual/kernel"

inherit kernel
inherit deploy

LINUX_VERSION = "6.6"
LINUX_QORIQ_BRANCH = "rpi-6.6.y"
LINUX_QORIQ_SRC = "git://github.com/raspberrypi/linux.git;protocol=https"

SRC_URI = "${LINUX_QORIQ_SRC};branch=${LINUX_QORIQ_BRANCH} \
          file://defconfig \
          file://gateway-rpi-rt.dts \
"

SRCREV = "bba53a117a4a5c29da892962332ff1605990e17a"

S = "${WORKDIR}/git"

KERNEL_IMAGETYPE = "Image"
KERNEL_DEVICETREE = "broadcom/gateway-rpi-rt.dtb"

KERNEL_DEFCONFIG = "defconfig"
KERNEL_CONFIG_COMMAND = "oe_runmake -C ${S} O=${B} olddefconfig"
COMPATIBLE_MACHINE = "gateway-rpi0-2w-64"

do_configure:prepend() {
    mkdir -p ${B}
    install -m 0644 ${THISDIR}/files/defconfig ${S}/.config
    install -m 0644 ${THISDIR}/files/gateway-rpi-rt.dts ${S}/arch/arm64/boot/dts/broadcom/
}
#do_compile:prepend() {
#    bbnote " Building kernel with real-time patches ${MACHINE}"
#}


