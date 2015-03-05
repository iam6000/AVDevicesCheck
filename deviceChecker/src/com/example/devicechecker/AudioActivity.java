package com.example.devicechecker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

public class AudioActivity extends Activity {
	private Button audioIsOK ; 
	private Button audioIsNotOK;
	boolean isAudioUseAble = false ; 
	private AudioDevice  audio ; 
	//private AndroidAudio androidAudioDevices ; 
	//private AlsaAudio alsaAudioDevices ; 
	
	// AndroidAudio ִ���߼��� ��AudioRecord and AudioTrack  ¼��������
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.audio);
		setTitle("Audio ¼��&���Ų���");
		audioIsOK = (Button)findViewById(R.id.AudioIsOK);
		audioIsNotOK = (Button)findViewById(R.id.AudioIsNotOK);
	
		// ��ȡ intent 
		Intent intent = this.getIntent(); 
		// ��ȡ ��������ݣ����жϳ�ʼ����һ���豸
		Bundle bundle = intent.getExtras();
		String value = bundle.getString("Type");		
		if(value.equals("android"))
		{
			audio = new AndroidAudio(); 
		}	
		else if(value.equals("tinyalsa"))
		{
			System.out.println("new TinyAlsaAudio 1111!!!!!!!!!!!!!!!!");	
		    audio = new TinyAlsaAudio(AudioActivity.this);	// ���� content ��tinyalsa����		
		}
	   
		
		audio.start();
		// ���� ������Ϣ����¼���� ���� ����audio.stop			
		
		audioIsOK.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				System.out.println("Audio is UseAble");
				isAudioUseAble = true ; 
				audio.stop();
				// ��ת��ԭactivity ��������ǰactivity
				Intent intent = new Intent();
				intent.setClass(AudioActivity.this,manualActivity.class);
				startActivity(intent);
				AudioActivity.this.finish();
			}
		});
		
		
		audioIsNotOK.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				System.out.println("Audio is UseNotAble");
				isAudioUseAble = false ; 
				audio.stop();			
				// ��ת��ԭactivity ��������ǰactivity
				Intent intent = new Intent();
				intent.setClass(AudioActivity.this,manualActivity.class);
				startActivity(intent);
				AudioActivity.this.finish();
			}
		});
	}
	
	protected void onDestroy() {
		super.onDestroy();
		// ɱ����ǰ����
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	

}
