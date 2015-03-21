#include "include/TinyAlsaAudio.h"
#include <stdio.h>

#define DEBUG_PCM_FILE  0
#define MAX_ERROR_LENGTH 200
#define AndroidAudioRecordingClass "com/example/devicechecker/AudioRecordWrapper"

static int capturing = 1;
FILE* fcapture = NULL ;
//uint8_t errorMSG[MAX_ERROR_LENGTH]  ;
//static int deviceIsOk = 0 ;
static int deviceIsOk = 0 ;
uint8_t errorMSG[MAX_ERROR_LENGTH]  ;


typedef union
{
    JNIEnv* env;
    void* venv;
} UnionJNIEnvToVoid;

jclass record_class ;
jclass track_class ;
jobject record ;
jobject track ;

/*!
 * //降低难度，直接保存pcm数据，然后使用android层播放
 * 原打算通过jni反调 androidTrack播放
 */

void savePcm(char* pcmBuffer)
{
	/*
	if(!fcapture)
	{
		 fprintf(stderr, "Unable to open record.pcm\n");
	}
	else
	{
		fwrite( pcmBuffer ,frame.size, 1 , fcapture) ;
	}
	*/
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
    	LOGE("Unable to open PCM device (%s)\n",
                pcm_get_error(pcm));
    	//strcpy(error,pcm_get_error(pcm));
    	// 需要想办法把错误信息返回
        return -1;
    }

    // 分配pcm Buffer的空间
    size = pcm_frames_to_bytes(pcm, pcm_get_buffer_size(pcm));
    buffer = malloc(size);
    if (!buffer) {
    	LOGE( "Unable to allocate %d bytes\n", size);
        free(buffer);
        pcm_close(pcm);
        return -1;
    }

    // 创建索要保存的pcm文件，可能需要确认Android下路径问题
    fcapture = fopen("/skydir/record.pcm","wb");
    if(!fcapture)
    {
    	LOGE( "Unable to open record.pcm\n");
    }

    // record Buffer
    while(capturing && !pcm_read(pcm,buffer,size)){
    	// get buffer, and how to send buffer to java
    	//savePcm(buffer);
    	if(!fcapture)
    	{
    		LOGE("Unable to open record.pcm\n");
    	}
    	else
    	{
    		fwrite( buffer ,size, 1 , fcapture) ;
    	}

    }
    // 释放掉内存，并且关闭 pcm
    free(buffer);
    pcm_close(pcm);

    return 0;
}

/*!
 *  check devices Available
 *  do check wheather device is useable , if not useable ,return -1 and errorMSG, else return 0
 */
int devicesIsAvailable(int cardID, int deviceID,char* errorMSG)
{
	unsigned int card = cardID;
	unsigned int device = deviceID ;
	unsigned int channels = 1;
	unsigned int rate = 8000 ;
	unsigned int frames ;
	unsigned int period_size = 1024 ;
	unsigned int period_count = 4 ;
	enum pcm_format  format = PCM_FORMAT_S16_LE;

	struct pcm_config  config ;
	struct pcm *pcm ;
	char *buffer ;

	// 设置pcm_config 参数
    config.channels = channels;
    config.rate = rate;
    config.period_size = period_size;
    config.period_count = period_count;
    config.format = format;

    // 打开pcm设备文件
    pcm  = pcm_open(card, device,PCM_IN, &config);
    if(!pcm ||(!pcm_is_ready(pcm)))
    {
    	LOGE("Unable to open PCM device (%s)\n",
                pcm_get_error(pcm));
    	//error 赋值
    	LOGE("Before strcpy!!!!");
    	strcpy(errorMSG,pcm_get_error(pcm));
    	LOGE("After strcpy!!!!");
    	LOGE("Error Msg is %s",errorMSG);
        return -1;
    }
    else
    {
    	pcm_close(pcm);
    	return 0 ;
    }

}


/*!
 * just stop
 */
int stopAudioRecord()
{
	 capturing = 0 ;
         return 0;
}


/*-----------jni funcs belows ------------*/
Java_com_example_devicechecker_TinyAlsaAudio_startAudioRecord(JNIEnv* env,jobject thiz, jint cardID, jint deviceID)
{
	// this will be a dead loop, change it to control able
	startAudioRecord(cardID, deviceID);
}

// stop RecordThread
Java_com_example_devicechecker_TinyAlsaAudio_stopAudioRecord(JNIEnv* env,jobject thiz)
{
	  LOGE("Stop TinyAlsa Capture Thread\n");
	  // set capturing to 0 , end thread
	  stopAudioRecord() ;
}


jobject Java_com_example_devicechecker_TinyAlsaAudio_checkDeviceAvailable(JNIEnv* env,jobject thiz, jint cardID, jint deviceID)
{
	LOGE("Java_com_example_devicechecker_TinyAlsaAudio_checkDeviceAvailable\n");
	//  test begin
	static jclass gDeviceErrorMsgClass = NULL;
	 //创建一个局部引用
	LOGE("AAA\n");
	 jclass localRefCls=(*env)->FindClass(env, "com/example/devicechecker/DeviceErrorMsg");
	if (localRefCls == NULL) {
		return NULL; /* exception thrown */
	}
	LOGE("BBB\n");
	 /* 创建一个全局引用 */
	gDeviceErrorMsgClass = (*env)->NewGlobalRef(env, localRefCls);
	 /* 局部引用localRefCls不再有效，删除局部引用localRefCls*/
	(*env)->DeleteLocalRef(env, localRefCls);
	LOGE("CCC\n");
	if (gDeviceErrorMsgClass == NULL) {
		return NULL; /* out of memory exception thrown */
	 }
	//--- test end
	memset(errorMSG, 0,MAX_ERROR_LENGTH*sizeof(char));
	LOGE("Before check Devices\n");

	deviceIsOk = devicesIsAvailable(cardID, deviceID,errorMSG);
	LOGE("Get Result is %d",deviceIsOk);
	LOGE("Get MSG %s",errorMSG);
	// if(!deviceIsOk)  if use if(!deviceIsOk)  will be error
	//JNI ERROR (app bug): attempt to use stale global reference 0x52
	if(deviceIsOk == -1)
	{
		LOGE("Device is not OK\n");
		// 找到对应的java MSG类

		if (gDeviceErrorMsgClass == NULL) {
			LOGE("java/lang/RuntimeException Can't find class CaAccountNoInfo");
			return NULL;
		}

		// 实例化一个对应的类
		jobject object_datarange = (*env)->AllocObject(env,gDeviceErrorMsgClass);

		if (NULL == object_datarange) {
			LOGE("java/lang/RuntimeException Can't find object is NULL");
			// 释放掉全局引用
			(*env)->DeleteGlobalRef(env,gDeviceErrorMsgClass);
			return NULL;
		}


		jfieldID jfieldid_result = (*env)->GetFieldID(env,gDeviceErrorMsgClass,
					"result", "I");

		jfieldID lErrorMsgClass = (*env)->GetFieldID(env,gDeviceErrorMsgClass,
						"ErrorMsg" , "Ljava/lang/String;");

		if (lErrorMsgClass == NULL) {
			return NULL;
		}
		// 赋值
		(*env)->SetIntField(env,object_datarange, jfieldid_result, deviceIsOk);
		jstring jErrorMSG = (*env)->NewStringUTF(env,(const char*) errorMSG);
		(*env)->SetObjectField(env,object_datarange, lErrorMsgClass,
				jErrorMSG);
		// 释放掉全局引用  may be error delete ref before return object ,just try it
		(*env)->DeleteGlobalRef(env,gDeviceErrorMsgClass);
		return object_datarange;

	}
}


// just try it
jobject Java_com_example_devicechecker_DeviceScan_checkDeviceAvailable(JNIEnv* env,jobject thiz, jint cardID, jint deviceID)
{
	LOGE("Java_com_example_devicechecker_TinyAlsaAudio_checkDeviceAvailable\n");
	//  test begin
	static jclass gDeviceErrorMsgClass = NULL;
	 //创建一个局部引用
	LOGE("AAA\n");
	 jclass localRefCls=(*env)->FindClass(env, "com/example/devicechecker/DeviceErrorMsg");
	if (localRefCls == NULL) {
		return NULL; /* exception thrown */
	}
	LOGE("BBB\n");
	 /* 创建一个全局引用 */
	gDeviceErrorMsgClass = (*env)->NewGlobalRef(env, localRefCls);
	 /* 局部引用localRefCls不再有效，删除局部引用localRefCls*/
	(*env)->DeleteLocalRef(env, localRefCls);
	LOGE("CCC\n");
	if (gDeviceErrorMsgClass == NULL) {
		return NULL; /* out of memory exception thrown */
	 }
	//--- test end
	memset(errorMSG, 0,MAX_ERROR_LENGTH*sizeof(char));
	LOGE("Before check Devices\n");

	deviceIsOk = devicesIsAvailable(cardID, deviceID,errorMSG);
	LOGE("Get Result is %d",deviceIsOk);
	LOGE("Get MSG %s",errorMSG);
	// if(!deviceIsOk)  if use if(!deviceIsOk)  will be error
	//JNI ERROR (app bug): attempt to use stale global reference 0x52
	if(deviceIsOk == -1)
	{
		LOGE("Device is not OK\n");
		// 找到对应的java MSG类

		if (gDeviceErrorMsgClass == NULL) {
			LOGE("java/lang/RuntimeException Can't find class CaAccountNoInfo");
			return NULL;
		}

		// 实例化一个对应的类
		jobject object_datarange = (*env)->AllocObject(env,gDeviceErrorMsgClass);

		if (NULL == object_datarange) {
			LOGE("java/lang/RuntimeException Can't find object is NULL");
			// 释放掉全局引用
			(*env)->DeleteGlobalRef(env,gDeviceErrorMsgClass);
			return NULL;
		}


		jfieldID jfieldid_result = (*env)->GetFieldID(env,gDeviceErrorMsgClass,
					"result", "I");

		jfieldID lErrorMsgClass = (*env)->GetFieldID(env,gDeviceErrorMsgClass,
						"ErrorMsg" , "Ljava/lang/String;");

		if (lErrorMsgClass == NULL) {
			return NULL;
		}
		// 赋值
		(*env)->SetIntField(env,object_datarange, jfieldid_result, deviceIsOk);
		jstring jErrorMSG = (*env)->NewStringUTF(env,(const char*) errorMSG);
		(*env)->SetObjectField(env,object_datarange, lErrorMsgClass,
				jErrorMSG);
		// 释放掉全局引用  may be error delete ref before return object ,just try it
		(*env)->DeleteGlobalRef(env,gDeviceErrorMsgClass);
		return object_datarange;

	}
}

// for TEST
Java_com_example_devicechecker_TinyAlsaAudio_doAndroidAudioRecord(JNIEnv* env,jobject thiz)
{
	long count = 500;
	FILE *frecord = NULL ;
	jlong bytesRead;
	long state ;
	long sampleFormat = 2 ;
	long size = 320 ;
	int tempsize = 1;
	jlong size_t = 320 ;
	jbyte* buf;
	jbyteArray inputBuffer;
	//jshortArray inputBuffer ;
	//bool isDoRecord = true ;
	// for audio record
	long inputBuffSize=0, inputBuffSizePlay, inputBuffSizeRec;
	jmethodID read_method=0, record_method=0;
	jmethodID record_constructor_method=0, get_min_buffer_record_method = 0, record_method_id = 0;
	// for audio track
	long outputBuffSize = 0 , outputBuffSizePlay, outputBuffSizeRec ;
	jmethodID write_method = 0 ,play_method = 0 ;
	jmethodID track_constructor_method = 0 , get_min_buffer_track_method = 0 ;
	jclass tmp = (*env)->FindClass(env,AndroidAudioRecordingClass);

	record_class = (jclass)(*env)->NewGlobalRef(env,tmp);

	track_class = (jclass)(*env)->NewGlobalRef(env,(*env)->FindClass(env,"android/media/AudioTrack"));

	//record_class = (jclass)(*env)->NewGlobalRef(env,(*env)->FindClass(env,"android/media/AudioRecord"));
	if (record_class == 0) {
		goto on_error;
	}

	if(track_class == 0)
	{
		goto on_error ;
	}

	get_min_buffer_record_method = (*env)->GetStaticMethodID(env,record_class, "getMinBufferSize", "(III)I");
	get_min_buffer_track_method = (*env)->GetStaticMethodID(env,track_class,"getMinBufferSize", "(III)I");
	if (get_min_buffer_record_method == 0) {
				//PJ_LOG(2, (THIS_FILE, "Not able to find audio record getMinBufferSize method"));
		goto on_error;
	}
	if(get_min_buffer_track_method == 0 )
	{
		goto on_error ;
	}

	// for track
	outputBuffSizeRec = (*env)->CallStaticIntMethod(env,track_class, get_min_buffer_track_method,
			8000, 2, 2);
	if(outputBuffSizeRec <= 0){
				//PJ_LOG(2, (THIS_FILE, "Min buffer size is not a valid value"));
		goto on_error;
	}
	track_constructor_method = (*env)->GetMethodID(env,track_class,"<init>", "(IIIIII)V");
	if (track_constructor_method == 0) {
		//PJ_LOG(2, (THIS_FILE, "Not able to find audio record class constructor"));
		goto on_error;
	}
	LOGE("init track class\n");
	track = (*env)->NewObject(env,track_class, track_constructor_method,
			0,
			8000,
			2, // CHANNEL_CONFIGURATION_MONO
			2, // 2
			outputBuffSizeRec,
			1);
	if (track == 0) {
		goto on_error;
	}

	// for record
	inputBuffSizeRec = (*env)->CallStaticIntMethod(env,record_class, get_min_buffer_record_method,
			8000, 2, 2);
	if(inputBuffSizeRec <= 0){
				//PJ_LOG(2, (THIS_FILE, "Min buffer size is not a valid value"));
		goto on_error;
	}
	record_constructor_method = (*env)->GetMethodID(env,record_class,"<init>", "(IIIII)V");
	if (record_constructor_method == 0) {
		//PJ_LOG(2, (THIS_FILE, "Not able to find audio record class constructor"));
		goto on_error;
	}
	LOGE("init record class\n");
	record = (*env)->NewObject(env,record_class, record_constructor_method,
			1,
			8000,
			2, // CHANNEL_CONFIGURATION_MONO
			2, // 2
			inputBuffSizeRec);
	if (record == 0) {
		//PJ_LOG(1, (THIS_FILE, "Not able to instantiate record class"));
		goto on_error;
	}

	record_method_id = (*env)->GetMethodID(env,record_class,"getState", "()I");
	state = (*env)->CallIntMethod(env,record, record_method_id);
	if(state == 0 )
	{
		goto on_error;
	}
	LOGE("get read method\n");
	read_method = (*env)->GetMethodID(env,record_class,"read", "([BII)I");
	record_method = (*env)->GetMethodID(env,record_class,"startRecording", "()V");
	if(read_method==0 || record_method==0) {
		goto on_error;
	}
	LOGE("do write method\n");
	write_method = (*env)->GetMethodID(env,track_class,"write", "([BII)I");
	play_method = (*env)->GetMethodID(env,track_class,"play", "()V");

	inputBuffer = (*env)->NewByteArray(env,size);
	if (inputBuffer == 0) {
		//PJ_LOG(2, (THIS_FILE, "Not able to allocate a buffer for input read process"));
		goto on_error;
	}

	//buf = (*env)->GetByteArrayElements(env,inputBuffer, 0);
	//memset(buf, 0, size);

	(*env)->CallVoidMethod(env,record, record_method);

	(*env)->CallVoidMethod(env,track, play_method);

	frecord = fopen("/skydir/audio_record.pcm","wb+");
	if(frecord == 0)
	{
		LOGE("open audio_record.pcm failed\n");
		goto on_error ;
	}


	// 调用 AudioRecord 记录pcm
	while(capturing && count--)
	{
		LOGE( "Size of jlong  is %d bytes\n", sizeof(size_t));
		LOGE( "Size of int  is %d bytes\n", sizeof(tempsize));
		//LOGE("1111\n");

		bytesRead = (*env)->CallIntMethod(env,record, read_method,
					inputBuffer,
					0,
					size);


		jsize theArrayLengthJ = (*env)->GetArrayLength(env,inputBuffer);

        jbyte *bytes = (*env)->GetByteArrayElements(env,inputBuffer, NULL);
        LOGE( "get Arrary Size is %d bytes\n", theArrayLengthJ);

        //ALOGE("read in jni 0 is:%d,123:%d  299:%d",bytes[0],bytes[123],bytes[229]);

		//LOGE("2222\n");
		if(bytesRead <=0)
		{
			continue ;
		}

		//LOGE("3333\n");
		if(bytesRead != size)
		{
			continue;
		}

		LOGE("do loop\n");
		if(frecord)
		{
			fwrite( (void*)bytes,size, 1 , frecord) ;
			fflush(frecord);
		}
		//send it to AudioTrack!!
		LOGE("do Track\n");
		int status =  (*env)->CallIntMethod(env,track, write_method,
						inputBuffer,
						0,
						size);
		LOGE("Track return status!!!" + status);
        (*env)->ReleaseByteArrayElements(env,inputBuffer, bytes, 0);
	}

	LOGE("end!!!!\n");
	if(frecord)
	{
		fclose(frecord);
		frecord = NULL ;
	}

	return 0 ;

on_error:
	if(frecord)
	{
		fclose(frecord);
		frecord = NULL ;
	}
	return -1 ;
}

// test for use native recevice pcm data
/*
Java_com_example_devicechecker_AudioRecordWrapper_doSendPcm(JNIEnv* env,jobject thiz,jbyteArray javaFrame,jint length)
{
	 jbyte* pcmFrames = (*env)->GetByteArrayElements(env,javaFrame, NULL);
	 //buffer =  reinterpret_cast<uint8_t*>(pcmFrames);
	 localBuffer = (uint8_t*)(pcmFrames);
	 memcpy(localBuffer,pcmBuffer,length);
	 // save pcm buffers
	 (*env)->ReleaseByteArrayElements(env,javaFrame, pcmFrames, JNI_ABORT);
}
*/

