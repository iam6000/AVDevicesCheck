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
	// 虚拟机上没有这个路径？？？
	static String path = "/dev/snd/";
	static String pcmFilePath = "record.pcm";
	static boolean doDevicesScan = true ; 
	static boolean isPlaying = true ;
	String CaptureDevice = null; 
	//String PlayDevice = null; 
	Context mContext;
	private  AudioTrack mAudioTrack; 
	private InputStream pcmInputStream = null ;
	int mPlayBuff = 0 ;
	int mSampleBit = 16 ;
	int mAudioChannel = 1; 
	
	
	List<String> lastScanResult = new ArrayList<String>();
	List<String> curScanResult  = new ArrayList<String>();
	List<String> targetScanResult = new ArrayList<String>() ;
	
	//declare JNI native methods
	public native int startAudioRecord(int cardID, int deviceID );	
	public native int stopAudioRecord();	
	
	public TinyAlsaAudio(Context context)
	{
		System.out.println("new TinyAlsaAudio 2222!!!!!!!!!!!!!!!!");	
		mContext = context ;
	}
	
	/*!
	 *  计算声卡id和设备id
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
	 * //一次扫描设备/dev/snd目录下的pcm设备，记录每个pcm设备名称，并更新cur	,随后单独做class， 用于 video设备检测
	 */	
	public void ScanProcess()
	{
		File file = new File(path);
		File[] fileList = file.listFiles();
		if(fileList == null)
		{
			 System.out.println("path error");			
		}
		
		// 若 cur为空，则直接填满
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
		else  // 先clean  然后填满
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
		
		// 判断 cur 与last 确定是否查找到devices 变化
		// 若lastScanReuslt未初始化 则替换last，
		if( lastScanResult.isEmpty() )
		{
			lastScanResult.addAll(curScanResult);
		}
		else if(lastScanResult.size() == curScanResult.size())
		{
			//大小不变，认为list相同，不需要做任何修改
		}
		else //  大小不同， 剔除重复，目标pcm  入 targetlist
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
			
			// 查找目标pcm设备名称
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
			// 跳出循环		
			doDevicesScan = false ;			
		}		
		
	}	
	
	
	/*!
	 *  检测 Device path是都合法
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
	 *   启用 AudioTrack 播放所录制的pcm文件
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
	 *  stop AudioTrack 播放线程
	 */
	private void stopAudioPlay()
	{
		if(isPlaying)
		{
			isPlaying = false ;
		}
		mAudioTrack.stop();
		mAudioTrack.release();
		mAudioTrack = null ;
	}
	
	/*!
	 *  启动 alsa audio，录音并播放
	 * @see com.example.devicechecker.AudioDevice#start()
	 */
	public void start()
	{
		System.out.println("start TinyAlsaAudio 3333!!!!!!!!!!!!!!!!");	
		
		//起一个线程开始扫描设备
		new  devicesScanThread().start();

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("USB设备操作");
		builder.setMessage("请完成一次摄像头拔插操作！\n 若摄像头已插上，请拔掉，再插上\n 若摄像头未插上，请直接插上\n");
		builder.setCancelable(false);
		// button 操作
		builder.setPositiveButton("已完成插拔操作,并已插上摄像头", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub								
				// stop The Scan thread 
				doDevicesScan = false ; 
				System.out.println("Devices Operated Success" );					
			
			}
		});
		
		builder.create().show();
				
		// 检测到设备
		if(CaptureDevice != null){		
			System.out.println("CheckDevices Success,get Pcm devices" + CaptureDevice );			
			// 播放需要使用android层播放，因为，播放pcm设备被android系统长期占用			
			parsePCMID();
			// 开启一个线程 startRecord() ,使用tinyalsa 设备 采集
			new alsaAudioThread().start();
			
			AlertDialog.Builder recordBuilder = new AlertDialog.Builder(mContext);
			recordBuilder.setTitle("检测到设备开始录音");
			recordBuilder.setMessage("已检测到pcm设备"+ CaptureDevice + "\n 请对着mic说话，开始录音\n");
			recordBuilder.setCancelable(false);
			recordBuilder.setPositiveButton("录音结束，播放录音",new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					// 1,先停掉Record线程
					stopAudioRecord();
					// 调用androidTrack播放声音		
					try {						
						startAudioPlay();
					}catch (Exception e){
						 e.printStackTrace();  
					}
					new playThread().start();
				}
			});
			
			
		}		
		
	}
	
	/*!
	 * stop alsa Audio
	 * @see com.example.devicechecker.AudioDevice#stop()
	 */
	public void  stop()
	{
		//先停掉 native层线程
		//stopAudioRecord();
		// 停掉 AudioTrack播放线程
		stopAudioPlay(); 		
		android.os.Process.killProcess(android.os.Process.myPid());
		
	}
	
	/*!
	 *  do devices scan 
	 *	起一个线程一直扫描 path内的设备，扫描模块 后期独立成class 同时供video使用
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
		 * // AlsaAudio采集和播放线程，采集和播放在一个线程中进行，由Native层进行
		 */		
		@SuppressLint("ShowToast")
		class alsaAudioThread extends Thread {
			public void run() {
				try{
					//启动 alsaAudio线程 调用JNI func
					startAudioRecord(CardId,DeviceId);
				}catch(Throwable t){
					Toast.makeText(mContext, t.getMessage(), 1000);
				}
				}
			};
			
		/*!
		 *  调用AudioTrack的播放线程 	,从文件中读取pcm并播放
		 */
		class playThread extends Thread{
			public void run(){
				try{
					
					//  初始化 InputStream，打开pcm文件
					BufferedInputStream bInputStream = null ; 					
					if(pcmInputStream != null)
					{
						 bInputStream = new BufferedInputStream(pcmInputStream);
					}
					else 
					{
						
						return ;
					}
					// 分配空间
					byte buffer[] = new byte[mPlayBuff];
					int readResult = 0 ; // 记录每次read的大小
					while(isPlaying){
						try{
							readResult = bInputStream.read(buffer,0,mPlayBuff);
							System.out.println("read pcm result :" + readResult);
						} catch (IOException e)
						{
							e.printStackTrace();
						}
						// 读到数据之后 写入 AudioTrack，否则跳出循环
						if(readResult != -1)
						{
							mAudioTrack.write(buffer, 0, readResult);
						}else 
						{
							isPlaying = false ; 
						}
						
					}
					
					// 关闭并释放相关资源
					bInputStream.close(); 				
					// audioTrack 单独close				
					
					
				}catch(Throwable t){
					Toast.makeText(mContext, t.getMessage(), 1000);
				}
			}
		};
	

}
