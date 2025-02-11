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
**Note:** if the image is built for the first time, please see also [SE05x error during build](#se05x-error-during-build)

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


##  Build secure boot image

Download the Code Signing Tools by NXP from [this link](https://www.nxp.com/webapp/Download?colCode=IMX_CST_TOOL_NEW&appType=license) and extract it to some location.

Add the location of the extracted package to local.conf as:

```
CST_PATH = "/path/to/extracted/cst-4.0.0"
```

---
The following steps are required if you want to generate your own set of keys, otherwise the build will use the default ones (not recommend for production).

```bash
$ cd /path/to/extracted/cst-4.0.0/keys
```

Create a text file called "serial", which contains 8 digits. It will be used for the certificate serial numbers.

```bash
echo 12345678 > serial
```

Create a text file called "key_pass.txt", which contains two lines of a password repeated twice.
This password will be used to protect the generated private keys.
All private keys in the PKI tree are in PKCS #8 format will be protected by the same password.

```bash
echo mypassword >  key_pass.txt
echo mypassword >> key_pass.txt
```

Now, to generate the PKI tree, run the following:

```bash
./hab4_pki_tree.sh
```

And complete the interactive questions. For example:

```
Do you want to use an existing CA key (y/n)?: n

Key type options (confirm targeted device supports desired key type):
Select the key type (possible values: rsa, rsa-pss, ecc)?: rsa
Enter key length in bits for PKI tree: 4096
Enter PKI tree duration (years): 20
How many Super Root Keys should be generated? 4
Do you want the SRK certificates to have the CA flag set? (y/n)?: y
```

Generate Super Root Key (SRK) table
```bash
cd ../crts/
../linux64/bin/srktool -h 4 -t SRK_1_2_3_4_table.bin -e SRK_1_2_3_4_fuse.bin -d sha256 -c ./SRK1_sha256_4096_65537_v3_ca_crt.pem,./SRK2_sha256_4096_65537_v3_ca_crt.pem,./SRK3_sha256_4096_65537_v3_ca_crt.pem,./SRK4_sha256_4096_65537_v3_ca_crt.pem -f 1
```

To customize the signing process of `cst_signer` (see [nxp-cst-signer](https://github.com/nxp-imx-support/nxp-cst-signer)), add a `csf_hab4.cfg` file inside `CST_PATH`.
For example:

```
#Header
header_version=4.3
header_eng=CAAM
header_eng_config=0
#Install SRK
srktable_file=SRK_1_2_3_4_table.bin
srk_source_index=0
#Install NOCAK
nocak_file=
#Install CSFK
csfk_file=CSF1_1_sha256_4096_65537_v3_usr_crt.pem
#Authenticate CSF
#Unlock
unlock_engine=CAAM
unlock_features=MID
unlock_uid=
#Install Key
img_verification_index=0
img_target_index=2
img_file=IMG1_1_sha256_4096_65537_v3_usr_crt.pem
#Authenticate Data
auth_verification_index=2
```

Finally, build the final image:

```bash
bitbake system-astrial-image-secure-boot
```

For more information about HABv4 and iMX Secure Boot see the following documents:
- <https://github.com/u-boot/u-boot/blob/master/doc/imx/habv4/introduction_habv4.txt>
- <https://community.nxp.com/pwmxy87654/attachments/pwmxy87654/imx-processors/202591/1/CST_UG.pdf>
- <https://www.nxp.com/doc/AN4581>

# Board programming

The artifacts are located in the directory `tmp/deploy/images/astrial-imx8mp`.

The files needed to program the board are the following:

- **imx-boot-astrial-imx8mp-sd.bin-flash_evk**
- **system-astrial-image-astrial-imx8mp-20240301095121.rootfs.wic.zst**

Or, if built with secure boot:
- **signed-imx-boot-astrial-imx8mp-sd.bin-flash_evk**
- **system-astrial-image-secure-boot-astrial-imx8mp-20240301095121.rootfs.wic.zst**
- **SRK_1_2_3_4_fuse.bin.u-boot-cmds**
    - with reference commands for efuse programming

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


## Verify Secure Boot

HAB generates events when processing the commands if it encounters issues.
The U-Boot `hab_status` command displays any events that were generated.
Run it at the U-Boot command line:

```
=> hab_status
```

If everything is okay you should get the following output:

```
Secure boot disabled

HAB Configuration: 0xf0, HAB State: 0x66
No HAB Events Found!
```

The same output should be displayed when booting the signed kernel image.


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

## SE05x error during build
If the full image is built for the first time, the recipe `se05x` will cause an error. To solve it, please see the guide [here](https://github.com/System-Electronics/meta-sysele-nxp-5.15.71/blob/main/ASTRIAL-SE05x.md).
