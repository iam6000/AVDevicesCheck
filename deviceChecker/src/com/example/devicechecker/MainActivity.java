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
	
	// ���� button�Ĳ��� 
	private void initTestView(){
		// ���ҵ�Button�� id
		autoCheck = (Button)findViewById(R.id.Button01);
		manualCheck = (Button)findViewById(R.id.Button02);
		checkUploadData = (Button)findViewById(R.id.Button03);
		saveLocalData = (Button)findViewById(R.id.Button04);		
						
		// ����ÿһ��button��ִ�еĲ���
		// ���� video ����
		autoCheck.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				System.out.println("Button auto test");
				LayoutInflater inflater = getLayoutInflater();
				final View manualLayout = inflater.inflate(R.layout.autotest, null); 								
				// ���� һ��intent �� ���� ����activity ����ת 
				Intent intent = new Intent();
				// ���� Ҫ��ת�� Ŀ�� activity ���� mainActivity ����manualActivity 
				intent.setClass(MainActivity.this, AutoActivity.class);
				//���� Ŀ��intent
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
				
			}
		});

		// �ֶ���ȡ�������ݣ�upload
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
