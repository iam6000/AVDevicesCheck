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
	boolean doRecordAndPlay = true ; // 由Button 触发更改为false
	
	
	public void start()
	{
		
		// 用getMinBufferSize()方法得到采集数据所需要的最小缓冲区的大小
		recBufSize = AudioRecord.getMinBufferSize(frequency,
						channelConfiguration, audioEncoding);
		playBufSize = AudioTrack.getMinBufferSize(frequency,
						channelConfiguration, audioEncoding);
		// 实例化AudioRecord(声音来源，采样率，声道设置，采样声音编码，缓存大小）
		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency,
						channelConfiguration, audioEncoding, recBufSize);
		// 实例化 AudioTrack 播放声音
		audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, frequency,
						channelConfiguration, audioEncoding, playBufSize,
						AudioTrack.MODE_STREAM);
		
		audioTrack.setStereoVolume(0.7f, 0.7f);	
		
		recordAndPlay();
		
	}
	
	// stop audio
	public void stop(){
		if(doRecordAndPlay)
		{
			doRecordAndPlay = false ;
		}
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
				byte[] buffer = new byte[recBufSize];
				// 开始采集声音
				audioRecord.startRecording();
				// 播放声音
				audioTrack.play();
				while (doRecordAndPlay) {
					// 从MIC存储到缓存区
					int bufferReadResult = audioRecord.read(buffer, 0,
								recBufSize);
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
