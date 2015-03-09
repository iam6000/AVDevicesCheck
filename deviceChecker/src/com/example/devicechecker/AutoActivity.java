package com.example.devicechecker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class AutoActivity extends Activity {
	
	// 根据不同的条件，创建不同的Preview类，并且进行preview的展示
	private Button deviceIsOK ; 
	private Button deviceIsNotOK;
	private FrameLayout priviewSurface;
	private CameraPreview camPreview ; 
	private V4l2Preview v4l2Preview ; 
	private AndroidAVTest androidAVTest ; 
	
	
	private void initViews()
	{
		deviceIsOK = (Button)findViewById(R.id.VideoIsOK); 
		deviceIsNotOK = (Button)findViewById(R.id.VideoIsNotOK);
		priviewSurface = (FrameLayout)findViewById(R.id.previewsurfaceView);
		
	}
	
	
	
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		// 载入 preview view !!!
		setContentView(R.layout.preview);
		System.out.println("PrviewActivity  onCreate !!!!!!!!!!!!!!!!");			
		initViews();
				
		Intent intent = this.getIntent(); 
		// 获取 传入的数据，并判断初始化哪一种设备
		Bundle bundle = intent.getExtras();
		String value = bundle.getString("Type");		
		if(value.equals("v4l2Video"))
		{
			// v4l2 视频测试
			System.out.println("new V4l2Preview !!!!!!!!!!!!!!!!");	
			v4l2Preview = new V4l2Preview(this); 	
			priviewSurface.removeAllViews();
			priviewSurface.addView(v4l2Preview);				
			
		}	
		else if(value.equals("AndroidVideo"))
		{
			// android 视频测试
			System.out.println("new CameraPreview !!!!!!!!!!!!!!!!");			
			camPreview = new CameraPreview(this);	
			priviewSurface.removeAllViews();
			priviewSurface.addView(camPreview);					
		}	
		else if(value.equals("AndroidAV"))
		{
			// Android 音视频测试
			System.out.println("new AndroidAV Preview !!!!!!!!!!!!!!!!");	
			androidAVTest = new AndroidAVTest(this);
			//setContentView(androidAVTest);
			priviewSurface.addView(androidAVTest);	
			//priviewSurface = androidAVTest ;
		}
		else if(value.equals("LinuxAV"))
		{
			//TODO 
		}
		
	}
	
	
	
	
	protected void onDestroy() {
		super.onDestroy();
		android.os.Process.killProcess(android.os.Process.myPid());
		// 跳转回原activity 并结束当前activity
		Intent intent = new Intent();
		intent.setClass(this,manualActivity.class);
		startActivity(intent);
		this.finish();
	}

}
