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
	
	private Button bt_AndroidAVTest; 
	private Button bt_LinuxAVTest ; 
	
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
		bt_AndroidAVTest = (Button)findViewById(R.id.AndroidAV);
		bt_LinuxAVTest = (Button)findViewById(R.id.LinuxAV);
		
		bt_AndroidVideo = (Button)findViewById(R.id.AndroidVideo);
		bt_AndroidAudio = (Button)findViewById(R.id.AndroidAudio);
		
		bt_AlsaAudio = (Button)findViewById(R.id.linuxAlsa);
		bt_v4l2Video = (Button)findViewById(R.id.linuxV4l2);
		
	}
	
	// ��Ը���ģ�飬�Ƿ�̫�����鷳��
	private void ManualViewListener()
	{
		// ����AndoridAV 
		bt_AndroidAVTest.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				System.out.println("bt_AndroidAVTest test !!!!!!!!!!!!!!!!");
				Intent intent = new Intent(); 
				intent.setClass(manualActivity.this,PreviewActivity.class );
				intent.putExtra("Type","AndroidAV");
				startActivity(intent);				
				
			} 
		}); 
		
		
		//���� Linux��AV
		bt_LinuxAVTest.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		// ���� android Video
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
		
		
		// ����AndoridAudio
		bt_AndroidAudio.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				System.out.println("bt_AndroidAudio test !!!!!!!!!!!!!!!!");			
				// ������ת ����һ��activity 
				// ���� һ��intent �� ���� ����activity ����ת 
				Intent intent = new Intent();
				// ���� Ҫ��ת�� Ŀ�� activity ���� mainActivity ����manualActivity 
				intent.setClass(manualActivity.this, AudioActivity.class);
				// �������ݽ�ȥ��ȷ��ʹ����һ��audio����android�豸�Ļ���alsa�豸
				intent.putExtra("Type", "android");				
				//���� Ŀ��intent
				startActivity(intent);						 
			}
		});
		
		//���� v4l2 button
		bt_v4l2Video.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {				
				// TODO Auto-generated method stub
				System.out.println("bt_v4l2Video test !!!!!!!!!!!!");
				// ���� һ��intent �� ���� ����activity ����ת 
				Intent intent = new Intent();
				intent.setClass(manualActivity.this, PreviewActivity.class);				
				intent.putExtra("Type","v4l2Video");				
				//start Ŀ��intent
				startActivity(intent);					
			}
		});
		
		
		//����AlsaAudio
		bt_AlsaAudio.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				System.out.println("bt_AlsaAudio test !!!!!!!!!!!!!!!!");			
				// ������ת ����һ��activity 
				// ���� һ��intent �� ���� ����activity ����ת 
				Intent intent = new Intent();
				// ���� Ҫ��ת�� Ŀ�� activity ���� mainActivity ����manualActivity 
				intent.setClass(manualActivity.this, AudioActivity.class);
				// �������ݽ�ȥ��ȷ��ʹ����һ��audio����android�豸�Ļ���alsa�豸
				intent.putExtra("Type", "tinyalsa");				
				//���� Ŀ��intent
				startActivity(intent);						 
			}
		});
		
	}

}
