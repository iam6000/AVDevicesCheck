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
	//boolean doRecordAndPlay = true ; // ��Button ��������Ϊfalse
	
	
	public void start()
	{		
		
		System.out.println("AndoridAudio start !!!!!!!");
		// ��getMinBufferSize()�����õ��ɼ���������Ҫ����С�������Ĵ�С
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
		// ʵ����AudioRecord(������Դ�������ʣ��������ã������������룬�����С��
		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency,
						channelConfiguration, audioEncoding, recBufSize);
		// ʵ���� AudioTrack ��������
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
	
	// ��� AudioRecord 
	public DeviceErrorMsg checkAudioRecord()
	{
		DeviceErrorMsg  errorInfo = new DeviceErrorMsg() ;
		recBufSize = AudioRecord.getMinBufferSize(frequency,
				channelConfiguration, 3);
		
		try{
			// ʵ����AudioRecord(������Դ�������ʣ��������ã������������룬�����С��
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
				errorInfo.setErrorMSG("Android Record ¼���豸 is OK", "AudioRecord", 0);
			}
				
			
						
			audioRecord.release() ;
			audioRecord = null ;
		}
		
		return errorInfo ; 
	}
	
	// ��� AudioTrack 
	public DeviceErrorMsg checkAudioTrack()
	{
		DeviceErrorMsg  errorInfo = new DeviceErrorMsg() ;
		playBufSize = AudioTrack.getMinBufferSize(frequency,
				channelConfiguration, audioEncoding);
		
		try{
			// ʵ����AudioRecord(������Դ�������ʣ��������ã������������룬�����С��
			// ʵ���� AudioTrack ��������
			audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, frequency,
							channelConfiguration, audioEncoding, playBufSize,
							AudioTrack.MODE_STREAM);
			audioTrack.setStereoVolume(0.7f, 0.7f);	
		}catch(Exception excaption){
			errorInfo.setErrorMSG(excaption.getMessage(), "audioTrack", -1);
		}
		
		if(audioTrack != null)
		{
			errorInfo.setErrorMSG("Android Track �����豸 is OK", "audioTrack", 0);
			audioTrack.release() ;
			audioTrack = null ;
		}
		
		return errorInfo ; 
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
				//byte[] buffer = new byte[recBufSize];
				byte[] buffer = new byte[320];
				// ��ʼ�ɼ�����
				audioRecord.startRecording();
				// ��������
				audioTrack.play();
				while (doRecordAndPlay) {
					// ��MIC�洢��������
//					int bufferReadResult = audioRecord.read(buffer, 0,
//								recBufSize);
					int bufferReadResult = audioRecord.read(buffer, 0,
							320);
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
