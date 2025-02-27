# Astrial SE05x

## Reference
- https://www.nxp.com/products/SE050
- PlugAndTrustMW.pdf (from se05x_mw_v04.05.01.zip)
- [AN13013 | Get started with EdgeLock SE05x support package](https://www.nxp.com/docs/en/application-note/AN13013.pdf)
- [AN12436 | SE050 configurations](https://www.nxp.com/docs/en/application-note/AN12436.pdf)

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

## Cryptographic Operations
### ECC (Generate, Sign, Verify)

The following is a simple example with `ssscli` that generates a key pair, signs a file and then verifies it against the same key.
```bash
ssscli connect se05x t1oi2c /dev/i2c-2
ssscli generate ecc 0x100 NIST_P192
echo "Hello World!" > test_file
ssscli sign 0x100 test_file test_file.sig
ssscli verify 0x100 test_file test_file.sig
ssscli get ecc pub 0x100 ecc-pub.pem
ssscli disconnect
```
One could also import its own set of keys:
```bash
openssl ecparam -name prime192v1 -genkey -out ecc-pair.pem
ssscli set ecc pair 0x101 ecc-pair.pem
```
And perform the verification via the custom openssl engine:
```bash
export EX_SSS_BOOT_SSS_PORT=/dev/i2c-2
openssl dgst -engine /usr/lib/libsss_engine.so -verify ecc-pub.pem -signature test_file.sig test_file
```

To implement this operations in a custom workflow, the `sss` APIs from the se05x middleware can be used as seen in the `ex_sss_ecc` example `simw-top/sss/ex/ecc/ex_sss_ecc.c`.
These are the key functions used:
- `sss_key_object_init`
- `sss_key_object_allocate_handle`
- `sss_key_store_set_key`
- `sss_asymmetric_context_init`
- `sss_asymmetric_sign_digest`
- `sss_asymmetric_verify_digest`

### RSA (Generate, Encrypt, Decrypt)

The following is a simple example with `ssscli` that generates a key pair, encrypts a file and then decrypts it.
```bash
ssscli connect se05x t1oi2c /dev/i2c-2
ssscli generate rsa 0x200 2048
ssscli get rsa pub 0x200 rsa-pub.pem
ssscli set rsa pub 0x201 rsa-pub.pem
ssscli encrypt 0x201 "Hello World!" hello_world.enc
ssscli decrypt 0x200 hello_world.enc hello_world.txt
cat hello_world.txt
    "Hello World!"
ssscli disconnect
```
Or likewise, perform encryption and decryption via the custom openssl engine:
```bash
echo "Hello World!" > test_file
export EX_SSS_BOOT_SSS_PORT=/dev/i2c-2
openssl genrsa -out rsa-prv.pem
openssl rsa -in rsa-prv.pem -pubout -out rsa-pub.pem
openssl pkeyutl -engine /usr/lib/libsss_engine.so -encrypt -pubin -inkey rsa-pub.pem -out test_file.enc -in test_file
openssl pkeyutl -engine /usr/lib/libsss_engine.so -decrypt -inkey rsa-prv.pem -in test_file.enc -out test_file.dec
echo test_file.dec
    "Hello World!"
```

In this example, the key functions used are:
- `sss_key_object_init`
- `sss_key_object_allocate_handle`
- `sss_key_store_set_key`
- `sss_asymmetric_context_init`
- `sss_asymmetric_encrypt`
- `sss_asymmetric_decrypt`

## Secure Communication Channel (PlatformSCP03)

The yocto layer is built by default with `-DPTMW_SE05X_Auth=PlatfSCP03` to allow PlatformSCP connections (though not mandatory).
Any delivered SE05x device has a SCP03 base key set that contains the same keys for each device-type.

In the previous examples, any `ssscli` command reported the following warnings since no PlatformSCP connection was established:
```
    sss   :WARN :Communication channel is Plain.
    sss   :WARN :!!!Not recommended for production use.!!!
```

The secure SCP connection can be established as follows:
```bash
ssscli connect se05x t1oi2c /dev/i2c-2 --auth_type=PlatformSCP --scpkey=scp_keys.txt
ssscli se05x uid
    sss   :INFO :atr (Len=35)
          01 A0 00 00    03 96 04 03    E8 00 FE 02    0B 03 E8 00
          01 00 00 00    00 64 13 88    0A 00 65 53    45 30 35 31
          00 00 00
    INFO:sss.se05x:04005001029c6f7358447f043c6e9ab61d90
    Unique ID: 04005001029c6f7358447f043c6e9ab61d90
```
Where `scp_keys.txt` is a **pre-existing** file in the following format:
```
ENC d2db63e7a0a5aed72a6460c4dfdcaf64
MAC 738d5b798ed241b0b24768514bfba95b
DEK 6702dac30942b2c85e7f47b42ced4e7f
```
**NOTE**: These values are the default keys already present on any SE05x chip, and each variant has different values, which can be found in [AN12436.pdf (Table 5 and 6)](https://www.nxp.com/docs/en/application-note/AN12436.pdf).

### Key Rotation
> This demo is automatically built by the se05x layer.
- `simw-top/demos/se05x/se05x_RotatePlatformSCP03Keys/`

The se05x middleware includes the "SE05X Rotate PlatformSCP" demo which rotates the existing SCP03 keys to new keys and then reverts them back.
Once the key rotation is successful on the chip, a file is created `/tmp/SE05X/plain_scp.txt`, which contains updated key values written to chip.

```bash
export EX_SSS_BOOT_SSS_PORT=/dev/i2c-2
export EX_SSS_BOOT_SCP03_PATH=/root/scp_keys.txt
se05x_RotatePlatformSCP03Keys
    App   :INFO :PlugAndTrust_v04.05.01_20240219
    App   :INFO :Running se05x_RotatePlatformSCP03Keys
    App   :INFO :Using PortName='/dev/i2c-2' (ENV: EX_SSS_BOOT_SSS_PORT=/dev/i2c-2)
    App   :INFO :Using default PlatfSCP03 keys. You can use keys from file using ENV=EX_SSS_BOOT_SCP03_PATH
    sss   :INFO :atr (Len=35)
          01 A0 00 00    03 96 04 03    E8 00 FE 02    0B 03 E8 00
          01 00 00 00    00 64 13 88    0A 00 65 53    45 30 35 31
          00 00 00
    App   :INFO :Using default PlatfSCP03 keys. You can use keys from file using ENV=EX_SSS_BOOT_SCP03_PATH
    App   :INFO :Updating key with version - 0b
    App   :INFO :Congratulations !!! Key Rotation Successful!!!!
    App   :WARN :Cannot access SCP03 keys directory '/tmp/SE05X/'
    App   :INFO :Creating directory
    App   :WARN :Changed keys logged to /tmp/SE05X/plain_scp.txt.
    App   :INFO :Updating key with version - 0b
    App   :INFO :Congratulations !!! Key Rotation Successful!!!!
    App   :INFO :SCP03 keys directory exists
    App   :WARN :Changed keys logged to /tmp/SE05X/plain_scp.txt.
    App   :INFO :

     NOTE:
     SE05x uses KVN (Key Version Number) 11 to authenticate the PlatformSCP channel.
     SE051 has an additional key in KVN 12 injected.
     To use KVN 12 for PlatformSCP authentication, update 'EX_SSS_AUTH_SE05X_KEY_VERSION_NO' in ex_sss_auth.h file.
    App   :INFO :

    App   :INFO :se05x_TP_PlatformSCP03keys Example Success !!!...
    App   :INFO :ex_sss Finished
```

Operations on how to perform the key rotation can be found in the `tp_PlatformKeys` function in the demo source code.

### Mandatory SCP
> This demo is automatically built by the se05x layer.
- `simw-top/demos/se05x/se05x_MandatePlatformSCP/`

The "SE05X Mandate SCP" demo can be used as a reference to further secure the chip and disallow any non-SCP connection.

```bash
export EX_SSS_BOOT_SSS_PORT=/dev/i2c-2
export EX_SSS_BOOT_SCP03_PATH=/root/scp_keys.txt
se05x_MandatePlatformSCP
    App   :INFO :PlugAndTrust_v04.05.01_20240219
    App   :INFO :Running se05x_MandatePlatformSCP
    App   :INFO :Using PortName='/dev/i2c-2' (ENV: EX_SSS_BOOT_SSS_PORT=/dev/i2c-2)
    App   :WARN :Using SCP03 keys from:'/tmp/SE05X/plain_scp.txt' (FILE=/tmp/SE05X/plain_scp.txt)
    sss   :INFO :atr (Len=35)
          01 A0 00 00    03 96 04 03    E8 00 FE 02    0B 03 E8 00
          01 00 00 00    00 64 13 88    0A 00 65 53    45 30 35 31
          00 00 00
    sss   :INFO :atr (Len=35)
          01 A0 00 00    03 96 04 03    E8 00 FE 02    0B 03 E8 00
          01 00 00 00    00 64 13 88    0A 00 65 53    45 30 35 31
          00 00 00
    sss   :WARN :Communication channel is with UserID (But Plain).
    sss   :WARN :!!!Not recommended for production use.!!!
    App   :INFO :Se05x_API_SetPlatformSCPRequest Successful
    App   :WARN :Further communication must be encrypted
    App   :INFO :se05x_MandatePlatformSCP Example Success !!!...
    App   :INFO :ex_sss Finished
```

The API function for this purpose is `Se05x_API_SetPlatformSCPRequest` with the `kSE05x_PlatformSCPRequest_REQUIRED` parameter.

To verify that a plain connection cannot be established anymore, run:
```bash
ssscli connect se05x t1oi2c /dev/i2c-2
ssscli se05x uid
    sss   :INFO :atr (Len=35)
          01 A0 00 00    03 96 04 03    E8 00 FE 02    0B 03 E8 00 
          01 00 00 00    00 64 13 88    0A 00 65 53    45 30 35 31 
          00 00 00 
    sss   :WARN :Communication channel is Plain.
    sss   :WARN :!!!Not recommended for production use.!!!
    sss   :WARN :nxEnsure:'ret == SM_OK' failed. At Line:7837 Function:sss_se05x_TXn
    sss   :ERROR:Invalid property
    INFO:sss.se05x:000000000000000000000000000000000000
    Unique ID: 000000000000000000000000000000000000
ssscli connect se05x t1oi2c /dev/i2c-2 --auth_type=PlatformSCP --scpkey=/tmp/SE05X/plain_scp.txt
ssscli se05x uid
    sss   :INFO :atr (Len=35)
          01 A0 00 00    03 96 04 03    E8 00 FE 02    0B 03 E8 00
          01 00 00 00    00 64 13 88    0A 00 65 53    45 30 35 31
          00 00 00
    INFO:sss.se05x:04005001029c6f7358447f043c6e9ab61d90
    Unique ID: 04005001029c6f7358447f043c6e9ab61d90
```
