package com.example.devicechecker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class V4l2Preview extends SurfaceView implements SurfaceHolder.Callback, Runnable  {

	static final String tag = "V4l2Preview";
	private static final boolean DEBUG = true;
	protected Context context;
	private SurfaceHolder holder;
    Thread mainLoop = null;
	private Bitmap bmp=null;

	private boolean cameraExists=false;
	private boolean shouldStop=false;
	private DeviceErrorMsg  mDeviceMsg  = new DeviceErrorMsg(); 	
	
	/// /dev/videox (x=cameraId+cameraBase) is used.
	// In some omap devices, system uses /dev/video[0-3],
	// so users must use /dev/video[4-].
	// In such a case, try cameraId=0 and cameraBase=4
	private int cameraId=0;
	private int cameraBase=0;
	//
	
	// This definition also exists in ImageProc.h.
	// Webcam must support the resolution 640x480 with YUYV format. 
	static final int IMG_WIDTH=640;
	static final int IMG_HEIGHT=480;

	// The following variables are used to draw camera images.
    private int winWidth=0;
    private int winHeight=0;
    private Rect rect;
    private int dw, dh;
    private float rate;
  
    // JNI functions
    public native DeviceErrorMsg prepareCamera(int videoid);
    public native DeviceErrorMsg prepareCameraWithBase(int videoid, int camerabase);
    public native void processCamera();
    public native void stopCamera();

	public native void pixeltobmp(Bitmap bitmap);
    static {
        System.loadLibrary("v4l2Device");
    }
    
	V4l2Preview(Context context) {
		super(context);
		this.context = context;
		if(DEBUG) Log.d("WebCam","CameraPreview constructed");
		setFocusable(true);
		
		holder = getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);	
	}
	
	int count = 0;
	
    @Override
    public void run() {
        while (true && cameraExists) {
        	Log.i(tag, "loop");
        	//obtaining display area to draw a large image
        	if(winWidth==0){
        		winWidth=this.getWidth();
        		winHeight=this.getHeight();

        		if(winWidth*3/4<=winHeight){
        			dw = 0;
        			dh = (winHeight-winWidth*3/4)/2;
        			rate = ((float)winWidth)/IMG_WIDTH;
        			rect = new Rect(dw,dh,dw+winWidth-1,dh+winWidth*3/4-1);
        		}else{
        			dw = (winWidth-winHeight*4/3)/2;
        			dh = 0;
        			rate = ((float)winHeight)/IMG_HEIGHT;
        			rect = new Rect(dw,dh,dw+winHeight*4/3 -1,dh+winHeight-1);
        		}
        	}
        	
        	// obtaining a camera image (pixel data are stored in an array in JNI).
        	processCamera();
        	// camera image to bmp
        	pixeltobmp(bmp);
        	
            Canvas canvas = getHolder().lockCanvas();
            if (canvas != null)
            {
            	// draw camera bmp on canvas
            	canvas.drawBitmap(bmp,null,rect,null);

            	getHolder().unlockCanvasAndPost(canvas);
            }

            if(shouldStop){
            	shouldStop = false;  
            	Log.i(tag, "break");
            	break;
            }	        
        }
        Log.i(tag, "�߳��˳�12");
    }

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		System.out.println("new V4l2Preview surfaceCreated!!!");
		if(DEBUG) Log.d("WebCam", "surfaceCreated");
		if(bmp==null){
			bmp = Bitmap.createBitmap(IMG_WIDTH, IMG_HEIGHT, Bitmap.Config.ARGB_8888);
		}
		// /dev/videox (x=cameraId + cameraBase) is used
		mDeviceMsg.DeviceType = "v4l2Device";
		mDeviceMsg = prepareCameraWithBase(cameraId, cameraBase);
		
		if(mDeviceMsg.result!=-1) cameraExists = true;
		if(mDeviceMsg.isErrorHappen())
		{
			// show Error
			// do Error ���� ����return  
			System.out.println("get Error result :" + mDeviceMsg.result );	
			System.out.println("get Error MSG :" + mDeviceMsg.ErrorMsg );	
			AlertDialog.Builder availableBuilder = new AlertDialog.Builder(this.context);				
			availableBuilder.setTitle("��Ƶ�ɼ��豸����");
			availableBuilder.setMessage(mDeviceMsg.ErrorMsg);
			availableBuilder.setNegativeButton("����", null);
			availableBuilder.create().show();	
			surfaceDestroyed(this.holder);
		}
		else 
		{
			  mainLoop = new Thread(this);
		      mainLoop.start();	
		}
		
      	
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		if(DEBUG) Log.d("WebCam", "surfaceChanged");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if(DEBUG) Log.d("WebCam", "surfaceDestroyed");
		if(cameraExists){
			shouldStop = true;
			while(shouldStop){
				try{ 
					Thread.sleep(100); // wait for thread stopping
				}catch(Exception e){}
			}
		}
		stopCamera();
		//super.onDestroy();
	}  

}




