package com.example.devicechecker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
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
		deviceIsOK = (Button)findViewById(R.id.AVisOK); 
		deviceIsNotOK = (Button)findViewById(R.id.trylinuxDeivice);
		priviewSurface = (FrameLayout)findViewById(R.id.surfaceView1);		
	}
	// TODO 获取设备平台信息，并开始写如result文件
	private void getDevicesInfo()
	{
		
	}
		
	
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		// 载入 preview view !!!
		setContentView(R.layout.autotest);
		System.out.println("AutoActivity  onCreate !!!!!!!!!!!!!!!!");			
		initViews();		
		getDevicesInfo();
		Intent intent = this.getIntent(); 
		// 获取 传入的数据，并判断初始化哪一种设备
		// Android 音视频测试		
		androidAVTest = new AndroidAVTest(this);		
		priviewSurface.addView(androidAVTest);			
		// 处理两个button		
		
		deviceIsOK.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// 保存信息
			}
		}) ;  
		
		deviceIsNotOK.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// 检测设备权限
				//priviewSurface.removeView(androidAVTest);
				
			}
		});
		
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
