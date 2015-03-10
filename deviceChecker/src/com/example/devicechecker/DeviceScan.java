package com.example.devicechecker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.Toast;

public class DeviceScan {
	
	Context mContext;
	static private String pcmDevicePath = "/dev/snd/";
	static private String videoDevicePath = "/dev/";	
	
	static private String pcmDeviceName ; 
	static private String videoDeviceName ; 
	
	public DeviceErrorMsg pcmErrorMsg ; 
	public DeviceErrorMsg videoErrorMsg ;
	int mCardID = -1 ; 
	int mDeviceID = -1 ; 
	
	// pcm �豸 list ����� 
	List<String> lastPcmScanResult = new ArrayList<String>();
	List<String> curPcmScanResult  = new ArrayList<String>();
	List<String> targetPcmScanResult = new ArrayList<String>();
	
	// video �豸list  �����
	List<String> lastVideoScanResult = new ArrayList<String>();
	List<String> curVideoScanResult  = new ArrayList<String>();
	List<String> targetVideoScanResult = new ArrayList<String>();
	
	
	static private boolean doPcmDevicesScan = true ; 
	
	static private boolean doVideoDevicesScan = true ;
	
	// jni func
	public native DeviceErrorMsg checkDeviceAvailable(int CardID, int deviceID);
	public native DeviceErrorMsg checkCameraWithBase(int videoId , int baseId) ; 
	
	static {
	      System.loadLibrary("TinyAlsaDevice");
	      System.loadLibrary("v4l2Device");
	}
	
	
	
	public DeviceScan(Context context) {
		// TODO Auto-generated constructor stub		
		mContext = context;
	}
			
	
	// pcm Devices Scan thread func
	private  void scanPcmDevices()
	{
		File file = new File(pcmDevicePath);
		File[] fileList = file.listFiles();
		if(fileList == null)
		{
			 System.out.println("path error");			
		}
		
		// �� curΪ�գ���ֱ������
		if(curPcmScanResult.isEmpty())
		{
			for(int i = 0 ; i < fileList.length; i++)
			{
				if(fileList[i].getName().startsWith("pcm"))
				{
					curPcmScanResult.add(fileList[i].getName());
				}
				
			}
		}
		else  // ��clean  Ȼ������
		{
			curPcmScanResult.clear();
			for(int i = 0 ; i < fileList.length; i++)
			{
				if(fileList[i].getName().startsWith("pcm"))
				{
					curPcmScanResult.add(fileList[i].getName());
				}
			}
		}
		
		// �ж� cur ��last ȷ���Ƿ���ҵ�devices �仯
		// ��lastScanReusltδ��ʼ�� ���滻last��
		if( lastPcmScanResult.isEmpty() )
		{
			lastPcmScanResult.addAll(curPcmScanResult);
		}
		else if(lastPcmScanResult.size() == curPcmScanResult.size())
		{
			//��С���䣬��Ϊlist��ͬ������Ҫ���κ��޸�
		}
		else //  ��С��ͬ�� �޳��ظ���Ŀ��pcm  �� targetlist
		{
			if(lastPcmScanResult.size() > curPcmScanResult.size())
			{
				lastPcmScanResult.removeAll(curPcmScanResult);
				targetPcmScanResult.addAll(lastPcmScanResult);
			}
			else
			{
				curPcmScanResult.removeAll(lastPcmScanResult);
				targetPcmScanResult.addAll(curPcmScanResult);
			}
			
			// ����Ŀ��pcm�豸����
			for(int i = 0 ; i < targetPcmScanResult.size(); ++i)
			{
				System.out.println("get pcm devices " + i +"\t" + targetPcmScanResult.get(i));
				// name pcmCxDxc is capture devices, pcmCxDxp is play devices
				if(targetPcmScanResult.get(i).endsWith("c"))
				{
					pcmDeviceName = targetPcmScanResult.get(i);
					System.out.println("Get CapuureDevice is" + pcmDeviceName);
					continue ;
				}
				if(targetPcmScanResult.get(i).endsWith("p"))
				{
					//PlayDevice = targetScanResult.get(i);
					continue ;
				}
				//System.out.println("Get CapuureDevice is" + CaptureDevice);
				//System.out.println("Get PlayDevices is " + PlayDevice);
			}				
			// ����ѭ��		
			doPcmDevicesScan = false ;			
		}					
	}
	
	// video Scan thread func	
	private void scanVideoDevices()
	{

		File file = new File(videoDevicePath);
		File[] fileList = file.listFiles();
		if(fileList == null)
		{
			 System.out.println("path error");			
		}
		
		// �� curΪ�գ���ֱ������
		if(curVideoScanResult.isEmpty())
		{
			for(int i = 0 ; i < fileList.length; i++)
			{
				if(fileList[i].getName().startsWith("video"))
				{
					curVideoScanResult.add(fileList[i].getName());
				}
				
			}
		}
		else  // ��clean  Ȼ������
		{
			curVideoScanResult.clear();
			for(int i = 0 ; i < fileList.length; i++)
			{
				if(fileList[i].getName().startsWith("video"))
				{
					curVideoScanResult.add(fileList[i].getName());
				}
			}
		}
		
		// �ж� cur ��last ȷ���Ƿ���ҵ�devices �仯
		// ��lastScanReusltδ��ʼ�� ���滻last��
		if( lastVideoScanResult.isEmpty() )
		{
			lastVideoScanResult.addAll(curVideoScanResult);
		}
		else if(lastVideoScanResult.size() == curVideoScanResult.size())
		{
			//��С���䣬��Ϊlist��ͬ������Ҫ���κ��޸�
		}
		else //  ��С��ͬ�� �޳��ظ���Ŀ��video  �� targetlist
		{
			if(lastVideoScanResult.size() > curVideoScanResult.size())
			{
				lastVideoScanResult.removeAll(curVideoScanResult);
				targetVideoScanResult.addAll(lastVideoScanResult);
			}
			else
			{
				curVideoScanResult.removeAll(lastVideoScanResult);
				targetVideoScanResult.addAll(curVideoScanResult);
			}
			
			// ����Ŀ��video�豸����
			for(int i = 0 ; i < targetVideoScanResult.size(); ++i)
			{
				System.out.println("get video devices " + i +"\t" + targetVideoScanResult.get(i));			
				videoDeviceName = targetVideoScanResult.get(i);
				System.out.println("Get video is" + videoDeviceName);
				continue ;				
			}				
			// ����ѭ��		
			doVideoDevicesScan = false ;			
		}			
	}
	
	public String getPcmDevices()
	{
		return pcmDeviceName ; 
	}
	
	public String getVideoDevices()
	{
		return videoDeviceName ;
	}
	
	public void setdoPcmDevicesScan(boolean value)
	{
		doPcmDevicesScan = value ;
	}
	
	public void setdoVideoDevicesScan(boolean value)
	{
		doVideoDevicesScan = value ;
	}


	
	public void beginScan()
	{
		System.out.println("start beginScan ,Start beginScan both devices");		
		// ����pcm����߳�
		new pcmDevicesScanThread().start();
		// ���� video����߳�
		new videoDevicesScanThread().start();	
		
	}
	
	public void beginScanPcm()
	{
		System.out.println("start beginScanPcm ,Start Scan pcm devices");	
		new pcmDevicesScanThread().start(); 
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("USB�豸����");
		builder.setMessage("�����һ������ͷ�β������\n ������ͷ�Ѳ��ϣ���ε����ٲ���\n ������ͷδ���ϣ���ֱ�Ӳ���\n");
		builder.setCancelable(false);
		builder.setPositiveButton("����ɲ�β���,���Ѳ�������ͷ", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub								
				// stop The Scan thread 
				doPcmDevicesScan = false ; 			
				System.out.println("Devices Operated Success" );											
			}
		});		
		builder.create().show();		
	}
	
	public void beginScanVideo()
	{
		System.out.println("start beginScanVideo ,Start Scan video devices");	
		new videoDevicesScanThread().start(); 
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("USB�豸����");
		builder.setMessage("�����һ������ͷ�β������\n ������ͷ�Ѳ��ϣ���ε����ٲ���\n ������ͷδ���ϣ���ֱ�Ӳ���\n");
		builder.setCancelable(false);
		builder.setPositiveButton("����ɲ�β���,���Ѳ�������ͷ", new OnClickListener() {			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub								
				// stop The Scan thread 
				doVideoDevicesScan = false ; 			
				System.out.println("Devices Operated Success" );											
			}
		});		
		builder.create().show();
				
	}
	
	// ����pcm��ϸ�豸��
	private Boolean parsePCMID(String pcmName){
		if(pcmName == null)
		{
			System.out.println("get Card and Device ID error\n");				
			return  false ;
		}
		System.out.println("get PCM name" + pcmName);	
		// PCMCXDYc, get X, get Y 
		mCardID = Integer.parseInt(String.valueOf(pcmName.charAt(4)));
		mDeviceID = Integer.parseInt(String.valueOf(pcmName.charAt(6)));		
		System.out.println("get Card "+ mCardID + "  and Device " + mDeviceID);
		return true ;
		
	}
	
	
	// ���pcm�豸����
	public DeviceErrorMsg checkPCMPermission(String pcmDevicesName)
	{
		System.out.println("start checkPCMPermission 2");	
		// check both pcm devices and video devices permission
		// ����tinyalsa ��ص� jni��������ȡPCM�豸Ȩ��
		String pcmIsOK = pcmDevicesName + "is OK";
		if(pcmDevicesName == null)
		{
			return null ;
		}
				
		if(parsePCMID(pcmDevicesName) == false )
		{
			return null ;
		}
		System.out.println("cardID is " + mCardID + "deviceID is "  + mDeviceID);	
		// use cardID and deviceID do PermissionCheck
		pcmErrorMsg  = checkDeviceAvailable(mCardID,mDeviceID);		
	    
		return pcmErrorMsg;		
	}
			
	
	// ���video�豸Ȩ��
	public DeviceErrorMsg checkVideoPermission(String videoDeviceName)
	{		
		System.out.println("start checkVideoPermission 2");	
		// ���� v4l2 ���jni��������ȡVideo�豸Ȩ��
		if(videoDeviceName == null )
		{
			return null ;
		}
		int videoId = Integer.parseInt(String.valueOf(videoDeviceName.charAt(5)));
		int baseId = 0 ; 
		
		videoErrorMsg = checkCameraWithBase(videoId, baseId);
		
		return videoErrorMsg ;
		
	}
	
	/*!
	 *  ��� Device path�Ƕ��Ϸ�
	 */
	public boolean checkDevicePath(String path)
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
	
	// �����߳��࣡��һ���̼߳�� pcm�豸��һ���̼߳��video�豸
	class pcmDevicesScanThread extends Thread {
		public void run() {
			try {				 
				if(!checkDevicePath(pcmDevicePath))
				{
					doPcmDevicesScan = false ;
				}
				//begin scan until all get two different pcmdevice list 
				while (doPcmDevicesScan) {							
						scanPcmDevices();
					}					
				} catch (Throwable t) {
					Toast.makeText(mContext, t.getMessage(), 1000);
				}
			}
		};
		
		
	class videoDevicesScanThread extends Thread {
		public void run() {
			try {				 
				if(!checkDevicePath(videoDevicePath))
				{
					doVideoDevicesScan = false ;
				}
				//begin scan until all get two different pcmdevice list 
				while (doVideoDevicesScan) {							
						scanVideoDevices();
					}					
				} catch (Throwable t) {
					Toast.makeText(mContext, t.getMessage(), 1000);
				}
			}
		};

}
