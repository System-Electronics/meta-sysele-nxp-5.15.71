<p align="center">
  <img src="https://github.com/System-Electronics/meta-sysele-nxp-5.15.71/blob/main/se_logo.png"/>
</p>

----

Yocto Layer for System Electronics SoM 'Astrial'
================================================================

This Yocto Project / OpenEmbedded layer **meta-sysele-nxp-5.15.71** provides the distro 
configuration used to build the demo images provided for **System Electronics 'Astrial'** system on module.

Dependencies
============

The setup is strictly alilgned to the version NXP imx-5.15.71-2.2.0

Building
========

See the [ASTRIAL-YOCTO-INSTALL.md](https://github.com/System-Electronics/meta-sysele-nxp-5.15.71/blob/main/ASTRIAL-YOCTO-INSTALL.md) in **meta-sysele-nxp-5.15.71** layer.

Patches
=======

This layer is maintained by System Electronics.

When creating a patch of the last commit, use

    git format-patch -s --subject-prefix='meta-sysele][<branch>][PATCH' -1

To send patches, use

    git send-email --to github@systemelectronics.com <generated patch>


License
=======

All metadata is MIT licensed unless otherwise stated. Source code and
binaries included in tree for individual recipes is under the LICENSE
stated in each recipe (.bb file) unless otherwise stated.
