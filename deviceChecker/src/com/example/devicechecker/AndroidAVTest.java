package com.example.devicechecker;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class AndroidAVTest extends SurfaceView implements Callback, Runnable {
	static final String tag = "AndroidAVTest";
	private SurfaceHolder holder;
	private static final boolean DEBUG = true;
	protected Context context;
	private AndroidAudio mAudio = null  ; 
	private Camera mCamera = null;
	

	public AndroidAVTest(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		if(DEBUG) 
		{
			Log.d("AndroidAVTest","CameraPreview constructed");
		}
		setFocusable(true);		
		holder = getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);	
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// TODO Auto-generated method stub
		// start Camera preview
		if(mCamera != null )
		{
			Camera.Parameters  parameters = mCamera.getParameters();
		    format = ImageFormat.NV21;	
		    width = 640 ; 
		    height = 480 ;
		    parameters.setPreviewFormat(format);			
			parameters.setPreviewSize(width, height);
			List<Size> vSizeList = parameters.getSupportedPictureSizes();
			for(int i = 0 ; i < vSizeList.size(); i ++)
			{
				Size vSize = vSizeList.get(i);
			}
			
			
			try{
				mCamera.setParameters(parameters);
				mCamera.setPreviewDisplay(holder);
				
			}catch(IOException excaption){
				mCamera.release();
				mCamera= null ;
			}
			if(mCamera != null)
			{
				mCamera.startPreview();		
			}
			
		}
		// start audio devices
		mAudio.start();
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		// 初始化摄像头和 audio 
        Log.e(tag, "Camera.getNumberOfCameras()" +Camera.getNumberOfCameras());        
        // 仅打开一个摄像头
        for(int i = 0 ; i < Camera.getNumberOfCameras(); ++i)
        {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            try{ 
            	 mCamera = Camera.open(i); 
			}catch(Exception e){}        
           
        }
        
        // 初始化 android Audio
        mAudio = new AndroidAudio(); 
        

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		if(mAudio != null)
		{
			mAudio.stop(); 		
		}
		else 
		{
			// mAudio设备没有成功，记录log
		}
		if(mCamera != null)
		{
			mCamera.stopPreview();
			mCamera.release(); 
			mCamera = null ;
		}		
		else
		{
			//Camara 设备存在问题，记录log
		}

	}

}
