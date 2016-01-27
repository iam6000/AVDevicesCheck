package com.example.devicechecker;

import java.util.ArrayList;
import java.util.List; 
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.example.devicechecker.AndroidAudio.RecordPlayThread;

import android.R.string;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

@SuppressLint("ShowToast")
public class TinyAlsaAudio extends AudioDevice {	
	
	static int CardId = -1 ; 
	static int DeviceId = -1 ; 	
	// �������û�����·��������
	static String path = "/dev/snd/";
	static String pcmFilePath = "record.pcm";
	static boolean doDevicesScan = true ; 
	static boolean isPlaying = true ;
	String CaptureDevice = null; 
	
	DeviceErrorMsg mDeviceMsg  = new DeviceErrorMsg(); 
	//String PlayDevice = null; 
	Context mContext;
	private  AudioTrack mAudioTrack; 
	private InputStream pcmInputStream = null ;
	int mPlayBuff = 0 ;
	int mSampleRate = 8000 ;
	int mSampleBit = 16 ;
	int mAudioChannel = 1; 
	int mPeriodSize = 160 ;
	int mperiodCount = 4; 
	
	
	List<String> lastScanResult = new ArrayList<String>();
	List<String> curScanResult  = new ArrayList<String>();
	List<String> targetScanResult = new ArrayList<String>();
	
	//declare JNI native methods
	//int cardID, int deviceID,int jChannels , int JRate, int Jperiod_size, int Jperiod_count ,char* errorMSG
	public native DeviceErrorMsg checkDeviceAvailable(int CardID, int deviceID , int JChannels, int  JRate , int  JperiodSize,int JperiodCount);
	public native void startAudioRecord(int cardID, int deviceID , int JChannels, int  JRate , int  JperiodSize,int JperiodCount );
	public native void stopAudioRecord();		
	public native void doAndroidAudioRecord();
	public native int doFormatChange(String path , String newPath ,String pcmFile);
	
	static {
	      System.loadLibrary("TinyAlsaDevice");
	      System.loadLibrary("ffmpeg_jni");
	}
	
	public TinyAlsaAudio(Context context)
	{
		System.out.println("new TinyAlsaAudio 2222!!!!!!!!!!!!!!!!");	
		mContext = context ;
	}
	
	/*!
	 *  ������id���豸id
	 */
	private void parsePCMID(){
		if(CaptureDevice == null)
		{
			System.out.println("get Card and Device ID error\n");	
			return  ;
		}
		System.out.println("get PCM name" + CaptureDevice);	
		// PCMCXDYc, get X, get Y 
		CardId = Integer.parseInt(String.valueOf(CaptureDevice.charAt(4)));
		DeviceId = Integer.parseInt(String.valueOf(CaptureDevice.charAt(6)));		
		System.out.println("get Card "+ CardId + "  and Device " + DeviceId);
		
	}
	
	/*!
	 * //һ��ɨ���豸/dev/sndĿ¼�µ�pcm�豸����¼ÿ��pcm�豸��ƣ�������cur	,��󵥶���class�� ���� video�豸���
	 */	
	public void ScanProcess()
	{
		File file = new File(path);
		File[] fileList = file.listFiles();
		if(fileList == null)
		{
			 System.out.println("path error");			
		}
		
		// �� curΪ�գ���ֱ������
		if(curScanResult.isEmpty())
		{
			for(int i = 0 ; i < fileList.length; i++)
			{
				if(fileList[i].getName().startsWith("pcm"))
				{
					curScanResult.add(fileList[i].getName());
				}
				
			}
		}
		else  // ��clean  Ȼ������
		{
			curScanResult.clear();
			for(int i = 0 ; i < fileList.length; i++)
			{
				if(fileList[i].getName().startsWith("pcm"))
				{
					curScanResult.add(fileList[i].getName());
				}
			}
		}
		
		// �ж� cur ��last ȷ���Ƿ���ҵ�devices �仯
		// ��lastScanReusltδ��ʼ�� ���滻last��
		if( lastScanResult.isEmpty() )
		{
			lastScanResult.addAll(curScanResult);
		}
		else if(lastScanResult.size() == curScanResult.size())
		{
			//��С���䣬��Ϊlist��ͬ������Ҫ���κ��޸�
		}
		else //  ��С��ͬ�� �޳��ظ���Ŀ��pcm  �� targetlist
		{
			if(lastScanResult.size() > curScanResult.size())
			{
				lastScanResult.removeAll(curScanResult);
				targetScanResult.addAll(lastScanResult);
			}
			else
			{
				curScanResult.removeAll(lastScanResult);
				targetScanResult.addAll(curScanResult);
			}
			
			// ����Ŀ��pcm�豸���
			for(int i = 0 ; i < targetScanResult.size(); ++i)
			{
				System.out.println("get pcm devices " + i +"\t" + targetScanResult.get(i));
				// name pcmCxDxc is capture devices, pcmCxDxp is play devices
				if(targetScanResult.get(i).endsWith("c"))
				{
					CaptureDevice = targetScanResult.get(i);
					System.out.println("Get CapuureDevice is" + CaptureDevice);
					continue ;
				}
				if(targetScanResult.get(i).endsWith("p"))
				{
					//PlayDevice = targetScanResult.get(i);
					continue ;
				}
				//System.out.println("Get CapuureDevice is" + CaptureDevice);
				//System.out.println("Get PlayDevices is " + PlayDevice);
			}				
			// ���ѭ��		
			doDevicesScan = false ;			
		}		
		
	}	
	
	
	/*!
	 *  ��� Device path�Ƕ��Ϸ�
	 */
	public boolean checkDevicePath()
	{
		File file = new File(path);
		File[] fileList = file.listFiles();
		if(fileList == null)
		{
			 System.out.println("path error");		
			 return false ;
		}
		return true ;
	}
	
	/*!
	 *   ���� AudioTrack ������¼�Ƶ�pcm�ļ�
	 */
	private Boolean startAudioPlay() throws Exception
	{
		
		File pcmFile = new File(pcmFilePath);
		
		if(!pcmFile.exists())
		{
			System.out.println("no pcm file exists!!");	
			return false ;
		}
		
		try {
			pcmInputStream = new FileInputStream(pcmFile);
		}catch (FileNotFoundException e){			
			 System.out.println("no pcm file exists!!");	
			 e.printStackTrace();
		}
		
		
		mPlayBuff = AudioTrack.getMinBufferSize(frequency, mAudioChannel, mSampleBit);
		
		mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, frequency,
						channelConfiguration, audioEncoding, mPlayBuff,
						AudioTrack.MODE_STREAM);	
		
		mAudioTrack.setStereoVolume(0.7f, 0.7f);			
	
		mAudioTrack.play();		
		
		return true ;
		
	}
	
	/*!
	 *  stop AudioTrack �����߳�
	 */
	private void stopAudioPlay()
	{
		if(isPlaying)
		{
			isPlaying = false ;
		}
		if(mAudioTrack != null)
		{
			mAudioTrack.stop();
			mAudioTrack.release();
			mAudioTrack = null ;
		}		
	}
	
	/*!
	 *   ����豸�����ʹ��native�������豸��¼��
	 */
	private void startRecordAndPlay()
	{			
		// ��⵽�豸
		if(CaptureDevice != null){	
			System.out.println("CheckDevices Success" );	
			System.out.println("CheckDevices Success,get Pcm devices" + CaptureDevice );			
			// ������Ҫʹ��android�㲥�ţ���Ϊ������pcm�豸��androidϵͳ����ռ��			
			parsePCMID();		
			
			// �ȼ�� �豸Ȩ�ޣ���ûȨ�� ֱ�ӷ��� 
			
			mDeviceMsg  = checkDeviceAvailable(CardId,DeviceId, mAudioChannel,mSampleRate,mPeriodSize,mperiodCount);
			System.out.println("Begin  doFormatChange " );	
			doFormatChange("/skydir/test.amr" , "/skydir/test.aac","/skydir/test.pcm");
			System.out.println("After   doFormatChange " );	
			
			if(mDeviceMsg.isErrorHappen())
			{
				// do Error ���� ����return  
				System.out.println("get Error result :" + mDeviceMsg.result );	
				System.out.println("get Error MSG :" + mDeviceMsg.ErrorMsg );	
				AlertDialog.Builder availableBuilder = new AlertDialog.Builder(mContext);				
				availableBuilder.setTitle("��Ƶ�ɼ��豸����");
				availableBuilder.setMessage(mDeviceMsg.ErrorMsg);
				availableBuilder.setNegativeButton("����", null);
				availableBuilder.create().show();
				
			}
			else 
			{		
				// ����һ���߳� startRecord() ,ʹ��tinyalsa �豸 �ɼ�
				new alsaAudioThread().start();			
				AlertDialog.Builder recordBuilder = new AlertDialog.Builder(mContext);
				recordBuilder.setTitle("��⵽�豸��ʼ¼��");
				recordBuilder.setMessage("�Ѽ�⵽pcm�豸"+ CaptureDevice + "\n �����mic˵������ʼ¼��\n");
				recordBuilder.setCancelable(false);
				recordBuilder.setPositiveButton("¼��������¼��",new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						// 1,��ͣ��Record�߳�
						stopAudioRecord();
						// ����androidTrack��������		
						try {						
							startAudioPlay();
						}catch (Exception e){
							 e.printStackTrace();  
						}
						new playThread().start();
					}
				});						
				recordBuilder.create().show();
				
			}			

		}
		else 
		{
			System.out.println("CheckDevices failed" );	
			System.out.println("Begin  doFormatChange " );	
			doFormatChange("/skydir/test.amr","/skydir/success.aac" , "/skydir/test.pcm");
			System.out.println("After   doFormatChange ");	
			AlertDialog.Builder noPcmDevicesBuilder = new AlertDialog.Builder(mContext);
			noPcmDevicesBuilder.setTitle("mic not found");
			noPcmDevicesBuilder.setMessage("无法找到mic");
			noPcmDevicesBuilder.setNegativeButton("确定", null);
			noPcmDevicesBuilder.create().show();
		}		
	}
	
	/*!
	 *  ���� alsa audio��¼��������
	 * @see com.example.devicechecker.AudioDevice#start()
	 */
	public void start()
	{
		System.out.println("start TinyAlsaAudio 3333!!!!!!!!!!!!!!!!");	
		
		System.out.println("start TinyAlsaAudio 3333!!!!!!!!!!!!!!!!");	
		
		//��һ���߳̿�ʼɨ���豸
		new  devicesScanThread().start();

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("USB�豸����");
		builder.setMessage("�����һ������ͷ�β������\n ������ͷ�Ѳ��ϣ���ε����ٲ���\n ������ͷδ���ϣ���ֱ�Ӳ���\n");
		builder.setCancelable(false);
		// button ����
		builder.setPositiveButton("����ɲ�β���,���Ѳ�������ͷ", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub								
				// stop The Scan thread 
				doDevicesScan = false ; 
				System.out.println("Devices Operated Success" );
				startRecordAndPlay();									
			}
		});		
		builder.create().show();	
			
		// doAndroidAudioRecord();		
		
	}
	
	/*!
	 * stop alsa Audio
	 * @see com.example.devicechecker.AudioDevice#stop()
	 */
	public void  stop()
	{
		//��ͣ�� native���߳�
		stopAudioRecord();
		// ͣ�� AudioTrack�����߳�
		//stopAudioPlay(); 		
		android.os.Process.killProcess(android.os.Process.myPid());
		
	}
		
	/*!
	 *  do devices scan 
	 *	��һ���߳�һֱɨ�� path�ڵ��豸��ɨ��ģ�� ���ڶ�����class ͬʱ��videoʹ��
	 */
	
	class devicesScanThread extends Thread {
		public void run() {
			try {				 
				if(!checkDevicePath())
				{
					doDevicesScan = false ;
				}
				//begin scan until all get two different pcmdevice list 
				while (doDevicesScan) {							
						ScanProcess();
					}					
				} catch (Throwable t) {
					Toast.makeText(mContext, t.getMessage(), 1000);
				}
			}
		};
	
		
		/*!
		 * // AlsaAudio�ɼ��Ͳ����̣߳��ɼ��Ͳ�����һ���߳��н��У���Native�����
		 */		
		@SuppressLint("ShowToast")
		class alsaAudioThread extends Thread {
			public void run() {
				try{
					//���� alsaAudio�߳� ����JNI func
					startAudioRecord(CardId,DeviceId,mAudioChannel,mSampleRate,mPeriodSize,mperiodCount);						
				}catch(Throwable t){
					Toast.makeText(mContext, t.getMessage(), 1000);
				}
				}
			};
			
		/*!
		 *  ����AudioTrack�Ĳ����߳� 	,���ļ��ж�ȡpcm������
		 */
		class playThread extends Thread{
			public void run(){
				try{
					
					//  ��ʼ�� InputStream����pcm�ļ�
					BufferedInputStream bInputStream = null ; 					
					if(pcmInputStream != null)
					{
						 bInputStream = new BufferedInputStream(pcmInputStream);
					}
					else 
					{
						
						return ;
					}
					// ����ռ�
					byte buffer[] = new byte[mPlayBuff];
					int readResult = 0 ; // ��¼ÿ��read�Ĵ�С
					while(isPlaying){
						try{
							readResult = bInputStream.read(buffer,0,mPlayBuff);
							System.out.println("read pcm result :" + readResult);
						} catch (IOException e)
						{
							e.printStackTrace();
						}
						// �������֮�� д�� AudioTrack���������ѭ��
						if(readResult != -1)
						{
							mAudioTrack.write(buffer, 0, readResult);
						}else 
						{
							isPlaying = false ; 
						}
						
					}
					
					// �رղ��ͷ������Դ
					bInputStream.close(); 				
					// audioTrack ����close				
					
					
				}catch(Throwable t){
					Toast.makeText(mContext, t.getMessage(), 1000);
				}
			}
		};
	

}
