SUMMARY = "ADI PlutoSDR JTAG bootstrap files"
DESCRIPTION = "Deploys ps7_init.tcl, system_top.bit, and u-boot.elf from the \
ADI plutosdr-fw release. scripts/jtag/pluto-jtag-boot.sh uses them. \
pluto-bitstream uses system_top.bit if PLUTO_BITSTREAM is empty."
HOMEPAGE = "https://github.com/analogdevicesinc/plutosdr-fw"

# u-boot.elf: ADI u-boot, GPL-2.0-or-later. Source: analogdevicesinc/u-boot.
# system_top.bit: ADI HDL modules under separate licenses. ps7_init.tcl: Vivado output.
# The recipe fetches the files from the ADI release. It does not keep them in the layer.
LICENSE = "GPL-2.0-or-later & Proprietary"
LIC_FILES_CHKSUM = " \
    file://${COMMON_LICENSE_DIR}/GPL-2.0-or-later;md5=fed54355545ffd980b814dab4a3b312c \
    file://${COMMON_LICENSE_DIR}/Proprietary;md5=0557f9d92cf58f2ccdd50f62f8ac0b28 \
    "

SRC_URI = "https://github.com/analogdevicesinc/plutosdr-fw/releases/download/v${PV}/plutosdr-jtag-bootstrap-v${PV}.zip;subdir=${BP}"
SRC_URI[sha256sum] = "8c1643ad13181932bcb099f95c6e807f85271e2f91d8faccd6d52b15d1bea99e"

inherit deploy nopackages

COMPATIBLE_MACHINE = "^pluto$"
PACKAGE_ARCH = "${MACHINE_ARCH}"
INHIBIT_DEFAULT_DEPS = "1"

do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_install[noexec] = "1"

# Output: ${DEPLOY_DIR_IMAGE}/adi-jtag-bootstrap/
do_deploy() {
    install -d ${DEPLOYDIR}/adi-jtag-bootstrap
    for f in ps7_init.tcl system_top.bit u-boot.elf; do
        install -m 0644 ${S}/$f ${DEPLOYDIR}/adi-jtag-bootstrap/$f
    done
}
addtask deploy after do_unpack before do_build
