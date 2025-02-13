update_file() {
    if ! grep -q "$1" $3; then
        bbfatal $1 not found in $3
    fi
    sed -i -e "s,$1,$2," $3
}

do_install:append() {
    # Inside meta-imx, NXP restored "root" as the default user, but they forgot to update the WorkingDirectory
    update_file "WorkingDirectory=/home/weston" "WorkingDirectory=/home/root" ${D}${systemd_system_unitdir}/weston.service
}
