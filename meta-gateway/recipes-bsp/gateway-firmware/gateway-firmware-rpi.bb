DESCRIPTION = "Gateway: Raspberry Pi boot firmware for Gateway RT"
SECTION = "bsp"
LICENSE = "CLOSED"

inherit gateway-firmware

FIRMWARE_QORIQ_BRANCH = "master"
FIRMWARE_QORIQ_SRC = "git://github.com/raspberrypi/firmware.git;protocol=https"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

# Pin to RPi firmware repo, user may swap for different board later
SRC_URI = "${FIRMWARE_QORIQ_SRC};branch=${FIRMWARE_QORIQ_BRANCH} \
           file://config.txt \
           file://cmdline.txt \
           "

SRCREV = "ba22330437d0aaae38ee01dc333e3210536a8333"

S = "${WORKDIR}/git"

FW_BOOT_SUBDIR = "boot"

#python do_deploy() {
#    import os, shutil
#    dstdir = os.path.join(d.getVar('DEPLOYDIR'), 'gateway-fw', d.getVar('MACHINE'))
#    os.makedirs(dstdir, exist_ok=True)
#    def _copy(srcname, dstname):
#        src = os.path.join(d.getVar('WORKDIR'), srcname)
#        dst = os.path.join(dstdir, dstname)
#        if os.path.isdir(src):
#            shutil.copytree(src, dst)
#        else:
#            shutil.copy2(src, dst)
#    for fname in ("config.txt", "cmdline.txt"):
#        _copy(fname, fname)
#}
do_install() {
    install -d ${D}/etc/gateway-fw
    for f in config.txt cmdline.txt; do
        if [ -f "${WORKDIR}/${f}" ]; then
            src="${WORKDIR}/${f}"
        elif [ -f "${WORKDIR}/files/${f}" ]; then
            src="${WORKDIR}/files/${f}"
        else
            src=""
        fi

        if [ -n "${src}" ]; then
            install -m 0644 "${src}" "${D}/etc/gateway-fw/${f}"
        fi
    done
}

FILES:${PN} += "/etc/gateway-fw/*"