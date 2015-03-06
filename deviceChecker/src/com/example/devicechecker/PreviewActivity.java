package com.example.devicechecker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class PreviewActivity extends Activity {
	
	// 根据不同的条件，创建不同的Preview类，并且进行preview的展示
	private CameraPreview camPreview ; 
	private V4l2Preview v4l2Preview ; 
	
	
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		System.out.println("PrviewActivity  onCreate !!!!!!!!!!!!!!!!");			
				
		Intent intent = this.getIntent(); 
		// 获取 传入的数据，并判断初始化哪一种设备
		Bundle bundle = intent.getExtras();
		String value = bundle.getString("Type");		
		if(value.equals("v4l2"))
		{
			System.out.println("new V4l2Preview !!!!!!!!!!!!!!!!");	
			v4l2Preview = new V4l2Preview(this); 
			setContentView(v4l2Preview);
		}	
		else if(value.equals("android"))
		{
			System.out.println("new CameraPreview !!!!!!!!!!!!!!!!");	
			camPreview = new CameraPreview(this);	
			setContentView(camPreview);
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
