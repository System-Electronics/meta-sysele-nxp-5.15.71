#
# Copyright (C)2024 KOAN sas - <https://koansoftware.com>
#

SUMMARY = "bitbake-layers recipe"
DESCRIPTION = "Recipe created by bitbake-layers"
LICENSE = "MIT"

python do_display_banner() {
    bb.plain("***********************************************");
    bb.plain("*                                             *");
    bb.plain("*  Example recipe created by KOAN             *");
    bb.plain("*                                             *");
    bb.plain("*  https://koansoftware.com                   *");
    bb.plain("*                                             *");
    bb.plain("***********************************************");
}

addtask display_banner before do_build
