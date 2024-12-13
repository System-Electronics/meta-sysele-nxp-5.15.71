SUMMARY = "Python command-line client for tldr pages"
HOMEPAGE = "https://github.com/tldr-pages/tldr-python-client"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=df0ce3f2490feb816da35cf659dbf99e"

DEPENDS = "python3-pip-native python3-wheel-native"
RDEPENDS:${PN} += " \
    python3-termcolor \
    python3-colorama \
    python3-shtab (>= 1.3.10) \
"

SRC_URI = "git://github.com/tldr-pages/tldr-python-client;protocol=https;nobranch=1"
SRCREV = "3bc6ba4c4eba3f0c0f4e4af4c5d4168ddb3cadd1"

S = "${WORKDIR}/git"

# tar.gz is missing requirements file needed by 'setup.py', so we can't rely on pypi package
inherit setuptools3

BBCLASSEXTEND = "native"
