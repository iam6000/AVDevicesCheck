/*tinyalsa native, use tinyalsa to do audio capture*/
#include<jni.h>
#include<android/log.h>
#include<malloc.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <signal.h>
#include <string.h>
#include "asoundlib.h"


#define  LOG_TAG    "TinyAlsaNative"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

#define ID_RIFF 0x46464952
#define ID_WAVE 0x45564157
#define ID_FMT  0x20746d66
#define ID_DATA 0x61746164

#define FORMAT_PCM 1


struct wav_header {
    uint32_t riff_id;
    uint32_t riff_sz;
    uint32_t riff_fmt;
    uint32_t fmt_id;
    uint32_t fmt_sz;
    uint16_t audio_format;
    uint16_t num_channels;
    uint32_t sample_rate;
    uint32_t byte_rate;
    uint16_t block_align;
    uint16_t bits_per_sample;
    uint32_t data_id;
    uint32_t data_sz;
};


/*!
 * brief  start AudioRecord
 * param s pcmdevices
 */


//Jni func
//void Java_com_example_devicechecker_TinyAlsaAudio_startAudioRecord(JNIEnv* env,jobject thiz, jint cardID, jint deviceID);


//void Java_com_example_devicechecker_TinyAlsaAudio_stopAudioRecord(JNIEnv* env,jobject thiz);


//jobject Java_com_example_devicechecker_TinyAlsaAudio_checkDeviceAvailable(JNIEnv* env,jobject thiz, jint cardID, jint deviceID);

