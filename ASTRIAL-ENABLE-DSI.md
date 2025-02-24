# How to configure DSI port

## Tested Display
- [Raspberry Pi 7-inch Display](https://www.raspberrypi.com/products/raspberry-pi-touch-display/) 
- [Waveshare 5-inch Display](https://www.waveshare.com/5inch-dsi-lcd-c.htm)
- [Waveshare 10.1-inch Display](https://www.waveshare.com/10.1inch-dsi-lcd-c.htm)

## Enable DSI
1. Connect the DSI cable to the DISP1 port on the Carrier Board (note: DISP0 is **not** functioning)

2. Connect the other end of the DSI cable to your DSI display

3. For Raspberry7inch and Waveshare10inch, connect the 5V and GND pins to the corresponding pins on the GPIO pinout on the carrier board. For example, you can use GPIO2 (5V) and GPIO39 (GND)

4. On the board, navigate to the mounted boot partition:
```bash
# if booted from SD Card
cd /run/media/boot-mmcblk1p1/
# if booted from eMMC
cd /run/media/boot-mmcblk2p1/
```

5. Replace the default DTB file with the one with the DSI node enabled:
```bash
# backup the current dtb
cp imx8mp-astrial.dtb imx8mp-astrial.dtb_default
# for Raspberry7inch and Waveshare5inch
cp imx8mp-astrial-dsi-rpi.dtb imx8mp-astrial.dtb
# for Waveshare10inch
cp imx8mp-astrial-dsi-waveshare.dtb imx8mp-astrial.dtb
```

6. Reboot the board
```bash
sync
reboot
```

## Known Issues
Raspberry driver try to read the firmware version of the display through I2C, so be sure to connect properly **Raspberry7inch** or **Waveshare5inch** display to the carrier board. Otherwhise, NXP DRM driver initialization will fail, preventing the kernel to complete the boot.  

This problem has not been observed with Waveshare driver. So in this case, the driver is loaded correctly and the DRM is initialized even if the **Waveshare10inch** display is missing.


