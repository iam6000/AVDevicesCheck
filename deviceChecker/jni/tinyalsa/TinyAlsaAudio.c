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
/*
jint JNICALL JNI_OnLoad (JavaVM* vm,void* reserved){

	UnionJNIEnvToVoid uenv;
	uenv.venv = NULL;
	jint result = -1;
	LOGE("Load JNI\n");
	JNIEnv* env = NULL;
	vm->GetEnv(&uenv.venv, JNI_VERSION_1_6);
	env = uenv.env;
    jclass tmp = (*env)->FindClass(env,"com/example/devicechecker/DeviceErrorMsg");
    myClass =(jclass)env->NewGlobalRef(tmp);
    return JNI_VERSION_1_6;
}
*/


/*
jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
    UnionJNIEnvToVoid uenv;
    uenv.venv = NULL;
    jint result = -1;
    JNIEnv* env = NULL;
    LOGI("JNI_OnLoad");
    if (vm->GetEnv(&uenv.venv, JNI_VERSION_1_4) != JNI_OK)
    {
        LOGE("ERROR: GetEnv failed");
        goto bail;

    }
    env = uenv.env;
    if (registerNatives(env) != JNI_TRUE)
    {
        LOGE("ERROR: registerNatives failed");
        goto bail;
    }

    result = JNI_VERSION_1_4;

bail:

    return result;

}
*/



