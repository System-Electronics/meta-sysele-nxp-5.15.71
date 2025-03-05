FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

# git instead of gitscm so that we can patch .gitmodules
NNSHARK_SRC = "git://github.com/nxp-imx/nnshark.git;protocol=https"
SRC_URI += "file://0001-fix-anongit-submodule.patch"

do_fix_submodule() {
    cd "${S}"
    git submodule update --init --recursive
}
do_fix_submodule[network] = "1"

addtask do_fix_submodule after do_patch before do_configure
