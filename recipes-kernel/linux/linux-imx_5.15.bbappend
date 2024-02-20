#
# Copyright (C)2024 KOAN sas - <https://koansoftware.com>
#

# change the NXP repo with the System Electronics custom one
KERNEL_SRC = "git://github.com/System-Electronics/linux-imx-lf-5.15.71;protocol=https;branch=${SRCBRANCH}"
SRCBRANCH = "main"
SRCREV = "5005dffbee28eee977b1f243ec47822670e4a018"

# set local version
LOCALVERSION = "-sysele"
