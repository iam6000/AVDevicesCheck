/*use Camera Preview, just use android Camera  */

package com.example.devicechecker;
import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;

import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;

public class CameraPreview extends SurfaceView implements Callback, Runnable {
	
	static final String tag = "CameraPreview";
	private static final boolean DEBUG = true;
	protected Context context;
	private SurfaceHolder holder;
    Thread mainLoop = null;
    Camera camera = null;

	public CameraPreview(Context context) {		
		super(context);
		this.context = context;
		if(DEBUG) Log.d("WebCam","CameraPreview constructed");
		setFocusable(true);		
		holder = getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);	
		// TODO Auto-generated constructor stub
		//surfaceCreated(holder);		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// TODO Auto-generated method stub
		// TODO 调用getParameters
		if(camera != null)
		{
			Camera.Parameters  parameters = camera.getParameters();
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
				camera.setParameters(parameters);
				camera.setPreviewDisplay(holder);				
			}catch(IOException excaption){
				camera.release();
				camera= null ;
				}
			if(camera != null)
			{
				camera.startPreview();
			}
			
		}
		else 
		{
			if(DEBUG) Log.d("Android Camera","Camera is not found");
		}

	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		// TODO 1，使用 getNumberOfCameras来获取摄像头数目
		// TODO 2,使用 getCamerainfo 得到摄像头ID
		// TODO 3,使用 open 打开摄像头 
		
        Log.e(tag, "Camera.getNumberOfCameras()" +Camera.getNumberOfCameras());
        
        // 仅打开一个摄像头
        for(int i = 0 ; i < Camera.getNumberOfCameras(); ++i)
        {
        	Log.e(tag, "Camera.CameraInfo()" );
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            try{ 
            	 camera = Camera.open(i); 
			}catch(Exception e){}        
           
        }

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		if(camera != null)
		{
			camera.stopPreview(); 
			camera.release(); 
			camera = null ;
		}
		else 
		{
			// 写入cameraError 信息
		}
	}

}
