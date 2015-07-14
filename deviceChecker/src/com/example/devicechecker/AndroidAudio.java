package com.example.devicechecker;

import com.example.devicechecker.AudioActivity;
import com.example.devicechecker.AudioDevice;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;


public class AndroidAudio extends AudioDevice {
	

	int recBufSize, playBufSize;
	// 录音用 audioRecord
	AudioRecord audioRecord ; 
	// 播放用 audioTrack
	AudioTrack audioTrack ; 
	//boolean doRecordAndPlay = true ; // 由Button 触发更改为false
	
	
	public void start()
	{		
		
		System.out.println("AndoridAudio start !!!!!!!");
		// 用getMinBufferSize()方法得到采集数据所需要的最小缓冲区的大小
		recBufSize = AudioRecord.getMinBufferSize(frequency,
						channelConfiguration, audioEncoding);
		System.out.println("AudioRecord minBufferSize  is " + recBufSize);
		if( recBufSize < 4096 )
		{
			recBufSize = 4096 * 3/2; 
		}
		System.out.println("Real AudioRecord minBufferSize  is " + recBufSize);
		playBufSize = AudioTrack.getMinBufferSize(frequency,
						channelConfiguration, audioEncoding);
		System.out.println("AudioTrack minBufferSize  is " + playBufSize);
		// 实例化AudioRecord(声音来源，采样率，声道设置，采样声音编码，缓存大小）
		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency,
						channelConfiguration, audioEncoding, recBufSize);
		// 实例化 AudioTrack 播放声音
		audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, frequency,
						channelConfiguration, audioEncoding, playBufSize,
						AudioTrack.MODE_STREAM);
		
		//audioTrack.setStereoVolume(0.7f, 0.7f);	
		
		recordAndPlay();
		
	}
	
	// stop audio
	public void stop(){
		if(doRecordAndPlay)
		{
			doRecordAndPlay = false ;
		}
		if(audioRecord != null)
		{
			audioRecord.release() ; 
			audioRecord = null ; 
		}
		if(audioTrack != null)
		{
			audioTrack.release() ; 
			audioTrack = null ;
		}
		
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	// 检测 AudioRecord 
	public DeviceErrorMsg checkAudioRecord()
	{
		DeviceErrorMsg  errorInfo = new DeviceErrorMsg() ;
		recBufSize = AudioRecord.getMinBufferSize(frequency,
				channelConfiguration, 3);
		
		try{
			// 实例化AudioRecord(声音来源，采样率，声道设置，采样声音编码，缓存大小）
			audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency,
							channelConfiguration, audioEncoding, recBufSize);
		}catch(Exception excaption){
			errorInfo.setErrorMSG(excaption.getMessage(), "AudioRecord", -1);
		}
		
		if(audioRecord != null)
		{
			if(audioRecord.getState() ==  AudioRecord.STATE_UNINITIALIZED)
			{
				errorInfo.setErrorMSG("AudioRecord STATE_UNINITIALIZED ", "AudioRecord", 0);
			}
			else
			{
				errorInfo.setErrorMSG("Android Record 录音设备 is OK", "AudioRecord", 0);
			}
				
			
						
			audioRecord.release() ;
			audioRecord = null ;
		}
		
		return errorInfo ; 
	}
	
	// 检测 AudioTrack 
	public DeviceErrorMsg checkAudioTrack()
	{
		DeviceErrorMsg  errorInfo = new DeviceErrorMsg() ;
		playBufSize = AudioTrack.getMinBufferSize(frequency,
				channelConfiguration, audioEncoding);
		
		try{
			// 实例化AudioRecord(声音来源，采样率，声道设置，采样声音编码，缓存大小）
			// 实例化 AudioTrack 播放声音
			audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, frequency,
							channelConfiguration, audioEncoding, playBufSize,
							AudioTrack.MODE_STREAM);
			audioTrack.setStereoVolume(0.7f, 0.7f);	
		}catch(Exception excaption){
			errorInfo.setErrorMSG(excaption.getMessage(), "audioTrack", -1);
		}
		
		if(audioTrack != null)
		{
			errorInfo.setErrorMSG("Android Track 播放设备 is OK", "audioTrack", 0);
			audioTrack.release() ;
			audioTrack = null ;
		}
		
		return errorInfo ; 
	}
	
	//录音并且播放
	public void recordAndPlay()
	{
		new  RecordPlayThread().start();
	}
		
		
	class RecordPlayThread extends Thread {
		public void run() {
			try {
				// byte 文件来存储声音
				//byte[] buffer = new byte[recBufSize];
				byte[] buffer = new byte[320];
				// 开始采集声音
				audioRecord.startRecording();
				// 播放声音
				audioTrack.play();
				while (doRecordAndPlay) {
					// 从MIC存储到缓存区
//					int bufferReadResult = audioRecord.read(buffer, 0,
//								recBufSize);
					int bufferReadResult = audioRecord.read(buffer, 0,
							320);
					byte[] tmpBuf = new byte[bufferReadResult];
					System.arraycopy(buffer, 0, tmpBuf, 0, bufferReadResult);
					// 播放缓存区的数据
					audioTrack.write(tmpBuf, 0, tmpBuf.length);
					}
					audioTrack.stop();
					audioRecord.stop();
				} catch (Throwable t) {
					//Toast.makeText(AndroidAudioActivity.this, t.getMessage(), 1000);
				}
			}
		};
		

}
