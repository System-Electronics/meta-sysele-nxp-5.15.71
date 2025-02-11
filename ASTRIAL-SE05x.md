# Astrial SE05x

## Reference
https://www.nxp.com/products/SE050

## Build Information
If you try to build the full image or the single recipe ```se05x``` for the first time, the following messages will be shown and the build will be interrupted:
```bash
WARNING: your/path/sources-extra/meta-sysele-nxp-5.15.71/recipes-security/se05x/se05x_04.05.01.bb: Unable to get checksum for se05x SRC_URI entry se05x_mw_v04.05.01.zip: file could not be found
```
```bash
ERROR: se05x-04.05.01-r0 do_check_smw_uri: file://se05x_mw_v04.05.01.zip is missing. This is a proprietary NXP package that you can download from here:
	https://www.nxp.com/webapp/Download?colCode=SE05x-PLUG-TRUST-MW&appType=license
```
To solve the problem:
1. Download the missing file from here https://www.nxp.com/webapp/Download?colCode=SE05x-PLUG-TRUST-MW&appType=license
2. Save the zip file in ```your/path/sources-extra/meta-sysele-nxp-5.15.71/recipes-security/se05x/files```
3. Restart the build process

## Example -How to use
1. Check the presence of the chip via i2c:
```bash
i2cdetect -y 2 0x48 0x48
```
output:
```bash
     0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f
00:                                                 
10:                                                 
20:                                                 
30:                                                 
40:                         48                      
50:                                                 
60:                                                 
70:
```
2. Check the presence of ```ssscli```:
```bash
ssscli --version
```
output:
```bash
ssscli, version v04.05.01
```

3. Establish and check the connection with the chip:
```bash
ssscli connect se05x t1oi2c /dev/i2c-2
ssscli se05x uid
```
output:
```bash
sss   :INFO :atr (Len=35)
      01 A0 00 00    03 96 04 03    E8 00 FE 02    0B 03 E8 00
      01 00 00 00    00 64 13 88    0A 00 65 53    45 30 35 31
      00 00 00
sss   :WARN :Communication channel is Plain.
sss   :WARN :!!!Not recommended for production use.!!!
INFO:sss.se05x:04005001029c6f7358447f043c6e9ab61d90
Unique ID: 04005001029c6f7358447f043c6e9ab61d90
```
The UID is unique, in case of different models with more updated Applets there may be the output:
```bash
sss   :INFO :Newer version of Applet Found
sss   :INFO :Compiled for 0x30100. Got newer 0x70200
```

4. Check the outputs of the following commands:
---
```bash
ssscli generate ecc 0x01010101 NIST_P192
```
output:
```bash
Generating ECC Key Pair at KeyID = 0x01010101,  curvetype=NIST_P192
sss   :INFO :atr (Len=35)
        01 A0 00 00    03 96 04 03    E8 00 FE 02    0B 03 E8 00
        01 00 00 00    00 64 13 88    0A 00 65 53    45 30 35 31
        00 00 00
sss   :WARN :Communication channel is Plain.
sss   :WARN :!!!Not recommended for production use.!!!
Generated ECC Key Pair at KeyID 0x01010101
```
---
```bash
ssscli se05x readidlist | grep 0X1010101
```
output:
```bash
Key-Id: 0X1010101    No Info available                Size(Bits): 192
```
---
5. Disconnect with the command:
```bash
ssscli disconnect
```


