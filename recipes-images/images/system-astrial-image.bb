#
# Copyright (C)2024 KOAN sas - <https://koansoftware.com>
#

require dynamic-layers/qt6-layer/recipes-fsl/images/imx-image-full.bb

IMAGE_FEATURES:append = " debug-tweaks ssh-server-openssh package-management"

################################################################################################################
## as per NXP YOCTO user guide section 5.6.7
IMAGE_INSTALL:append = " libgpiod libgpiod-dev libgpiod-tools htop mc "
IMAGE_INSTALL:append = " packagegroup-imx-ml "
TOOLCHAIN_TARGET_TASK:append = " tensorflow-lite-dev onnxruntime-dev "

#################################################################################################################
## Hailo
IMAGE_INSTALL:append = " libhailort hailortcli pyhailort libgsthailo hailo-pci hailo-firmware"
IMAGE_INSTALL:append = " libgsthailotools hailo-post-processes"
IMAGE_INSTALL:append = " tappas-apps"

#################################################################################################################
## KOAN + Kalpa
IMAGE_INSTALL:append = " \
    v4l-utils \
    packagegroup-fsl-gstreamer1.0 \
    packagegroup-fsl-gstreamer1.0-full \
    parted \
    rootfs-resize \
    "
