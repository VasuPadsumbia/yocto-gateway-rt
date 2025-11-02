SUMMARY = "Raw socket RT sender application"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://raw_sender.cpp \
           file://raw_sender.service \
          "

S = "${THISDIR}"

inherit systemd

SYSTEMD_SERVICE = "${PN}"
SYSTEMD_SERVICE:${PN} = "raw_sender.service"

do_compile() {
    ${CXX} ${CXXFLAGS} ${LDFLAGS} ${S}/files/raw_sender.cpp -o raw-sender
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/raw-sender ${D}${bindir}/raw-sender
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/files/raw_sender.service ${D}${systemd_system_unitdir}/
}

FILES:${PN} += "${systemd_system_unitdir}/raw_sender.service"

