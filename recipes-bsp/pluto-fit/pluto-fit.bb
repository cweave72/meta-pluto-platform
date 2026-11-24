SUMMARY = "Pluto FIT firmware image"
DESCRIPTION = "Makes pluto.itb for the stock ADALM-PLUTO u-boot, and \
pluto.frm (mass-storage update) and pluto.dfu (DFU update) from it."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://pluto.its.in"
S = "${UNPACKDIR}"

inherit deploy nopackages

COMPATIBLE_MACHINE = "^pluto$"
PACKAGE_ARCH = "${MACHINE_ARCH}"

# Image recipe that supplies the initramfs.
PLUTO_FIT_IMAGE ?= "core-image-minimal"
# Maximum pluto.itb size: 14 MB (0xE00000), the platform qspi-linux partition.
PLUTO_FIT_MAX_SIZE ?= "14680064"
# Device tree base names in DEPLOY_DIR_IMAGE, in fdt@1..fdt@3 sequence.
PLUTO_FIT_DTBS ?= "zynq-pluto-sdr zynq-pluto-sdr-revb zynq-pluto-sdr-revc"
# USB IDs of the stock Pluto DFU interface.
PLUTO_DFU_VID ?= "0x0456"
PLUTO_DFU_PID ?= "0xb673"

DEPENDS = "u-boot-tools-native dtc-native dfu-util-native"

# DEPENDS does not wait for deploy or image tasks. These task dependencies do.
do_compile[depends] += " \
    virtual/kernel:do_deploy \
    ${PLUTO_FIT_IMAGE}:do_image_complete \
    pluto-bitstream:do_deploy \
    "

do_configure[noexec] = "1"

do_compile() {
    fitdir=${B}/fit
    rm -rf $fitdir
    mkdir -p $fitdir

    # The stock u-boot environment edits nodes under /amba. The ADI kernel
    # names this node "axi". Rename it.
    for dtb in ${PLUTO_FIT_DTBS}; do
        dtc -q -@ -I dtb -O dts ${DEPLOY_DIR_IMAGE}/$dtb.dtb \
            | sed 's/axi {/amba {/g' \
            | dtc -q -@ -I dts -O dtb -o $fitdir/$dtb.dtb
    done

    cp -L ${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE} $fitdir/zImage
    cp -L ${DEPLOY_DIR_IMAGE}/${PLUTO_FIT_IMAGE}-${MACHINE}.rootfs.cpio.gz $fitdir/rootfs.cpio.gz
    cp -L ${DEPLOY_DIR_IMAGE}/pluto-default.bit $fitdir/system_top.bit

    sed -e "s|@FITDIR@|$fitdir|g" ${S}/pluto.its.in > ${B}/pluto.its
    mkimage -f ${B}/pluto.its ${B}/pluto.itb

    size=$(stat -c %s ${B}/pluto.itb)
    if [ "$size" -gt "${PLUTO_FIT_MAX_SIZE}" ]; then
        bbfatal "pluto.itb is $size bytes. The maximum is ${PLUTO_FIT_MAX_SIZE} bytes (qspi-linux partition)."
    fi

    # .frm: the image followed by its ASCII MD5 sum.
    md5sum ${B}/pluto.itb | cut -d ' ' -f 1 > ${B}/pluto.itb.md5
    cat ${B}/pluto.itb ${B}/pluto.itb.md5 > ${B}/pluto.frm

    # .dfu: the image followed by a DFU suffix.
    cp ${B}/pluto.itb ${B}/pluto.dfu
    dfu-suffix -a ${B}/pluto.dfu -v ${PLUTO_DFU_VID} -p ${PLUTO_DFU_PID}
}

do_deploy() {
    install -m 0644 ${B}/pluto.itb ${B}/pluto.frm ${B}/pluto.dfu ${DEPLOYDIR}/
}
addtask deploy after do_compile before do_build
