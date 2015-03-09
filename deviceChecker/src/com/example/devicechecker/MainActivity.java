package com.example.devicechecker;



import java.beans.IndexedPropertyChangeEvent;

import android.os.Bundle;
import android.os.Handler;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initTestView();
	}
	
	// 定义 button的操作 
	private void initTestView(){
		// 查找到Button的 id
		autoCheck = (Button)findViewById(R.id.Button01);
		manualCheck = (Button)findViewById(R.id.Button02);
		checkUploadData = (Button)findViewById(R.id.Button03);
		saveLocalData = (Button)findViewById(R.id.Button04);		
						
		// 定义每一个button所执行的操作
		// 定义 video 操作
		autoCheck.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				System.out.println("Button auto test");
				LayoutInflater inflater = getLayoutInflater();
				final View manualLayout = inflater.inflate(R.layout.autotest, null); 								
				// 创建 一个intent ， 用于 进行activity 的跳转 
				Intent intent = new Intent();
				// 设置 要跳转的 目标 activity ，从 mainActivity 跳到manualActivity 
				intent.setClass(MainActivity.this, AutoActivity.class);
				//启动 目标intent
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
				final View manualLayout = inflater.inflate(R.layout.manualtest, null); 								
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
				
			}
		});

		// 手动获取到的数据，upload
		checkUploadData.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub				
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
