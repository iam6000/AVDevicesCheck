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
	
	private Button bt_AndroidAudio ; 
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
		bt_AndroidAudio = (Button)findViewById(R.id.AndroidAudio);
		bt_AlsaAudio = (Button)findViewById(R.id.linuxAlsa);
	}
	
	// ��Ը���ģ�飬�Ƿ�̫�����鷳��
	private void ManualViewListener()
	{
		
		// ��������
		bt_AndroidAudio.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				System.out.println("bt_android_audio test !!!!!!!!!!!!!!!!");			
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
		
		//��������
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
