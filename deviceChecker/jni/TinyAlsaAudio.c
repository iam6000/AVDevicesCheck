#include "TinyAlsaAudio.h"
#include <stdio.h>

#define DEBUG_PCM_FILE  0

static int capturing = 1;
FILE* fcapture = NULL ;

/*!
 * //降低难度，直接保存pcm数据，然后使用android层播放
 * 原打算通过jni反调 androidTrack播放
 */

void savePcm(char* pcmBuffer)
{
	if(!fcapture)
	{
		 fprintf(stderr, "Unable to open record.pcm\n");
	}
	else
	{
		fwrite( frame.buf ,frame.size, 1 , fcapture) ;
	}
	// call androidTrack do audio play
}

/*!
 *  startAudioRecord 开始记录采集到的pcm数据
 */
int startAudioRecord(int card_id , int device_id)
{

	unsigned int card = card_id;
	unsigned int device = device_id ;
	unsigned int channels = 1;
	unsigned int rate = 8000 ;
	unsigned int frames ;
	unsigned int period_size = 1024 ;
	unsigned int period_count = 4 ;
	enum pcm_format  format = PCM_FORMAT_S16_LE;


	struct pcm_config  config ;
	struct pcm *pcm ;
	char *buffer ;

	unsigned int size = 0;
	unsigned int bytes_read = 0 ;

	// 设置pcm_config 参数
    config.channels = channels;
    config.rate = rate;
    config.period_size = period_size;
    config.period_count = period_count;
    config.format = format;
    config.start_threshold = 0;
    config.stop_threshold = 0;
    config.silence_threshold = 0;

    // 打开pcm设备文件
    pcm  = pcm_open(card, device,PCM_IN, &config);
    if(!pcm ||(!pcm_is_ready(pcm)))
    {
        fprintf(stderr, "Unable to open PCM device (%s)\n",
                pcm_get_error(pcm));
        return -1;
    }

    // 分配pcm Buffer的空间
    size = pcm_frames_to_bytes(pcm, pcm_get_buffer_size(pcm));
    buffer = malloc(size);
    if (!buffer) {
        fprintf(stderr, "Unable to allocate %d bytes\n", size);
        free(buffer);
        pcm_close(pcm);
        return -1;
    }

    // 创建索要保存的pcm文件，可能需要确认Android下路径问题
    fcapture = fopen("record.pcm",wb);
    if(!fcapture)
    {
    	fprintf(stderr, "Unable to open record.pcm\n");
    }

    // record Buffer
    while(capturing && !pcm_read(pcm,buffer,size)){
    	// get buffer, and how to send buffer to java
    	savePcm(buffer);
    }
    // 释放掉内存，并且关闭 pcm
    free(buffer);
    pcm_close(pcm);

    return 0;
}


/*!
 * just stop
 */
int stopAudioRecord()
{
	 capturing = 0 ;
}


/*-----------jni funcs belows ------------*/
Java_com_example_devicechecker_TinyAlsaAudio_startAudioRecord(JNIEnv* env,jobject thiz, jint cardID, jint deviceID)
{
	int ret ;
	// this will be a dead loop, change it to control able
	startAudioRecord(cardID, deviceID);
}

// stop RecordThread
Java_com_example_devicechecker_TinyAlsaAudio_stopAudioRecord(JNIEnv* env,jobject thiz)
{
	  fprintf(stderr, "Stop TinyAlsa Capture Thread\n");
	  // set capturing to 0 , end thread
	  stopAudioRecord() ;
}
