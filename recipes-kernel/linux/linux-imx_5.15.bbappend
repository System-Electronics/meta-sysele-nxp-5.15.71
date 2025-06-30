#
# Copyright (C)2024 KOAN sas - <https://koansoftware.com>
#

# change the NXP repo with the System Electronics custom one
KERNEL_SRC = "git://github.com/System-Electronics/linux-imx-lf-5.15.71;protocol=https;branch=${SRCBRANCH}"
SRCBRANCH = "arducam"
SRCREV = "ba3bd049e17984700d583e7cc7fe9640456acf92"

# set local version
LOCALVERSION = "-sysele"

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://0001-backport-pca9450-driver-from-upstream-83808c5.patch"
SRC_URI += "file://0002-arducam-pivariety-2lane-cam0.patch"
SRC_URI += "file://caam.cfg"
