package com.example.devicechecker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;


public class PreviewActivity extends Activity {
	
	// ��ݲ�ͬ��������������ͬ��Preview�࣬���ҽ���preview��չʾ
	protected Context mContext;
	private Button deviceIsOK ; 
	private Button deviceIsNotOK;
	private FrameLayout priviewSurface;
	private FrameLayout priviewSurface1;
	private FrameLayout priviewSurface2;
	private CameraPreview camPreview ; 
	private V4l2Preview camPreview1 ; 
	private V4l2Preview camPreview2 ; 
	private V4l2Preview v4l2Preview ; 
	private AndroidAVTest androidAVTest ; 	
	private String devicetype ;
	
	private DeviceScan mDeviceScaner; 
	
	int videoID = 0 ;
	
	private void initViews()
	{
		deviceIsOK = (Button)findViewById(R.id.VideoIsOK); 
		deviceIsNotOK = (Button)findViewById(R.id.VideoIsNotOK);
		priviewSurface = (FrameLayout)findViewById(R.id.previewsurfaceView);
		priviewSurface1 = (FrameLayout)findViewById(R.id.previewsurfaceView01);		
		//priviewSurface2 = (FrameLayout)findViewById(R.id.previewsurfaceView02);
	}
	
	
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		
		// ���� preview view !!!
		setContentView(R.layout.preview);
		mContext = PreviewActivity.this;
		System.out.println("PrviewActivity  onCreate !!!!!!!!!!!!!!!!");			
		initViews();
				
		Intent intent = this.getIntent(); 
		// ��ȡ �������ݣ����жϳ�ʼ����һ���豸
		Bundle bundle = intent.getExtras();
		String value = bundle.getString("Type");		
		if(value.equals("v4l2Video"))
		{
			// v4l2 ��Ƶ����
			System.out.println("new V4l2Preview !!!!!!!!!!!!!!!!");	
			devicetype = "linux V4l2 Video deivce";
			// do check device Id  
			
			
			mDeviceScaner = new DeviceScan(mContext);
			mDeviceScaner.beginScan() ; 				
						
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setTitle("USB�豸����");
			builder.setMessage("�����һ������ͷ�β������\n ������ͷ�Ѳ��ϣ���ε����ٲ���\n ������ͷδ���ϣ���ֱ�Ӳ���\n");
			builder.setCancelable(false);	

			
			builder.setPositiveButton("����ɲ�β��������Ѳ�������ͷ", new DialogInterface.OnClickListener(){				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {			

					// TODO Auto-generated method stub		
					// set stop 
					/*
					mDeviceScaner.setdoPcmDevicesScan(false); 
					mDeviceScaner.setdoVideoDevicesScan(false);
					
					if(mDeviceScaner.getVideoDevices() != null)
					{
						videoID = Integer.parseInt(String.valueOf(mDeviceScaner.getVideoDevices().charAt(5)));
					}
					*/
					//v4l2Preview = new V4l2Preview(PreviewActivity.this, videoID); 	
					v4l2Preview = new V4l2Preview(PreviewActivity.this, 0); 
					priviewSurface.removeAllViews();
					priviewSurface.addView(v4l2Preview);	
								
				}
			});
			builder.create().show();		
			
		}	
		else if(value.equals("AndroidVideo"))
		{
			// android ��Ƶ����
			System.out.println("new CameraPreview !!!!!!!!!!!!!!!!");
			devicetype = "Android Camera Video device";
			camPreview = new CameraPreview(this,0);	
			//camPreview1  = new V4l2Preview(PreviewActivity.this, 1);	
			//camPreview2  = new V4l2Preview(PreviewActivity.this, 2);
			priviewSurface.removeAllViews();
			priviewSurface.addView(camPreview);		
			//priviewSurface1.removeAllViews();
			//priviewSurface1.addView(camPreview1);	
			//priviewSurface2.removeAllViews();
			//priviewSurface2.addView(camPreview2);	
		}	
		else if(value.equals("AndroidAV"))
		{
			// Android ����Ƶ����
			System.out.println("new AndroidAV Preview !!!!!!!!!!!!!!!!");	
			devicetype = "Android Camera Video and Audio device";
			androidAVTest = new AndroidAVTest(this);
			//setContentView(androidAVTest);
			priviewSurface.addView(androidAVTest);	
			//priviewSurface = androidAVTest ;
		}
		else if(value.equals("LinuxAV"))
		{
			//TODO 
		}
		
		//
		deviceIsOK.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// record result 
				System.out.println("devices " + devicetype + "is OK !!!!!!!!!!!!!!!!");	
				Intent intent = new Intent();
				intent.setClass( PreviewActivity.this, manualActivity.class);	
				
				startActivity(intent);
				PreviewActivity.this.finish();
			}
		}) ; 
		
		deviceIsNotOK.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				System.out.println("devices " + devicetype + "is  not OK !!!!!!!!!!!!!!!!");	
				Intent intent = new Intent();
				intent.setClass( PreviewActivity.this, manualActivity.class);		
				
				startActivity(intent);
				PreviewActivity.this.finish();
				
			}
		}); 
		
		
	}
	
	
	
	
	protected void onDestroy() {
		super.onDestroy();
		android.os.Process.killProcess(android.os.Process.myPid());
		// ��ת��ԭactivity ������ǰactivity
		Intent intent = new Intent();
		intent.setClass(this,manualActivity.class);
		startActivity(intent);
		this.finish();
	}

}
