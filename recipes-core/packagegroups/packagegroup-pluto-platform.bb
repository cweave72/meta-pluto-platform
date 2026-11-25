SUMMARY = "Pluto platform base packages"
DESCRIPTION = "Packages that every Pluto platform image contains. \
conf/machine/pluto.conf adds this group to packagegroup-core-boot \
through MACHINE_ESSENTIAL_EXTRA_RDEPENDS."

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

# Add base packages here. Each image built for MACHINE = "pluto" gets them.
RDEPENDS:${PN} = " \
    packagegroup-core-ssh-dropbear \
    pluto-usb-gadget \
    "
