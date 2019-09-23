#Android NDK: APP_STL gnustl_static is no longer supported. Please switch to either c++_static or c++_shared.
#APP_STL := gnustl_static
APP_STL := c++_static

APP_CPPFLAGS := -frtti -fexceptions -mfloat-abi=softfp -mfpu=neon -std=gnu++0x -Wno-deprecated \
-ftree-vectorize -ffast-math -fsingle-precision-constant
#NDK_TOOLCHAIN_VERSION := 4.8
#APP_CFLAGS := --std=c++11
APP_ABI := all #armeabi-v7a
APP_OPTIM := release
APP_SHORT_COMMANDS := true

#Android NDK: Module ORB_SLAM2 depends on undefined modules: pangolin
#Android NDK: Aborting (set APP_ALLOW_MISSING_DEPS=true to allow missing dependencies)
APP_ALLOW_MISSING_DEPS := true
