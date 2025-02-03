# Encrypted storage on Astrial

## Reference
https://www.marcusfolkesson.se/blog/encrypted-storage-on-imx/

## Example - How to use

1. Verify the following command:
```
cat /proc/crypto | grep -B1 -A2 tk
```
output:
```
name         : tk(ecb(aes))  \n
driver       : tk-ecb-aes-caam  \n  
module       : caam_jr 
priority     : 3000
--

name         : tk(cbc(aes))
driver       : tk-cbc-aes-caam
module       : caam_jr
priority     : 3000
```
2. Verify the following commands:
```
modprobe dm-crypt
dmsetup targets
```
output:
```
crypt            v1.23.0
striped          v1.6.0
linear           v1.4.0
error            v1.5.0
```

3. Create a random key, using 16, 24 or 32 byte ECB encryption:
```
caam-keygen create randomkey ecb -s 16
```

4. Check the gererated files and their sizes:
```
ls -l /data/caam
```
output:
```
total 36
-rw-r--r-- 1 root root    36 Mar 18 13:44 randomkey
-rw-r--r-- 1 root root    96 Mar 18 13:44 randomkey.bb
```

5. Add the "logon" key based on the previous random key to the keyring:
```
cat /data/caam/randomkey | keyctl padd logon logkey: @s
```

6. Check the "logon" key in the keyring:
```
keyctl list @s
```
output:
```
2 keys in keyring:
623954697: ----s-rv     0     0 user: invocation_id
949507891: --alsw-v     0     0 logon: logkey:
```

7. Create an img file to use as a device to format and encrypt:
```
dd if=/dev/zero of=data.img bs=1M count=16
```

8. Mount the img file as a loop device:
```
losetup /dev/loop0 data.img
```

9. Activate the encrypted device mapper using dmsetup with the key created previously:
```
keysize=$(stat --printf="%s" /data/caam/randomkey)
dmsetup -v create encrypted-dev --table "0 $(blockdev --getsz /dev/loop0) crypt capi:tk(cbc(aes))-plain :$keysize:logon:logkey: 0 /dev/loop0 0 1 sector_size:512"
```

10. Format the newly created device:
```
mkfs.ext4 /dev/mapper/encrypted-dev
sync
```

11. Mount the device:
```
mkdir -p /tmp/mnt
mount /dev/mapper/encrpyted-dev /tmp/mnt
```

12. Create a sample file:
```
echo "Hello World!" >> /tmp/mnt/crypto-file
sync
```

13. Unmount the device:
```
umount /dev/mapper/encrypted-dev
dmsetup remove encrypted-dev
losetup -d /dev/loop0
```

14. Save the "black-blob" key and delete the keys:
```
cp /data/caam/randomkey.bb randomkey.bb
rm /data/caam/*
keyctl clear @s
```

15. Reboot the board and reimport the key saved previously:
```
caam-keygen import randomkey.bb importkey
```

16. Repeat steps 4, 5, 7, 8, 10 using "importkey":
- step 4:
```
cat /data/caam/importkey | keyctl padd logon logkey: @s
```
- step 5:
```
keyctl list @s
```
- step 7:
```
losetup /dev/loop0 data.img
```
- step 8:
```
keysize=$(stat --printf="%s" /data/caam/importkey)
dmsetup -v create encrypted-dev --table "0 $(blockdev --getsz /dev/loop0) crypt capi:tk(cbc(aes))-plain :$keysize:logon:logkey: 0 /dev/loop0 0 1 sector_size:512"
```
step 10:
```
mkdir -p /tmp/mnt
mount /dev/mapper/encrpyted-dev /tmp/mnt
```

16. Verify the output:
```
cat /tmp/mnt/crypto-file
```
output:
```
Hello World!
```