#
# Copyright (C)2024 KOAN sas - <https://koansoftware.com>
#

# change the NXP repo with the System Electronics custom one
KERNEL_SRC = "git://github.com/System-Electronics/linux-imx-lf-5.15.71;protocol=https;branch=${SRCBRANCH}"
SRCBRANCH = "main"
SRCREV = "e6ab9f07454504def424200d5673be22d318a242"

# set local version
LOCALVERSION = "-sysele"
