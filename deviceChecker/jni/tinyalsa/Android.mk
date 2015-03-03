LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)
LOCAL_C_INCLUDES:= $(LOCAL_PATH)/include
LOCAL_SRC_FILES:= mixer.c pcm.c TinyAlsaAudio.c
LOCAL_MODULE := TinyAlsaDevice
LOCAL_SHARED_LIBRARIES:= libcutils libutils
LOCAL_LDLIBS    := -llog -ljnigraphics
LOCAL_MODULE_TAGS := optional

include $(BUILD_SHARED_LIBRARY)

