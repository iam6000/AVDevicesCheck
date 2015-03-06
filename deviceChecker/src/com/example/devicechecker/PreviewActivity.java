package com.example.devicechecker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class PreviewActivity extends Activity {
	
	// ���ݲ�ͬ��������������ͬ��Preview�࣬���ҽ���preview��չʾ
	private CameraPreview camPreview ; 
	private V4l2Preview v4l2Preview ; 
	
	
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		System.out.println("PrviewActivity  onCreate !!!!!!!!!!!!!!!!");			
				
		Intent intent = this.getIntent(); 
		// ��ȡ ��������ݣ����жϳ�ʼ����һ���豸
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
		// ��ת��ԭactivity ��������ǰactivity
		Intent intent = new Intent();
		intent.setClass(this,manualActivity.class);
		startActivity(intent);
		this.finish();
	}

}
