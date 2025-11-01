SUMMARY = "WiFi configuration utility"
DESCRIPTION = "A utility for configuring WiFi settings"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/MIT;md5=0835cfd4e8f1f1a1a9c58f5e8d3f4b6d"

SRC_URI = "file://wpa_supplicant.conf.in \
           file://wifi-wlan0.service \
          "

S = "${THISDIR}"

inherit systemd

SYSTEMD_SERVICE = "${PN}"
SYSTEMD_SERVICE:${PN} = "wifi-wlan0.service"

do_configure() {
    install -d ${S}/generated
    sed -e "s|@SSID@|${WIFI_SSID}|g" \
        -e "s|@PSK@|${WIFI_PSK}|g" \
        ${THISDIR}/wpa_supplicant.conf.in > ${S}/generated/wpa_supplicant-wlan0.conf
}

do_install() {
    install -d ${D}${sysconfdir}/wpa_supplicant
    install -m 0600 ${S}/generated/wpa_supplicant-wlan0.conf \
                    ${D}${sysconfdir}/wpa_supplicant/wpa_supplicant-wlan0.conf

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${THISDIR}/wifi-wlan0.service \
                    ${D}${systemd_system_unitdir}/
}

FILES:${PN} += " \
    ${sysconfdir}/wpa_supplicant/wpa_supplicant-wlan0.conf \
    ${systemd_system_unitdir}/wifi-wlan0.service \
"

