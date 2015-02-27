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
	// ¼���� audioRecord
	AudioRecord audioRecord ; 
	// ������ audioTrack
	AudioTrack audioTrack ; 
	boolean doRecordAndPlay = true ; // ��Button ��������Ϊfalse
	
	
	public void start()
	{
		
		// ��getMinBufferSize()�����õ��ɼ���������Ҫ����С�������Ĵ�С
		recBufSize = AudioRecord.getMinBufferSize(frequency,
						channelConfiguration, audioEncoding);
		playBufSize = AudioTrack.getMinBufferSize(frequency,
						channelConfiguration, audioEncoding);
		// ʵ����AudioRecord(������Դ�������ʣ��������ã������������룬�����С��
		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency,
						channelConfiguration, audioEncoding, recBufSize);
		// ʵ���� AudioTrack ��������
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
	
	
	//¼�����Ҳ���
	public void recordAndPlay()
	{
		new  RecordPlayThread().start();
	}
		
		
	class RecordPlayThread extends Thread {
		public void run() {
			try {
				// byte �ļ����洢����
				byte[] buffer = new byte[recBufSize];
				// ��ʼ�ɼ�����
				audioRecord.startRecording();
				// ��������
				audioTrack.play();
				while (doRecordAndPlay) {
					// ��MIC�洢��������
					int bufferReadResult = audioRecord.read(buffer, 0,
								recBufSize);
					byte[] tmpBuf = new byte[bufferReadResult];
					System.arraycopy(buffer, 0, tmpBuf, 0, bufferReadResult);
					// ���Ż�����������
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
