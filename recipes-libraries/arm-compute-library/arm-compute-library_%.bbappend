# older scons do not support MAXLINELENGTH and some packages still may be
# using older scons, these recipes can clear SCONS_MAXLINELENGTH in them
# (see poky commit fb0b2cf7727984bfdc295879bd605d6bd105c585 introducted in release 4.0.8)
SCONS_MAXLINELENGTH = ""
