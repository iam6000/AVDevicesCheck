package com.example.devicechecker;



import java.beans.IndexedPropertyChangeEvent;

import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;  
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class MainActivity extends Activity {
	
	// set Button 
	private Button autoCheck ; 
	private Button manualCheck ; 
	private Button checkUploadData ; 
	private Button saveLocalData ;
	
	private ResultHandler hander = new ResultHandler(MainActivity.this);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// 增加 StrictMode 用于发起http请求
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
		
		initTestView();
	}
	
	// 定义 button的操作 
	
	private void initTestView(){
		// 查找到Button的 id
		autoCheck = (Button)findViewById(R.id.Button01);
		manualCheck = (Button)findViewById(R.id.Button02);
		checkUploadData = (Button)findViewById(R.id.Button04);
		saveLocalData = (Button)findViewById(R.id.Button03);		
						
		// 定义每一个button所执行的操作
		// 定义 video 操作
		autoCheck.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				System.out.println("Button auto test");
				LayoutInflater inflater = getLayoutInflater();
				final View autoLayout = inflater.inflate(R.layout.preview, null); 								
				// 创建 一个intent ， 用于 进行activity 的跳转 
//				Intent intent = new Intent();
//				// 设置 要跳转的 目标 activity ，从 mainActivity 跳到manualActivity 
//				// use AndroidAV
//				intent.setClass(MainActivity.this, PreviewActivity.class);
//				intent.putExtra("Type","AndroidAV");
//				//启动 目标intent
//				startActivity(intent);		
				Intent intent = new Intent(); 
				intent.setClass(MainActivity.this,DevicePermissionActivity.class );
				startActivity(intent);		
				
			}
		});
		
		//
		manualCheck.setOnClickListener(new OnClickListener() {	
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				System.out.println("Button manual test");
				LayoutInflater inflater = getLayoutInflater();										
				// 创建 一个intent ， 用于 进行activity 的跳转 
				Intent intent = new Intent();
				// 设置 要跳转的 目标 activity ，从 mainActivity 跳到manualActivity 
				intent.setClass(MainActivity.this, manualActivity.class);
				//启动 目标intent
				startActivity(intent);						
					
				}
				
				// 是否用这种方法可以对下一层的view进行操作		
			
		}) ;
		
		// 无网络连接情况下，保存设备数据
		saveLocalData.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// TODO Auto-generated method stub
				System.out.println("bt_AlsaAudio test !!!!!!!!!!!!!!!!");			
				// 继续跳转 到下一个activity 
				// 创建 一个intent ， 用于 进行activity 的跳转 
				Intent intent = new Intent();
				// 设置 要跳转的 目标 activity ，从 mainActivity 跳到manualActivity 
				intent.setClass(MainActivity.this, AudioActivity.class);
				// 传入数据进去，确定使用哪一种audio，是android设备的还是alsa设备
				intent.putExtra("Type", "tinyalsa");				
				//启动 目标intent
				startActivity(intent);			
				
			}
		});

		// 手动获取到的数据，upload
		checkUploadData.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub	
				
				if(hander.upLoadResult())
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
					builder.setTitle("完成文件传输");
					builder.setMessage("测试结果已上传至服务器\n");
					builder.setCancelable(false);
					builder.setPositiveButton("确定",null);
					builder.create().show();
				}
				else 
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
					builder.setTitle("文件传输失败");
					builder.setMessage("文件传输失败，请确定是否已经进行设备检测，并检查网络\n");
					builder.setCancelable(false);
					builder.setPositiveButton("确定",null);
					builder.create().show();
				}
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub	
		ResultHandler  handler = new ResultHandler(MainActivity.this);
		handler.removeResultFile(); 
		//android.os.Process.killProcess(android.os.Process.myPid());
		super.onPause();
	}

	
//	protected void onDestroy() {
//		// 清掉检测数据
//		ResultHandler  handler = new ResultHandler(MainActivity.this);
//		handler.removeResultFile(); 
//		super.onDestroy();
//		// 杀死当前进程
//		
//		android.os.Process.killProcess(android.os.Process.myPid());
//	}


}
