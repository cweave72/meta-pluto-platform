SUMMARY = "Platform default FPGA bitstream"
DESCRIPTION = "Deploys the bitstream in PLUTO_BITSTREAM as pluto-default.bit. \
The pluto-fit recipe puts it into the FIT image. u-boot loads it at boot."
LICENSE = "CLOSED"

# Path of the .bit file. Set it in local.conf.
PLUTO_BITSTREAM ?= ""

python () {
    if not d.getVar("PLUTO_BITSTREAM"):
        raise bb.parse.SkipRecipe("Set PLUTO_BITSTREAM in local.conf to the path of the default .bit file")
}

inherit deploy nopackages

COMPATIBLE_MACHINE = "^pluto$"
PACKAGE_ARCH = "${MACHINE_ARCH}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_install[noexec] = "1"

do_deploy() {
    if [ ! -f "${PLUTO_BITSTREAM}" ]; then
        bbfatal "PLUTO_BITSTREAM file not found: ${PLUTO_BITSTREAM}"
    fi
    install -m 0644 "${PLUTO_BITSTREAM}" ${DEPLOYDIR}/pluto-default.bit
}
# Run do_deploy again when the bitstream file changes.
do_deploy[file-checksums] += "${PLUTO_BITSTREAM}:True"
addtask deploy after do_unpack before do_build
