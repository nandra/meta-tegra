require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

COMPATIBLE_MACHINE = "(tegra186)"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"

inherit systemd

do_configure() {
	tar -C ${B} -x -f ${S}/nv_tegra/nv_tools.tbz2 usr/sbin
        tar -C ${B} -x -f ${S}/nv_tegra/config.tbz2 etc
}

do_compile() {
	:
}

do_install() {
	install -d ${D}${sbindir}
	install -m 0755 ${B}/usr/sbin/nvbootctrl ${D}${sbindir}
	install -m 0755 ${B}/usr/sbin/nv_bootloader_payload_updater ${D}${sbindir}
	install -m 0755 ${B}/usr/sbin/nv_update_engine ${D}${sbindir}
	install -d ${D}${sysconfdir}
	install -m 0644 ${B}/etc/nv_boot_control.conf ${D}${sysconfdir}
	install -d ${D}${systemd_system_unitdir}
	install -m 0644 ${B}/etc/systemd/system/nv_update_verifier.service ${D}${systemd_system_unitdir}
	sed -i -e's,^After=nv\.service,After=nvstartup.service,' ${D}${systemd_system_unitdir}/nv_update_verifier.service
	install -d ${D}/opt/ota_package
	install -d ${D}${datadir}/nv_tegra/rollback
	install -m 0644 ${S}/bootloader/rollback/rollback.cfg ${D}${datadir}/nv_tegra/rollback
}

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"
PACKAGES = "${PN} ${PN}-dev"
FILES_${PN} += "/opt/ota_package"
FILES_${PN}-dev = "${datadir}/nv_tegra/rollback"
SYSTEMD_SERVICE_${PN} = "nv_update_verifier.service"
INSANE_SKIP_${PN} = "ldflags"