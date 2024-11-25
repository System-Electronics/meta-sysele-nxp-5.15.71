#!/bin/bash
set -e

device=$(eval $(lsblk -oMOUNTPOINT,PKNAME -P -M | grep 'MOUNTPOINT="/"'); echo $PKNAME)
partition=$(eval $(lsblk -oMOUNTPOINT,NAME -P -M | grep 'MOUNTPOINT="/"'); echo $NAME)
# partition = ${device}p${N}, retrieve N
partition_num=${partition##"${device}p"}

logger "resizing ${partition}"

parted /dev/${device} resizepart ${partition_num} 100%
resize2fs /dev/${partition}

logger "remove rootfs-resize service and files"

systemctl disable rootfs-resize.service
rm -f /etc/systemd/system/rootfs-resize.service
rm -f /usr/bin/rootfs-resize.sh
