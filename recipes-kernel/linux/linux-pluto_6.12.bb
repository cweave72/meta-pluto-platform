SUMMARY = "ADI Linux kernel for the ADALM-PLUTO"
DESCRIPTION = "Analog Devices Linux kernel (Xilinx release branch) with the \
Pluto device trees, zynq_pluto_defconfig, and the AD9361 family drivers."

require recipes-kernel/linux/linux-yocto.inc

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

KBRANCH = "xlnx/release/v6.12.y-2026r1"
SRC_URI = "git://github.com/analogdevicesinc/linux.git;protocol=https;branch=${KBRANCH};name=machine"
SRCREV_machine = "b486fe824008ce25f56036b03d891063e6f233a8"

# Must agree with VERSION.PATCHLEVEL.SUBLEVEL in the Makefile at SRCREV_machine.
LINUX_VERSION = "6.12.77"
LINUX_VERSION_EXTENSION = ""
PV = "${LINUX_VERSION}+git"

# Clone with a short history. The full ADI history is several GB.
BB_GIT_SHALLOW = "1"

# Base configuration from the kernel tree. kernel-yocto merges the .cfg
# fragments in SRC_URI on top of it.
KBUILD_DEFCONFIG = "zynq_pluto_defconfig"
KCONFIG_MODE = "alldefconfig"

COMPATIBLE_MACHINE = "^pluto$"
