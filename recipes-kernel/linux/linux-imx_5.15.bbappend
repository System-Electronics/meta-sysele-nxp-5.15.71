#
# Copyright (C)2024 KOAN sas - <https://koansoftware.com>
#

# change the NXP repo with the System Electronics custom one
KERNEL_SRC = "git://github.com/System-Electronics/linux-imx-lf-5.15.71;protocol=https;branch=${SRCBRANCH}"
SRCBRANCH = "main"
SRCREV = "cdd10b801345b9b30a9ad986da303becc7809c11"

# set local version
LOCALVERSION = "-sysele"
