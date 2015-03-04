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
	
	// AndroidAudio 执行逻辑， 打开AudioRecord and AudioTrack  录音并播放
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.audio);
		setTitle("Audio 录音&播放测试");
		audioIsOK = (Button)findViewById(R.id.AudioIsOK);
		audioIsNotOK = (Button)findViewById(R.id.AudioIsNotOK);
	
		// 获取 intent 
		Intent intent = this.getIntent(); 
		// 获取 传入的数据，并判断初始化哪一种设备
		Bundle bundle = intent.getExtras();
		String value = bundle.getString("Type");		
		if(value.equals("android"))
		{
			audio = new AndroidAudio(); 
		}	
		else if(value.equals("tinyalsa"))
		{
			System.out.println("new TinyAlsaAudio 1111!!!!!!!!!!!!!!!!");	
		    audio = new TinyAlsaAudio(AudioActivity.this);	// 传递 content 到tinyalsa的类		
		}
	   
		
		audio.start();
		// 监听 按键信息，记录按键 并且 调用audio.stop			
		
		audioIsOK.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				System.out.println("Audio is UseAble");
				isAudioUseAble = true ; 
				audio.stop();
				// 跳转回原activity 并结束当前activity
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
				// 跳转回原activity 并结束当前activity
				Intent intent = new Intent();
				intent.setClass(AudioActivity.this,manualActivity.class);
				startActivity(intent);
				AudioActivity.this.finish();
			}
		});
	}
	
	protected void onDestroy() {
		super.onDestroy();
		// 杀死当前进程
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	

}
