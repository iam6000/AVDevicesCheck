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
		// ���� StrictMode ���ڷ���http����
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
		
		initTestView();
	}
	
	// ���� button�Ĳ��� 
	
	private void initTestView(){
		// ���ҵ�Button�� id
		autoCheck = (Button)findViewById(R.id.Button01);
		manualCheck = (Button)findViewById(R.id.Button02);
		checkUploadData = (Button)findViewById(R.id.Button04);
		saveLocalData = (Button)findViewById(R.id.Button03);		
						
		// ����ÿһ��button��ִ�еĲ���
		// ���� video ����
		autoCheck.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				System.out.println("Button auto test");
				LayoutInflater inflater = getLayoutInflater();
				final View autoLayout = inflater.inflate(R.layout.preview, null); 								
				// ���� һ��intent �� ���� ����activity ����ת 
//				Intent intent = new Intent();
//				// ���� Ҫ��ת�� Ŀ�� activity ���� mainActivity ����manualActivity 
//				// use AndroidAV
//				intent.setClass(MainActivity.this, PreviewActivity.class);
//				intent.putExtra("Type","AndroidAV");
//				//���� Ŀ��intent
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
				// ���� һ��intent �� ���� ����activity ����ת 
				Intent intent = new Intent();
				// ���� Ҫ��ת�� Ŀ�� activity ���� mainActivity ����manualActivity 
				intent.setClass(MainActivity.this, manualActivity.class);
				//���� Ŀ��intent
				startActivity(intent);						
					
				}
				
				// �Ƿ������ַ������Զ���һ���view���в���		
			
		}) ;
		
		// ��������������£������豸����
		saveLocalData.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// TODO Auto-generated method stub
				System.out.println("bt_AlsaAudio test !!!!!!!!!!!!!!!!");			
				// ������ת ����һ��activity 
				// ���� һ��intent �� ���� ����activity ����ת 
				Intent intent = new Intent();
				// ���� Ҫ��ת�� Ŀ�� activity ���� mainActivity ����manualActivity 
				intent.setClass(MainActivity.this, AudioActivity.class);
				// �������ݽ�ȥ��ȷ��ʹ����һ��audio����android�豸�Ļ���alsa�豸
				intent.putExtra("Type", "tinyalsa");				
				//���� Ŀ��intent
				startActivity(intent);			
				
			}
		});

		// �ֶ���ȡ�������ݣ�upload
		checkUploadData.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub	
				
				if(hander.upLoadResult())
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
					builder.setTitle("����ļ�����");
					builder.setMessage("���Խ�����ϴ���������\n");
					builder.setCancelable(false);
					builder.setPositiveButton("ȷ��",null);
					builder.create().show();
				}
				else 
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
					builder.setTitle("�ļ�����ʧ��");
					builder.setMessage("�ļ�����ʧ�ܣ���ȷ���Ƿ��Ѿ������豸��⣬���������\n");
					builder.setCancelable(false);
					builder.setPositiveButton("ȷ��",null);
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
//		// ����������
//		ResultHandler  handler = new ResultHandler(MainActivity.this);
//		handler.removeResultFile(); 
//		super.onDestroy();
//		// ɱ����ǰ����
//		
//		android.os.Process.killProcess(android.os.Process.myPid());
//	}


}
