SUMMARY = "Change default shell to bash"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit update-alternatives

RDEPENDS:${PN} += "bash"

do_install() {
    install -d ${D}${base_bindir}
    touch ${D}${base_bindir}/custom-shell-placeholder
    
    # Create script directory
    install -d ${D}${bindir}
    
    # Create wrapper script for SHELL exportation
    cat > ${D}${bindir}/weston-terminal.wrapped << 'EOF'
#!/bin/sh
# Set SHELL variable to bash
export SHELL=/bin/bash
exec /usr/bin/weston-terminal.real --shell=/bin/bash "$@"
EOF
    
    # Change script permissions
    chmod 755 ${D}${bindir}/weston-terminal.wrapped
}

pkg_postinst_ontarget:${PN}() {
    if [ -f /etc/passwd ]; then
        sed -i 's|:/bin/sh$|:/bin/bash|' /etc/passwd
        rm -f /bin/custom-shell-placeholder
    fi
    
    # Fix symbolic link
    rm -f /bin/sh
    ln -sf bash /bin/sh
    
    # Setup weston-terminal wrapper
    if [ -f /usr/bin/weston-terminal ] && [ ! -L /usr/bin/weston-terminal ]; then
        mv /usr/bin/weston-terminal /usr/bin/weston-terminal.real
        cp /usr/bin/weston-terminal.wrapped /usr/bin/weston-terminal
        chmod 755 /usr/bin/weston-terminal
    fi
}

ALTERNATIVE:${PN} = "sh"
ALTERNATIVE_LINK_NAME[sh] = "${base_bindir}/sh"
ALTERNATIVE_TARGET[sh] = "${base_bindir}/bash"
ALTERNATIVE_PRIORITY = "100"

FILES:${PN} += "${bindir}/weston-terminal.wrapped"