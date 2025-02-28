DESCRIPTION = "NXP Plug and Trust Middleware"
LICENSE = "CLOSED & MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

NXP_PLUG_TRUST_MW = "${PN}_mw_v${PV}"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

inherit dos2unix cmake setuptools3

DEPENDS += "openssl"
RDEPENDS:${PN} += "\
    bash \
    python3-cffi \
    python3-cryptography \
    python3-click \
    func-timeout \
    "

# Allow symlink .so in non-dev package
INSANE_SKIP:${PN} += "dev-so"

S = "${WORKDIR}/${NXP_PLUG_TRUST_MW}/simw-top"
B = "${WORKDIR}/${NXP_PLUG_TRUST_MW}/build"

SMW_URI = "file://${NXP_PLUG_TRUST_MW}.zip"
SRC_URI = "${SMW_URI} \
           file://0001-fix-openssl-3-compatibility.patch;patchdir=.. \
           file://0002-fix-core-json-as-static-library.patch;patchdir=.. \
           file://0003-fix-remove-use-of-missing-python-cryptography-api.patch;patchdir=.. \
           file://0004-fix-pycli-setup.py.patch;patchdir=.. \
           file://0005-feat-install-se05x_MandatePlatform_SCP.patch;patchdir=.. \
           "

EXTRA_OECMAKE:append = "\
    -DCMAKE_BUILD_TYPE=Release \
    -DWithSharedLIB=ON \
    -DPTMW_Host=iMXLinux \
    -DPTMW_HostCrypto=OPENSSL \
    -DPTMW_SMCOM=T1oI2C \
    -DPTMW_Applet=SE05X_E \
    -DPTMW_SE05X_Ver=07_02 \
    -DPTMW_SCP=SCP03_SSS \
    -DPTMW_SE05X_Auth=PlatfSCP03 \
    -DPAHO_BUILD_DEB_PACKAGE=OFF \
    -DPAHO_BUILD_DOCUMENTATION=OFF \
    -DPAHO_BUILD_SHARED=ON \
    -DPAHO_BUILD_STATIC=OFF \
    -DPAHO_BUILD_SAMPLES=OFF \
    -DPAHO_ENABLE_CPACK=OFF \
    -DPAHO_ENABLE_TESTING=OFF \
    -DPAHO_WITH_SSL=ON \
    "

SETUPTOOLS_SETUP_PATH = "${S}/pycli/src"

FILES:${PN} += "${datadir}/se05x \
                ${bindir} \
                ${libdir}/*.so \
                ${libdir}/*.so.* \
                "
FILES:${PN}-dev = "${includedir} ${libdir}/cmake"

python do_check_smw_uri () {
    # manually check for SMW_URI to better explain why it is missing and where to get it
    smw_uri = d.getVar("SMW_URI") or ""
    try:
        fetcher = bb.fetch2.Fetch([smw_uri], d)
        fetcher.checkstatus()
    except bb.fetch2.BBFetchException:
        bb.fatal(f"{smw_uri} is missing. This is a proprietary NXP package that you can download from here:\n" \
                  "\thttps://www.nxp.com/webapp/Download?colCode=SE05x-PLUG-TRUST-MW&appType=license")
}
addtask do_check_smw_uri before do_fetch

do_configure() {
    cmake_do_configure
    setuptools3_do_configure
}

do_compile () {
    cmake_do_compile
    setuptools3_do_compile
}

do_install () {
    cmake_do_install
    setuptools3_do_install
}
