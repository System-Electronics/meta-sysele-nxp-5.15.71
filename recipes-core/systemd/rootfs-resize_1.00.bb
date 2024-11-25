LICENSE = "CLOSED"

inherit systemd

RDEPENDS:${PN}:append = "bash"

SYSTEMD_AUTO_ENABLE = "enable"
SYSTEMD_SERVICE:${PN} = "rootfs-resize.service"

SRC_URI:append = "file://rootfs-resize.service \
                  file://rootfs-resize.sh \
                  "

FILES:${PN}:append = "${systemd_system_unitdir}/rootfs-resize.service"
FILES:${PN}:append = "${bindir}/rootfs-resize.sh"

do_install() {
  install -d ${D}/${bindir}
  install -m 0755 ${WORKDIR}/rootfs-resize.sh ${D}/${bindir}

  install -d ${D}/${systemd_unitdir}/system
  install -m 0644 ${WORKDIR}/rootfs-resize.service ${D}/${systemd_system_unitdir}
}
