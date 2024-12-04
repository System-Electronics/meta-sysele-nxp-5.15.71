#
# Copyright (C)2024 KOAN sas - <https://koansoftware.com>
#

# change the NXP repo with the System Electronics custom one
KERNEL_SRC = "git://github.com/System-Electronics/linux-imx-lf-5.15.71;protocol=https;branch=${SRCBRANCH}"
SRCBRANCH = "main"
SRCREV = "07482dfaaa1a49c57f88955ce5b9fdbf1af07a35"

# set local version
LOCALVERSION = "-sysele"

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://0001-backport-pca9450-driver-from-upstream-83808c5.patch"
