![System Electronics](https://www.systemelectronics.com/wp-content/uploads/2023/01/cropped-System_Electronics_Coesia_Logo_Vector_CMYK-003.png)

----

# Yocto Project setup for ASTRIAL board [SYSTEM ELECTRONICS]

WARNING: the build requires Ubuntu 22.04 or 20.04. It fails on 18.04

## Prerequisites

This guide is targeted and tested on a x86-64bit PC with **Ubuntu 22.04 64bit**.

* **IMPORTANT** : is mandatory to have 200GB free space on storage !

Minimum suggested requirements:

* at least 4 cores
* at least 16BG RAM
* at least 250GB storage (possibly a SSD)

The list of packages needed on the development host is extensive, and varies depending on the features to be built in the image for the Yocto target. The packages that must necessarily be installed on the system are listed below.


## Setup Lubuntu 22.04

Install Ubuntu 22.04 (or equivalent like Lubuntu)

Install the required packages

```bash
sudo apt update
sudo apt upgrade
sudo apt install gawk wget git diffstat unzip texinfo gcc build-essential chrpath socat cpio python3 python3-pip python3-pexpect xz-utils debianutils iputils-ping python3-git python3-jinja2 libegl1-mesa libsdl1.2-dev python3-subunit mesa-common-dev zstd liblz4-tool file locales libacl1
sudo apt  install curl python-is-python3
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


* When you need to Initialize the build environment later

```bash
source setup-environment build
```


##  Build full rootfs image

```bash
bitbake -k system-astrial-image
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


## Build issues

The build involves a lot of packages and in case you face to issues at the end on Qt or Rust, 
please reduce the cores enabled for the build.

In **local.conf** set the following variable

```python
BB_NUMBER_THREADS="2"
```

The build may fail many times. In that case restart bitbake again and again until it works.


---------------------------

# Board programming

```bash
sudo apt-get install bmap-tools
```

```bash
sudo umount /media/tux/*

sudo bmaptool copy imx-image-full-imx8mp-lpddr4-evk.wic.zst /dev/sdX
```

---------------------

# Serial debug port

This board provides a **3v3 TTL debug UART** on the connector RPi HAT (J8), pins 6, 8, 10.

You can use a FTDI 3v3 UART to USB adapter to connectt the board to a PC.

```bash
picocom -b 115200 /dev/ttyUSB0
```
