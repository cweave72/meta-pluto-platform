SUMMARY = "USB network gadget for the Pluto USB OTG port"
DESCRIPTION = "Configures a CDC ECM (or NCM) USB network gadget with configfs \
at boot. The networking script gives usb0 the address 192.168.2.1. \
Busybox udhcpd gives the host 192.168.2.10."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = " \
    file://pluto-usb-gadget \
    file://pluto-usb-gadget.default \
    file://udhcpd.conf \
    "
S = "${UNPACKDIR}"

inherit update-rc.d allarch

# rcS.d/S38: after populate-volatile (S37), before networking (rc5.d/S01).
INITSCRIPT_NAME = "pluto-usb-gadget"
INITSCRIPT_PARAMS = "start 38 S . stop 90 0 6 ."

do_install() {
    install -d ${D}${sysconfdir}/init.d ${D}${sysconfdir}/default
    install -m 0755 ${S}/pluto-usb-gadget ${D}${sysconfdir}/init.d/pluto-usb-gadget
    install -m 0644 ${S}/pluto-usb-gadget.default ${D}${sysconfdir}/default/pluto-usb-gadget
    install -m 0644 ${S}/udhcpd.conf ${D}${sysconfdir}/udhcpd.conf
}

CONFFILES:${PN} = "${sysconfdir}/default/pluto-usb-gadget ${sysconfdir}/udhcpd.conf"
RDEPENDS:${PN} = "busybox-udhcpd init-ifupdown"
