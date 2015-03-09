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
	
	// ���ݲ�ͬ��������������ͬ��Preview�࣬���ҽ���preview��չʾ
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
	// TODO ��ȡ�豸ƽ̨��Ϣ������ʼд��result�ļ�
	private void getDevicesInfo()
	{
		
	}
		
	
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		// ���� preview view !!!
		setContentView(R.layout.autotest);
		System.out.println("AutoActivity  onCreate !!!!!!!!!!!!!!!!");			
		initViews();		
		getDevicesInfo();
		Intent intent = this.getIntent(); 
		// ��ȡ ��������ݣ����жϳ�ʼ����һ���豸
		// Android ����Ƶ����		
		androidAVTest = new AndroidAVTest(this);		
		priviewSurface.addView(androidAVTest);			
		// ��������button		
		
		deviceIsOK.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// ������Ϣ
			}
		}) ;  
		
		deviceIsNotOK.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// ����豸Ȩ��
				//priviewSurface.removeView(androidAVTest);
				
			}
		});
		
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
