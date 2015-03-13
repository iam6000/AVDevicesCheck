package com.example.devicechecker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class manualActivity extends Activity {
	
	private Button bt_Permission; 
	//private Button bt_LinuxAVTest ; 
	
	private Button bt_AndroidAudio ; 
	private Button bt_AndroidVideo ; 
	
	private Button bt_v4l2Video ; 
	private Button bt_AlsaAudio ; 
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//this.setContentView(R.layout.sub);
		setContentView(R.layout.manualtest);
		
		initUIButton();
		ManualViewListener();
	}
	
	private void initUIButton()
	{	
		bt_Permission = (Button)findViewById(R.id.DevicePermission);
		//bt_LinuxAVTest = (Button)findViewById(R.id.LinuxAV);
		
		bt_AndroidVideo = (Button)findViewById(R.id.AndroidVideo);
		bt_AndroidAudio = (Button)findViewById(R.id.AndroidAudio);
		
		bt_AlsaAudio = (Button)findViewById(R.id.linuxAlsa);
		bt_v4l2Video = (Button)findViewById(R.id.linuxV4l2);
		
	}
	
	// 针对各个模块，是否太过于麻烦了
	private void ManualViewListener()
	{
		// 监听AndoridAV 
		bt_Permission.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				System.out.println("bt_Permission test !!!!!!!!!!!!!!!!");
				Intent intent = new Intent(); 
				intent.setClass(manualActivity.this,DevicePermissionActivity.class );
				
				startActivity(intent);						
			} 
		}); 
		
		/*
		//监听 Linux层AV
		bt_LinuxAVTest.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		*/
		// 监听 android Video
		bt_AndroidVideo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				System.out.println("bt_AndroidVideo test !!!!!!!!!!!!!!!!");
				Intent intent = new Intent(); 
				intent.setClass(manualActivity.this,PreviewActivity.class );
				intent.putExtra("Type","AndroidVideo");
				startActivity(intent);					
			}
		}); 		
		
		
		// 监听AndoridAudio
		bt_AndroidAudio.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				System.out.println("bt_AndroidAudio test !!!!!!!!!!!!!!!!");			
				// 继续跳转 到下一个activity 
				// 创建 一个intent ， 用于 进行activity 的跳转 
				Intent intent = new Intent();
				// 设置 要跳转的 目标 activity ，从 mainActivity 跳到manualActivity 
				intent.setClass(manualActivity.this, AudioActivity.class);
				// 传入数据进去，确定使用哪一种audio，是android设备的还是alsa设备
				intent.putExtra("Type", "android");				
				//启动 目标intent
				startActivity(intent);						 
			}
		});
		
		//监听 v4l2 button
		bt_v4l2Video.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {				
				// TODO Auto-generated method stub
				System.out.println("bt_v4l2Video test !!!!!!!!!!!!");
				// 创建 一个intent ， 用于 进行activity 的跳转 
				Intent intent = new Intent();
				intent.setClass(manualActivity.this, PreviewActivity.class);				
				intent.putExtra("Type","v4l2Video");				
				//start 目标intent
				startActivity(intent);					
			}
		});
		
		
		//监听AlsaAudio
		bt_AlsaAudio.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				System.out.println("bt_AlsaAudio test !!!!!!!!!!!!!!!!");			
				// 继续跳转 到下一个activity 
				// 创建 一个intent ， 用于 进行activity 的跳转 
				Intent intent = new Intent();
				// 设置 要跳转的 目标 activity ，从 mainActivity 跳到manualActivity 
				intent.setClass(manualActivity.this, AudioActivity.class);
				// 传入数据进去，确定使用哪一种audio，是android设备的还是alsa设备
				intent.putExtra("Type", "tinyalsa");				
				//启动 目标intent
				startActivity(intent);						 
			}
		});
		
	}
	
	
	protected void onDestroy() {

		super.onDestroy();
		// 杀死当前进程
		
		//android.os.Process.killProcess(android.os.Process.myPid());
	}

}
