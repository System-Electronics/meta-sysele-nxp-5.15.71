SUMMARY = "Automagic shell tab completion for Python CLI applications"
HOMEPAGE = "https://pypi.org/project/shtab"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENCE;md5=03492e75d06e3c4883c2f0567dca05f0"

SRC_URI[md5sum] = "d9e0f4e3a1c46dfc7d1cce2b10aaf217"
SRC_URI[sha256sum] = "decc78082c3ffb518c1dfd3a8da99653a2d47e58e3104197bce8ae6507dad78b"

# later versions do not have setup.py, but only pyproject.toml which is not yet
# supported by itself by setuptools in kirkstone
PV = "1.6.1"

DEPENDS = "python3-pip-native python3-setuptools-scm-native"

inherit pypi setuptools3

BBCLASSEXTEND = "native"
