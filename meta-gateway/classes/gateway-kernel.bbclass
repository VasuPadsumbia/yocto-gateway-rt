# SPDX-License-Identifier: MIT
# Common helpers for custom kernel handling
inherit kernel
inherit deploy

# Change the image type based on the machine
KERNEL_IMAGETYPE ?= "Image"
#KERNEL_IMAGETYPE ?= "zImage"  # For 32-bit ARM

# Device Tree Compiler flags
EXTRA_OEMAKE += "DTC_FLAGS='-@ -H epapr'"

# Copy the device tree blob to the deploy directory
python do_deploy:append() {
    import os, glob, shutil
    ovsrc = os.path.join(d.getVar('B'), 'arch', 'arm64', 'boot', 'dts', 'overlays', 'dtb')
    ovdst = os.path.join(d.getVar('DEPLOYDIR'), 'overlays')
    if os.path.isdir(ovsrc):
        bb.utils.mkdirhier(ovdst)
        for overlay in glob.glob(os.path.join(ovsrc, '*.dtbo')):
            shutil.copy2(overlay, ovdst)
    else:
        bb.warn("No DTB overlays found to deploy at: %s." % ovsrc)
}
