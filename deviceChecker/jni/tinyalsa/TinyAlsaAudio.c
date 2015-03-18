#include "include/TinyAlsaAudio.h"
#include <stdio.h>

#define DEBUG_PCM_FILE  0
#define MAX_ERROR_LENGTH 200

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

/*!
 * //�����Ѷȣ�ֱ�ӱ���pcm���ݣ�Ȼ��ʹ��android�㲥��
 * ԭ����ͨ��jni���� androidTrack����
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
 *  startAudioRecord ��ʼ��¼�ɼ�����pcm����
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

	// ����pcm_config ����
    config.channels = channels;
    config.rate = rate;
    config.period_size = period_size;
    config.period_count = period_count;
    config.format = format;
    config.start_threshold = 0;
    config.stop_threshold = 0;
    config.silence_threshold = 0;

    // ��pcm�豸�ļ�
    pcm  = pcm_open(card, device,PCM_IN, &config);
    if(!pcm ||(!pcm_is_ready(pcm)))
    {
    	LOGE("Unable to open PCM device (%s)\n",
                pcm_get_error(pcm));
    	//strcpy(error,pcm_get_error(pcm));
    	// ��Ҫ��취�Ѵ�����Ϣ����
        return -1;
    }

    // ����pcm Buffer�Ŀռ�
    size = pcm_frames_to_bytes(pcm, pcm_get_buffer_size(pcm));
    buffer = malloc(size);
    if (!buffer) {
    	LOGE( "Unable to allocate %d bytes\n", size);
        free(buffer);
        pcm_close(pcm);
        return -1;
    }

    // ������Ҫ�����pcm�ļ���������Ҫȷ��Android��·������
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
    // �ͷŵ��ڴ棬���ҹر� pcm
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

	// ����pcm_config ����
    config.channels = channels;
    config.rate = rate;
    config.period_size = period_size;
    config.period_count = period_count;
    config.format = format;

    // ��pcm�豸�ļ�
    pcm  = pcm_open(card, device,PCM_IN, &config);
    if(!pcm ||(!pcm_is_ready(pcm)))
    {
    	LOGE("Unable to open PCM device (%s)\n",
                pcm_get_error(pcm));
    	//error ��ֵ
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
	 //����һ���ֲ�����
	LOGE("AAA\n");
	 jclass localRefCls=(*env)->FindClass(env, "com/example/devicechecker/DeviceErrorMsg");
	if (localRefCls == NULL) {
		return NULL; /* exception thrown */
	}
	LOGE("BBB\n");
	 /* ����һ��ȫ������ */
	gDeviceErrorMsgClass = (*env)->NewGlobalRef(env, localRefCls);
	 /* �ֲ�����localRefCls������Ч��ɾ���ֲ�����localRefCls*/
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
		// �ҵ���Ӧ��java MSG��

		if (gDeviceErrorMsgClass == NULL) {
			LOGE("java/lang/RuntimeException Can't find class CaAccountNoInfo");
			return NULL;
		}

		// ʵ����һ����Ӧ����
		jobject object_datarange = (*env)->AllocObject(env,gDeviceErrorMsgClass);

		if (NULL == object_datarange) {
			LOGE("java/lang/RuntimeException Can't find object is NULL");
			// �ͷŵ�ȫ������
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
		// ��ֵ
		(*env)->SetIntField(env,object_datarange, jfieldid_result, deviceIsOk);
		jstring jErrorMSG = (*env)->NewStringUTF(env,(const char*) errorMSG);
		(*env)->SetObjectField(env,object_datarange, lErrorMsgClass,
				jErrorMSG);
		// �ͷŵ�ȫ������  may be error delete ref before return object ,just try it
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
	 //����һ���ֲ�����
	LOGE("AAA\n");
	 jclass localRefCls=(*env)->FindClass(env, "com/example/devicechecker/DeviceErrorMsg");
	if (localRefCls == NULL) {
		return NULL; /* exception thrown */
	}
	LOGE("BBB\n");
	 /* ����һ��ȫ������ */
	gDeviceErrorMsgClass = (*env)->NewGlobalRef(env, localRefCls);
	 /* �ֲ�����localRefCls������Ч��ɾ���ֲ�����localRefCls*/
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
		// �ҵ���Ӧ��java MSG��

		if (gDeviceErrorMsgClass == NULL) {
			LOGE("java/lang/RuntimeException Can't find class CaAccountNoInfo");
			return NULL;
		}

		// ʵ����һ����Ӧ����
		jobject object_datarange = (*env)->AllocObject(env,gDeviceErrorMsgClass);

		if (NULL == object_datarange) {
			LOGE("java/lang/RuntimeException Can't find object is NULL");
			// �ͷŵ�ȫ������
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
		// ��ֵ
		(*env)->SetIntField(env,object_datarange, jfieldid_result, deviceIsOk);
		jstring jErrorMSG = (*env)->NewStringUTF(env,(const char*) errorMSG);
		(*env)->SetObjectField(env,object_datarange, lErrorMsgClass,
				jErrorMSG);
		// �ͷŵ�ȫ������  may be error delete ref before return object ,just try it
		(*env)->DeleteGlobalRef(env,gDeviceErrorMsgClass);
		return object_datarange;

	}
}


// for TEST
Java_com_example_devicechecker_TinyAlsaAudio_doAndroidAudioRecord(JNIEnv* env,jobject thiz)
{
	long count = 1000;
	FILE *frecord = NULL ;
	jlong bytesRead;
	long state ;
	long sampleFormat = 2 ;
	long size = 320 ;
	int tempsize = 1;
	jlong size_t = 320 ;
	jbyte* buf;
	jbyteArray inputBuffer;
	//bool isDoRecord = true ;
	long inputBuffSize=0, inputBuffSizePlay, inputBuffSizeRec;
	jmethodID read_method=0, record_method=0;
	jmethodID constructor_method=0, get_min_buffer_size_method = 0, method_id = 0;

	record_class = (jclass)(*env)->NewGlobalRef(env,(*env)->FindClass(env,"android/media/AudioRecord"));
	if (record_class == 0) {
		goto on_error;
	}

	get_min_buffer_size_method = (*env)->GetStaticMethodID(env,record_class, "getMinBufferSize", "(III)I");
	if (get_min_buffer_size_method == 0) {
				//PJ_LOG(2, (THIS_FILE, "Not able to find audio record getMinBufferSize method"));
		goto on_error;
	}

	inputBuffSizeRec = (*env)->CallStaticIntMethod(env,record_class, get_min_buffer_size_method,
			8000, 2, 2);
	if(inputBuffSizeRec <= 0){
				//PJ_LOG(2, (THIS_FILE, "Min buffer size is not a valid value"));
		goto on_error;
	}


	constructor_method = (*env)->GetMethodID(env,record_class,"<init>", "(IIIII)V");
	if (constructor_method == 0) {
		//PJ_LOG(2, (THIS_FILE, "Not able to find audio record class constructor"));
		goto on_error;
	}

	record = (*env)->NewObject(env,record_class, constructor_method,
			1,
			8000,
			2, // CHANNEL_CONFIGURATION_MONO
			2, // 2
			inputBuffSizeRec);

	if (record == 0) {
		//PJ_LOG(1, (THIS_FILE, "Not able to instantiate record class"));
		goto on_error;
	}

	method_id = (*env)->GetMethodID(env,record_class,"getState", "()I");
	state = (*env)->CallIntMethod(env,record, method_id);

	if(state == 0 )
	{
		goto on_error;
	}

	read_method = (*env)->GetMethodID(env,record_class,"read", "([BII)I");
	record_method = (*env)->GetMethodID(env,record_class,"startRecording", "()V");
	if(read_method==0 || record_method==0) {
		goto on_error;
	}

	inputBuffer = (*env)->NewByteArray(env,size);
	if (inputBuffer == 0) {
		//PJ_LOG(2, (THIS_FILE, "Not able to allocate a buffer for input read process"));
		goto on_error;
	}

	buf = (*env)->GetByteArrayElements(env,inputBuffer, 0);
	memset(buf, 0, size);

	(*env)->CallVoidMethod(env,record, record_method);

	frecord = fopen("/skydir/audio_record.pcm","wb+");
	if(frecord == 0)
	{
		LOGE("open audio_record.pcm failed\n");
		goto on_error ;
	}


	// ���� AudioRecord ��¼pcm
	while(capturing && count--)
	{
		LOGE( "Size of jlong  is %d bytes\n", sizeof(size_t));
		LOGE( "Size of int  is %d bytes\n", sizeof(tempsize));
		//LOGE("1111\n");
		memset(buf, 1, size);

		bytesRead = (*env)->CallIntMethod(env,record, read_method,
					inputBuffer,
					0,
					size);

		jsize theArrayLengthJ = (*env)->GetArrayLength(env,inputBuffer);

		LOGE( "Size of theArrayLengthJ  is %d bytes\n", theArrayLengthJ);

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
			fwrite( (void*)buf,size, 1 , frecord) ;
			fflush(frecord);
		}
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

