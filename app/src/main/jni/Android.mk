LOCAL_PATH := $(call my-dir)
MAIN_LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

# Here is the name of your lib.
# When you change the lib name, change also on System.loadLibrary("") under OnCreate method on StaticActivity.java
# Both must have same name
LOCAL_MODULE    := MyLib

# Code optimization
LOCAL_CFLAGS := -Wno-error=format-security -fpermissive -w -s -Werror -Wold-style-cast -fms-extensions -Wno-narrowing
LOCAL_CFLAGS += -fno-rtti -fno-exceptions -fvisibility=hidden -ffunction-sections -fdata-sections -stdlib=libc++
LOCAL_CPPFLAGS += -fvisibility=hidden -ffunction-sections -fdata-sections -w -std=c++14
LOCAL_LDFLAGS += -Wl,--gc-sections,--strip-all
LOCAL_ARM_MODE := arm

# Here you add the cpp file
LOCAL_C_INCLUDES += $(MAIN_LOCAL_PATH)
LOCAL_SRC_FILES := Main.cpp \

LOCAL_LDLIBS := -llog -landroid

include $(BUILD_SHARED_LIBRARY)