# SPDX-License-Identifier: MIT
# Generic: fetched FW and stage boot files into deploy directory
inherit deploy

# Firmware boot files source directory
FW_BOOT_SUBDIR ?= "boot"
S = "${WORKDIR}/git"
FW_BOOT_SRC_DIR = "${S}/${FW_BOOT_SUBDIR}"

# Match machine config's expected deploy path: gateway-firmware/${MACHINE}
FW_DEPLOY_DIR = "${DEPLOYDIR}/gateway-firmware/${MACHINE}"

#python do_deploy() {
#    import os, shutil
#    dstdir = os.path.join(d.getVar('DEPLOYDIR'), 'gateway-fw', d.getVar('MACHINE'))
#    srcdir = os.path.join(d.getVar('S'), d.getVar('FW_BOOT_SUBDIR'))
#    bb.utils.mkdirhier(dstdir)
#
#    if not os.path.isdir(srcdir):
#        bb.fatal("Firmware boot files source directory not found: {}".format(srcdir))
#    for item in os.listdir(srcdir):
#        s = os.path.join(srcdir, item)
#        d = os.path.join(dstdir, item)
#        if os.path.isdir(s):
#            shutil.rmtree(d)
#            shutil.copytree(s, d)
#        else:
#            shutil.copy2(s, d)
#}
do_deploy() {
  install -d ${FW_DEPLOY_DIR}

  if [ ! -d "${FW_BOOT_SRC_DIR}" ]; then
    echo "ERROR: Firmware boot source directory missing: ${FW_BOOT_SRC_DIR}" >&2
    echo "HINT: Did the firmware recipe fetch succeed? Check SRC_URI/SRCREV." >&2
    exit 1
  fi

  # Copy only specific expected boot files to keep deploy tidy.
  for f in bootcode.bin start.elf start4.elf fixup.dat fixup4.dat config.txt cmdline.txt; do
    for cand in "${FW_BOOT_SRC_DIR}/${f}" "${WORKDIR}/${f}" "${WORKDIR}/files/${f}"; do
      if [ -f "$cand" ]; then
        install -m 0644 "$cand" ${FW_DEPLOY_DIR}/$f
        break
      fi
    done
  done

  # Copy overlays (dtb/dtbo + overlay_map.dtb) into both firmware deploy dir
  # (preserving subdir layout) and DEPLOY_DIR_IMAGE/overlays so IMAGE_BOOT_FILES works.
  if [ -d "${FW_BOOT_SRC_DIR}/overlays" ]; then
    install -d ${FW_DEPLOY_DIR}/overlays
    install -d ${DEPLOY_DIR_IMAGE}/overlays
    for ov in ${FW_BOOT_SRC_DIR}/overlays/*.dtb ${FW_BOOT_SRC_DIR}/overlays/*.dtbo; do
      if [ -f "$ov" ]; then
        install -m 0644 "$ov" ${FW_DEPLOY_DIR}/overlays/
        base=$(basename "$ov")
        install -m 0644 "$ov" ${DEPLOY_DIR_IMAGE}/overlays/$base
      fi
    done
    if [ -f "${FW_BOOT_SRC_DIR}/overlays/overlay_map.dtb" ]; then
      install -m 0644 "${FW_BOOT_SRC_DIR}/overlays/overlay_map.dtb" ${FW_DEPLOY_DIR}/overlays/
      install -m 0644 "${FW_BOOT_SRC_DIR}/overlays/overlay_map.dtb" ${DEPLOY_DIR_IMAGE}/overlays/overlay_map.dtb
    fi
  else
    echo "WARNING: No overlays directory found under firmware; overlay_map.dtb will be missing" >&2
  fi
}
addtask deploy after do_install before do_build
