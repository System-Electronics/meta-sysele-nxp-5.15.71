HOMEPAGE = "https://github.com/kata198/func_timeout"
SUMMARY = "Python module which allows you to specify timeouts when calling any existing function. Also provides support for stoppable-threads"
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e6a600fd5e1d9cbde2d983680233ad02"

SRC_URI = "https://files.pythonhosted.org/packages/b3/0d/bf0567477f7281d9a3926c582bfef21bff7498fc0ffd3e9de21811896a0b/func_timeout-${PV}.tar.gz"
SRC_URI[md5sum] = "3535d4e00d54e36757ba7c65f20e4c91"
SRC_URI[sha256sum] = "74cd3c428ec94f4edfba81f9b2f14904846d5ffccc27c92433b8b5939b5575dd"

S = "${WORKDIR}/func_timeout-${PV}"

inherit setuptools3
