DESCRIPTION = "Gateway: Raspberry Pi boot firmware for Gateway RT"
SECTION = "bsp"
LICENSE = "CLOSED"
LIC_FILES_CHKSUM = "file://boot/LICENCE.broadcom;md5=c403841ff2837657b2ed8e5bb474ac8d"

inherit deploy

SRC_URI = "git://github.com/raspberrypi/firmware.git;protocol=https;branch=stable \
           file://config.txt \
           file://cmdline.txt \
"
SRCREV = "ba22330437d0aaae38ee01dc333e3210536a8333"

S = "${WORKDIR}/git"

RPI_FIRMWARE_OVERLAYS ?= ""

do_install() {
    install -d ${D}/boot ${D}/boot/overlays

    # Use the config/cmdline from sources-unpack
    install -m 0644 ${WORKDIR}/sources-unpack/config.txt  ${D}/boot/config.txt
    install -m 0644 ${WORKDIR}/sources-unpack/cmdline.txt ${D}/boot/cmdline.txt

    # Core firmware blobs
    for f in start.elf start_cd.elf start_db.elf start_x.elf \
             fixup.dat fixup_cd.dat fixup_db.dat fixup_x.dat; do
        if [ -f ${S}/boot/${f} ]; then
            install -m 0644 ${S}/boot/${f} ${D}/boot/${f}
        fi
    done

    # Optional overlays
    for o in ${RPI_FIRMWARE_OVERLAYS}; do
        if [ -f ${S}/boot/overlays/${o} ]; then
            install -m 0644 ${S}/boot/overlays/${o} ${D}/boot/overlays/${o}
        fi
    done
}

do_deploy() {
    install -d "${DEPLOYDIR}"

    # Put files at top-level in deploy dir
    for f in start.elf start_cd.elf start_db.elf start_x.elf \
             fixup.dat fixup_cd.dat fixup_db.dat fixup_x.dat \
             config.txt cmdline.txt; do
        install -m 0644 ${D}/boot/$f "${DEPLOYDIR}/$f"
    done
}

#FILES:${PN} = "\
#    /boot \
#    /boot/overlays \
#    /boot/config.txt \
#    /boot/cmdline.txt \
#    /boot/start*.elf \
#    /boot/fixup*.dat \
#    /boot/overlays/*.dtbo \
#"

INSANE_SKIP:${PN} += "already-stripped arch"
INHIBIT_PACKAGE_STRIP = "1"
PACKAGE_ARCH = "${MACHINE_ARCH}"
PACKAGES = "${PN}"
FILES:${PN} += "/boot"
addtask deploy after do_install before do_build