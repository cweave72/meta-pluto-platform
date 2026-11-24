SUMMARY = "Platform default FPGA bitstream"
DESCRIPTION = "Deploys the default bitstream as pluto-default.bit. \
The pluto-fit recipe puts it into the FIT image. u-boot loads it at boot."
LICENSE = "CLOSED"

# Path of the .bit file. Set it in local.conf.
# Empty: use the ADI stock system_top.bit from pluto-jtag-bootstrap.
PLUTO_BITSTREAM ?= ""

python () {
    bit = d.getVar("PLUTO_BITSTREAM")
    if bit:
        # Run do_deploy again when the bitstream file changes.
        d.appendVarFlag("do_deploy", "file-checksums", " %s:True" % bit)
    else:
        d.appendVarFlag("do_deploy", "depends", " pluto-jtag-bootstrap:do_deploy")
}

inherit deploy nopackages

COMPATIBLE_MACHINE = "^pluto$"
PACKAGE_ARCH = "${MACHINE_ARCH}"
INHIBIT_DEFAULT_DEPS = "1"

do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_install[noexec] = "1"

do_deploy() {
    bit="${PLUTO_BITSTREAM}"
    [ -n "$bit" ] || bit="${DEPLOY_DIR_IMAGE}/adi-jtag-bootstrap/system_top.bit"
    if [ ! -f "$bit" ]; then
        bbfatal "Bitstream file not found: $bit"
    fi
    install -m 0644 "$bit" ${DEPLOYDIR}/pluto-default.bit
}
addtask deploy after do_unpack before do_build
