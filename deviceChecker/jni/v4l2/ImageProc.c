#include "ImageProc.h"
//01-24 07:43:06.439: E/TEST(3756): VIDIOC_DQBUF error 22, Invalid argument

int errnoexit(const char *s)
{
	LOGE("%s error %d, %s", s, errno, strerror (errno));
	return ERROR_LOCAL;
}

int xioctl(int fd, int request, void *arg)
{
	int r;

	do r = ioctl (fd, request, arg);
	while (-1 == r && EINTR == errno);

	return r;
}

//妫�煡video璁惧鍚嶇О
int checkCamerabase(void){
	struct stat st;
	int i;
	int start_from_4 = 1;
	
	/* if /dev/video[0-3] exist, camerabase=4, otherwise, camrerabase = 0 */
	for(i=0 ; i<4 ; i++){
		sprintf(dev_name,"/dev/video%d",i);
		if (-1 == stat (dev_name, &st)) {
			start_from_4 &= 0;
		}else{
			start_from_4 &= 1;
		}
	}

	if(start_from_4){
		return 4;
	}else{
		return 0;
	}
}
//鎵撳紑video璁惧
int opendevice(int i,char* errorMSG)
{
	struct stat st;

	sprintf(dev_name,"/dev/video%d",i);
	//stat() 鑾峰緱鏂囦欢灞炴�锛屽苟鍒ゆ柇鏄惁涓哄瓧绗﹁澶囨枃浠�
	if (-1 == stat (dev_name, &st)) {
		LOGE("Cannot identify '%s': %d, %s", dev_name, errno, strerror (errno));
		strcpy(errorMSG,strerror (errno));
		return ERROR_LOCAL;
	}

	if (!S_ISCHR (st.st_mode)) {
		LOGE("%s is no device", dev_name);
		strcpy(errorMSG,strerror (errno));
		return ERROR_LOCAL;
	}

	fd = open (dev_name, O_RDWR);

	if (-1 == fd) {
		LOGE("Cannot open '%s': %d, %s", dev_name, errno, strerror (errno));
		strcpy(errorMSG,strerror (errno));
		return ERROR_LOCAL;
	}
	return SUCCESS_LOCAL;
}
//鍒濆鍖栬澶�
int initdevice(char* errorMSG)
{
	struct v4l2_capability cap;
	struct v4l2_cropcap cropcap;
	struct v4l2_crop crop;
	struct v4l2_format fmt;
	unsigned int min;
	//VIDIOC_QUERYCAP 鍛戒护 鏉ヨ幏寰楀綋鍓嶈澶囩殑鍚勪釜灞炴�
	if (-1 == xioctl (fd, VIDIOC_QUERYCAP, &cap)) {
		if (EINVAL == errno) {
			LOGE("%s is no V4L2 device", dev_name);
			char msg[80];
			sprintf(msg,"%s is no V4L2 device", dev_name);
			strcpy(errorMSG,msg);
			return ERROR_LOCAL;
		} else {
			char msg[80];
			sprintf(msg,"%s VIDIOC_QUERYCAP", dev_name);
			strcpy(errorMSG,msg);
			return errnoexit ("VIDIOC_QUERYCAP");
		}
	}
	//V4L2_CAP_VIDEO_CAPTURE 0x00000001
	// 杩欎釜璁惧鏀寔 video capture 鐨勬帴鍙ｏ紝鍗宠繖涓澶囧叿澶�video capture 鐨勫姛鑳�
	if (!(cap.capabilities & V4L2_CAP_VIDEO_CAPTURE)) {
		char msg[80];
		sprintf("%s is no video capture device", dev_name);
		strcpy(errorMSG,msg);
		LOGE("%s is no video capture device", dev_name);
		return ERROR_LOCAL;
	}
	//V4L2_CAP_STREAMING 0x04000000
	// 杩欎釜璁惧鏄惁鏀寔 streaming I/O 鎿嶄綔鍑芥暟
	if (!(cap.capabilities & V4L2_CAP_STREAMING)) {
		char msg[80];
		sprintf("%s does not support streaming i/o", dev_name);
		strcpy(errorMSG,msg);
		LOGE("%s does not support streaming i/o", dev_name);
		return ERROR_LOCAL;
	}
	//鑾峰緱璁惧瀵�Image Cropping 鍜�Scaling 鐨勬敮鎸�
	CLEAR (cropcap);

	cropcap.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;

	if (0 == xioctl (fd, VIDIOC_CROPCAP, &cropcap)) {
		crop.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
		crop.c = cropcap.defrect; 

		if (-1 == xioctl (fd, VIDIOC_S_CROP, &crop)) {
			switch (errno) {
				case EINVAL:
					break;
				default:
					break;
			}
		}
	} else {
	}
	//璁剧疆鍥惧舰鏍煎紡
	CLEAR (fmt);

	fmt.type                = V4L2_BUF_TYPE_VIDEO_CAPTURE;

	fmt.fmt.pix.width       = IMG_WIDTH; 
	fmt.fmt.pix.height      = IMG_HEIGHT;

	fmt.fmt.pix.pixelformat = V4L2_PIX_FMT_YUYV;
	fmt.fmt.pix.field       = V4L2_FIELD_INTERLACED;
	//妫�煡娴佹潈闄�
	if (-1 == xioctl (fd, VIDIOC_S_FMT, &fmt))
		return errnoexit ("VIDIOC_S_FMT");

	min = fmt.fmt.pix.width * 2;
	//姣忚鍍忕礌鎵�崰鐨�byte 鏁�
	if (fmt.fmt.pix.bytesperline < min)
		fmt.fmt.pix.bytesperline = min;
	min = fmt.fmt.pix.bytesperline * fmt.fmt.pix.height;
	if (fmt.fmt.pix.sizeimage < min)
		fmt.fmt.pix.sizeimage = min;

	return initmmap ();

}
//I/O妯″紡閫夋嫨
int initmmap(void)
{
	struct v4l2_requestbuffers req;

	CLEAR (req);

	req.count               = 4;
	req.type                = V4L2_BUF_TYPE_VIDEO_CAPTURE;
	req.memory              = V4L2_MEMORY_MMAP;

	if (-1 == xioctl (fd, VIDIOC_REQBUFS, &req)) {
		if (EINVAL == errno) {
			LOGE("%s does not support memory mapping", dev_name);
			return ERROR_LOCAL;
		} else {
			return errnoexit ("VIDIOC_REQBUFS");
		}
	}

	if (req.count < 2) {
		LOGE("Insufficient buffer memory on %s", dev_name);
		return ERROR_LOCAL;
 	}

	buffers = calloc (req.count, sizeof (*buffers));

	if (!buffers) {
		LOGE("Out of memory");
		return ERROR_LOCAL;
	}

	for (n_buffers = 0; n_buffers < req.count; ++n_buffers) {
		struct v4l2_buffer buf;

		 CLEAR (buf);

		buf.type        = V4L2_BUF_TYPE_VIDEO_CAPTURE;
		buf.memory      = V4L2_MEMORY_MMAP;
		buf.index       = n_buffers;

		if (-1 == xioctl (fd, VIDIOC_QUERYBUF, &buf))
			return errnoexit ("VIDIOC_QUERYBUF");

		buffers[n_buffers].length = buf.length;
		buffers[n_buffers].start =
		mmap (NULL ,
			buf.length,
			PROT_READ | PROT_WRITE,
			MAP_SHARED,
			fd, buf.m.offset);

		if (MAP_FAILED == buffers[n_buffers].start)
			return errnoexit ("mmap");
	}

	return SUCCESS_LOCAL;
}

int startcapturing(void)
{
	unsigned int i;
	enum v4l2_buf_type type;

	for (i = 0; i < n_buffers; ++i) {
		struct v4l2_buffer buf;

		CLEAR (buf);

		buf.type        = V4L2_BUF_TYPE_VIDEO_CAPTURE;
		buf.memory      = V4L2_MEMORY_MMAP;
		buf.index       = i;

		if (-1 == xioctl (fd, VIDIOC_QBUF, &buf))
			return errnoexit ("VIDIOC_QBUF");
	}

	type = V4L2_BUF_TYPE_VIDEO_CAPTURE;

	if (-1 == xioctl (fd, VIDIOC_STREAMON, &type))
		return errnoexit ("VIDIOC_STREAMON");

	return SUCCESS_LOCAL;
}

int readframeonce(void)
{
	for (;;) {
		fd_set fds;
		struct timeval tv;
		int r;

		FD_ZERO (&fds);
		FD_SET (fd, &fds);

		tv.tv_sec = 2;
		tv.tv_usec = 0;

		r = select (fd + 1, &fds, NULL, NULL, &tv);

		if (-1 == r) {
			if (EINTR == errno)
				continue;

			return errnoexit ("select");
		}

		if (0 == r) {
			LOGE("select timeout");
			return ERROR_LOCAL;

		}

		if (readframe ()==1)
			break;

	}

	return SUCCESS_LOCAL;

}


void processimage (const void *p){
		yuyv422toABGRY((unsigned char *)p);
}

int readframe(void){

	struct v4l2_buffer buf;
	unsigned int i;

	CLEAR (buf);

	buf.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
	buf.memory = V4L2_MEMORY_MMAP;
	//buf.memory = V4L2_MEMORY_USERPTR;
	//LOGE("fd=%d,request=%d,buf=%d",fd,VIDIOC_DQBUF,&buf);
	if (-1 == xioctl (fd, VIDIOC_DQBUF, &buf)) {
		switch (errno) {
			case EAGAIN:
				return 0;
			case EIO:
			default:
				return errnoexit ("VIDIOC_DQBUF");
		}
	}

	assert (buf.index < n_buffers);

	processimage (buffers[buf.index].start);

	if (-1 == xioctl (fd, VIDIOC_QBUF, &buf))
		return errnoexit ("VIDIOC_QBUF");

	return 1;
}

int stopcapturing(void)
{
	enum v4l2_buf_type type;

	type = V4L2_BUF_TYPE_VIDEO_CAPTURE;

	if (-1 == xioctl (fd, VIDIOC_STREAMOFF, &type))
		return errnoexit ("VIDIOC_STREAMOFF");

	return SUCCESS_LOCAL;

}

int uninitdevice(void)
{
	unsigned int i;

	for (i = 0; i < n_buffers; ++i)
		if (-1 == munmap (buffers[i].start, buffers[i].length))
			return errnoexit ("munmap");

	free (buffers);

	return SUCCESS_LOCAL;
}
//鍏抽棴璁惧
int closedevice(void)
{
	if (-1 == close (fd)){
		fd = -1;
		return errnoexit ("close");
	}

	fd = -1;
	return SUCCESS_LOCAL;
}

void yuyv422toABGRY(unsigned char *src)
{

	int width=0;
	int height=0;

	width = IMG_WIDTH;
	height = IMG_HEIGHT;

	int frameSize =width*height*2;

	int i;

	if((!rgb || !ybuf)){
		return;
	}
	int *lrgb = NULL;
	int *lybuf = NULL;
		
	lrgb = &rgb[0];
	lybuf = &ybuf[0];

	if(yuv_tbl_ready==0){
		for(i=0 ; i<256 ; i++){
			y1192_tbl[i] = 1192*(i-16);
			if(y1192_tbl[i]<0){
				y1192_tbl[i]=0;
			}

			v1634_tbl[i] = 1634*(i-128);
			v833_tbl[i] = 833*(i-128);
			u400_tbl[i] = 400*(i-128);
			u2066_tbl[i] = 2066*(i-128);
		}
		yuv_tbl_ready=1;
	}

	for(i=0 ; i<frameSize ; i+=4){
		unsigned char y1, y2, u, v;
		y1 = src[i];
		u = src[i+1];
		y2 = src[i+2];
		v = src[i+3];

		int y1192_1=y1192_tbl[y1];
		int r1 = (y1192_1 + v1634_tbl[v])>>10;
		int g1 = (y1192_1 - v833_tbl[v] - u400_tbl[u])>>10;
		int b1 = (y1192_1 + u2066_tbl[u])>>10;

		int y1192_2=y1192_tbl[y2];
		int r2 = (y1192_2 + v1634_tbl[v])>>10;
		int g2 = (y1192_2 - v833_tbl[v] - u400_tbl[u])>>10;
		int b2 = (y1192_2 + u2066_tbl[u])>>10;

		r1 = r1>255 ? 255 : r1<0 ? 0 : r1;
		g1 = g1>255 ? 255 : g1<0 ? 0 : g1;
		b1 = b1>255 ? 255 : b1<0 ? 0 : b1;
		r2 = r2>255 ? 255 : r2<0 ? 0 : r2;
		g2 = g2>255 ? 255 : g2<0 ? 0 : g2;
		b2 = b2>255 ? 255 : b2<0 ? 0 : b2;

		*lrgb++ = 0xff000000 | b1<<16 | g1<<8 | r1;
		*lrgb++ = 0xff000000 | b2<<16 | g2<<8 | r2;

		if(lybuf!=NULL){
			*lybuf++ = y1;
			*lybuf++ = y2;
		}
	}

}


void 
Java_com_example_devicechecker_V4l2Preview_pixeltobmp( JNIEnv* env,jobject thiz,jobject bitmap){

	jboolean bo;


	AndroidBitmapInfo  info;
	void*              pixels;
	int                ret;
	int i;
	int *colors;

	int width=0;
	int height=0;

	if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
		LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
		return;
	}
    
	width = info.width;
	height = info.height;

	if(!rgb || !ybuf) return;

	if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
		LOGE("Bitmap format is not RGBA_8888 !");
		return;
	}

	if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
		LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
	}

	colors = (int*)pixels;
	int *lrgb =NULL;
	lrgb = &rgb[0];

	for(i=0 ; i<width*height ; i++){
		*colors++ = *lrgb++;
	}

	AndroidBitmap_unlockPixels(env, bitmap);

}

// 使用该函数 返回错误类型，如果打开错误，则给出错误提示类型
jobject
Java_com_example_devicechecker_V4l2Preview_prepareCamera( JNIEnv* env,jobject thiz, jint videoid){

	LOGE("Java_com_example_devicechecker_V4l2Preview_prepareCamera\n");
	int ret;
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

	if(camerabase<0){
		camerabase = checkCamerabase();
	}

	ret = opendevice(camerabase + videoid,errorMSG);

	if(ret != ERROR_LOCAL){
		ret = initdevice(errorMSG);
	}
	if(ret != ERROR_LOCAL){
		ret = startcapturing();

		if(ret != SUCCESS_LOCAL){
			stopcapturing();
			uninitdevice ();
			closedevice ();
			LOGE("device resetted");	
		}
	}

	if(ret != ERROR_LOCAL){
		rgb = (int *)malloc(sizeof(int) * (IMG_WIDTH*IMG_HEIGHT));
		ybuf = (int *)malloc(sizeof(int) * (IMG_WIDTH*IMG_HEIGHT));
	}

	//填充ErrorMSG并返回
	// 找到对应的java MSG类,
	if (gDeviceErrorMsgClass == NULL) {
		LOGE("java/lang/RuntimeException Can't find class ErrorMSG");
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
	(*env)->SetIntField(env,object_datarange, jfieldid_result, ret);
	jstring jErrorMSG = (*env)->NewStringUTF(env,(const char*) errorMSG);
	(*env)->SetObjectField(env,object_datarange, lErrorMsgClass,
			jErrorMSG);
	// 释放掉全局引用  may be error delete ref before return object ,just try it
	(*env)->DeleteGlobalRef(env,gDeviceErrorMsgClass);
	return object_datarange;

}	


jobject
Java_com_example_devicechecker_V4l2Preview_prepareCameraWithBase( JNIEnv* env,jobject thiz, jint videoid, jint videobase){
	
		int ret;
		camerabase = videobase;
		return Java_com_example_devicechecker_V4l2Preview_prepareCamera(env,thiz,videoid);
	
}

void 
Java_com_example_devicechecker_V4l2Preview_processCamera( JNIEnv* env,
										jobject thiz){

	readframeonce();
}

void 
Java_com_example_devicechecker_V4l2Preview_stopCamera(JNIEnv* env,jobject thiz){

	stopcapturing ();
	uninitdevice ();
	closedevice ();
	if(rgb) free(rgb);
	if(ybuf) free(ybuf);
	fd = -1;

}

