SUMMARY = "Raw socket RT sender application"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/MIT;md5=0835cfd4e8f1f1a1a9c58f5e8d3f4b6d"

SRC_URI = "file://raw_sender.cpp \
           file://raw_sender.service \
          "

S = "${THISDIR}"

inherit systemd

SYSTEMD_SERVICE = "${PN}"
SYSTEMD_SERVICE:${PN} = "raw-sender.service"

do_compile() {
    ${CXX} ${CXXFLAGS} ${LDFLAGS} -o raw-sender raw-sender.cpp
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/raw-sender ${D}${bindir}/raw-sender
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/raw-sender.service ${D}${systemd_system_unitdir}/
}

FILES:${PN} += "${systemd_system_unitdir}/raw-sender.service"

