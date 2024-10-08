<p align="center">
  <img src="https://github.com/System-Electronics/meta-sysele-nxp-5.15.71/blob/main/se_logo.png"/>
</p>

----

# Yocto Project setup for ASTRIAL board [SYSTEM ELECTRONICS]

WARNING: the build requires Ubuntu 22.04 or 20.04. It fails on 18.04

## Prerequisites

This guide is targeted and tested on a x86-64bit PC with **Ubuntu 22.04 64bit**.

* **IMPORTANT** : is mandatory to have 200GB free space on storage !

The suggested requirements are:

* at least 8 cores
* at least 32BG RAM
* at least 250GB storage (possibly a SSD)

The list of packages needed on the development host is extensive, and varies depending on the features to be built in the image for the Yocto target. The packages that must necessarily be installed on the system are listed below.


## Setup Lubuntu 22.04

Install Ubuntu 22.04 (or equivalent like Lubuntu)

Install the required packages

```bash
sudo apt update
sudo apt upgrade
sudo apt install gawk wget git diffstat unzip texinfo gcc build-essential chrpath socat cpio python3 python3-pip python3-pexpect xz-utils debianutils iputils-ping python3-git python3-jinja2 libegl1-mesa libsdl1.2-dev python3-subunit mesa-common-dev zstd liblz4-tool file locales libacl1
sudo apt install curl python-is-python3
sudo apt-get install bmap-tools
sudo locale-gen en_US.UTF-8
```

---------------

## Install the repo utility:

To use this manifest repo, the repo tool must be installed first.

```bash
mkdir ~/bin
curl http://commondatastorage.googleapis.com/git-repo-downloads/repo  > ~/bin/repo
chmod a+x ~/bin/repo
PATH=${PATH}:~/bin
```

**NOTE**: Do not use the repo package provided by your distro.

## Configure git

```bash
  git config --global user.email "you@example.com"
  git config --global user.name "Your Name"
```

-----------

## Setup Yocto environment

Create the working direrctory

```bash
mkdir yocto-astrial && cd yocto-astrial
```

## Initialize repo manifest

The setup is strictly alilgned to the version NXP imx-5.15.71-2.2.0

* NXP repositories

```bash
repo init -u https://github.com/nxp-imx/imx-manifest -b imx-linux-kirkstone -m imx-5.15.71-2.2.0.xml
```

* System Electronics and Hailo repositories

```
mkdir -p .repo/local_manifests
wget --directory-prefix .repo/local_manifests https://raw.githubusercontent.com/System-Electronics/meta-sysele-nxp-5.15.71/main/manifest/astrial-5.15.71.xml
```

* Sync Them all

```bash
repo sync
```

## Setup build environment MX8-EVK

* Initialize the build environment the first time - ( **First time only !** )

```bash
source astrial-setup-env -b build
```


* When you need to initialize the build environment later

```bash
source setup-environment build
```


##  Build full rootfs image

```bash
bitbake system-astrial-image
```

You will see a message screen like this

```bash
Build Configuration:
BB_VERSION           = "2.0.0"
BUILD_SYS            = "x86_64-linux"
NATIVELSBSTRING      = "ubuntu-22.04"
TARGET_SYS           = "aarch64-poky-linux"
MACHINE              = "astrial-imx8mp"
DISTRO               = "fsl-imx-xwayland"
DISTRO_VERSION       = "5.15-kirkstone"
TUNE_FEATURES        = "aarch64 armv8a crc crypto"
TARGET_FPU           = ""
```
---------------------------

# Board programming

The artifacts are located in the directory `tmp/deploy/images/astrial-imx8mp`.

The files needed to program the board are the following:

- **imx-boot-astrial-imx8mp-sd.bin-flash_evk**
- **system-astrial-image-astrial-imx8mp-20240301095121.rootfs.wic.zst**

**NOTE**: replace the timestamp '20240301095121' with the proper one.


## MicroSD programming

**NOTE**: replace `/dev/sdX` with a proper device name.

```bash
sudo umount /dev/sdX*

sudo bmaptool copy system-astrial-image-astrial-imx8mp-20240301095121.rootfs.wic.zst /dev/sdX
```

## eMMC programming


Program eMMC with the NXP **uuu** (Universal Update Utility) valid for Linux, Windows, MacOS(not test yet).

## Get the program 'uuu'

Download the latest stable version, or ver. 1.5.125
https://github.com/nxp-imx/mfgtools/releases/tag/uuu_1.5.125

```
wget https://github.com/nxp-imx/mfgtools/releases/download/uuu_1.5.125/uuu
```

And change execution permissions (Linux only)

```
chmod +x ./uuu
```

In case you want to use `uuu` for Windows, please use the file `uuu.exe`


## Reset the board to USB-OTG programming mode

In case you need to restore the Astrial SoM to its factory state, delete the existing bootloder from eMMC.

Enter in the u-boot shell pressing a key in the serial terminal during boot, then enter the following commands:

```
mmc dev 2 1
mmc erase 0 0x1000
reset
```

## Program the eMMC

Uncompress the file **wic.zst**

```bash
unzstd -d system-astrial-image-astrial-imx8mp-20240301095121.rootfs.wic.zst
```

1. Connect a microUSB cable to USB-OTG (J11) on the **Raspberry CM4-IO board** to your PC.
1. Run the following command to program the eMMC

```bash
sudo ./uuu -b emmc_all imx-boot-astrial-imx8mp-sd.bin-flash_evk system-astrial-image-astrial-imx8mp-20240301095121.rootfs.wic
```

At the end, power off the board, disconnect the microUSB cable from USB-OTG and power it on again.


---------------------

# Serial debug port

This board provides a **3v3 TTL debug UART** on the connector RPi HAT (J8), pins 6, 8, 10.

You can use a FTDI 3v3 UART to USB adapter to connectt the board to a PC.

```bash
picocom -b 115200 /dev/ttyUSB0
```

-------------------


# Trobleshooting

* Native linux machine 

The suggested condition to avoid build issues is to run Yocto bitbake into a Linux native machine.

* Virtual machine or Docker

Even would be possible to have several possibilities is strongly suggested to avoid Virtual machines and avoid Docker.

## Build issues

On a powerful PC the build process should be straightforward, however in some specific conditions you may face to some issues.

The build involves a lot of packages and in case you face to issues at the end on Qt or Rust, please reduce the cores enabled for the build.

In **local.conf** set the following variable

```python
BB_NUMBER_THREADS="4"
```

The build may fail many times. In that case check that your PC meets the minimum requirements and restart bitbake again.


## Errors during package build

It is possible to overcome errors on specific package build, restarting its build from scratch and restart the normal `bitbake system-astrial-image`.

**NOTE**: replace PKGNAME with the package name you want to rebuild

```shell
bitbake -c cleansstate PKGNAME
bitbake system-astrial-image
```

## Test the USB-OTG connection

When the board doesn't have a bootloader programmed would be possible to run the command `uuu -lsusb` 
to test the USB-OTG connection is functional as expected. The expected output is the following:

```bash
sudo ./uuu -lsusb

uuu (Universal Update Utility) for nxp imx chips -- libuuu_1.5.125-0-gaeb3490

Connected Known USB Devices
	Path	 Chip	 Pro	 Vid	 Pid	 BcdVersion
	==================================================
	3:6	 MX865	 SDPS:	 0x1FC9	0x0146	 0x0002
```
