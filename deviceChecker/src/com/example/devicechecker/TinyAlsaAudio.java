package com.example.devicechecker;

import java.util.ArrayList;
import java.util.List; 
import java.io.File;

import com.example.devicechecker.AndroidAudio.RecordPlayThread;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

public class TinyAlsaAudio extends AudioDevice {
	
	
	static int CardId = -1 ; 
	static int DeviceId = -1 ; 	
	// �������û�����·��������
	static String path = "/dev/snd/";
	static boolean doDevicesScan = true ; 
	String CaptureDevice = null; 
	String PlayDevice = null; 
	Context mContext;
	
	
	List<String> lastScanResult = new ArrayList<String>();
	List<String> curScanResult  = new ArrayList<String>();
	List<String> targetScanResult = new ArrayList<String>() ;
	
	public TinyAlsaAudio(Context context)
	{
		System.out.println("new TinyAlsaAudio 2222!!!!!!!!!!!!!!!!");	
		mContext = context ;
	}
	
	//һ��ɨ���豸/dev/sndĿ¼�µ�pcm�豸����¼ÿ��pcm�豸���ƣ�������cur	
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
			if(lastScanResult.size() > curScanResult.size()){
				lastScanResult.removeAll(curScanResult);
				targetScanResult.addAll(lastScanResult);
			}
			else
			{
				curScanResult.removeAll(lastScanResult);
				targetScanResult.addAll(curScanResult);
			}
			
			// ����Ŀ��pcm�豸����
			for(int i = 0 ; i < targetScanResult.size(); ++i){
				System.out.println("get pcm devices " + i +"\t" + targetScanResult.get(i));
				// name pcmCxDxc is capture devices, pcmCxDxp is play devices
				if(targetScanResult.get(i).endsWith("c"))
				{
					CaptureDevice = targetScanResult.get(i);
					continue ;
				}
				if(targetScanResult.get(i).endsWith("p"))
				{
					PlayDevice = targetScanResult.get(i);
					continue ;
				}
				System.out.println("Get CapuureDevice is" + CaptureDevice);
				System.out.println("Get PlayDevices is " + PlayDevice);
			}				
			// ����ѭ��		
			doDevicesScan = false ;			
		}		
		
	}	
	
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
	
	// start ���� 
	public void start()
	{
		System.out.println("start TinyAlsaAudio 3333!!!!!!!!!!!!!!!!");	
		
		//��һ���߳̿�ʼɨ���豸
		new  devicesScanThread().start();

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("USB�豸����");
		builder.setMessage("�����һ������ͷ�β������\n ������ͷ�Ѳ��ϣ���ε����ٲ���\n ������ͷδ���ϣ���ֱ�Ӳ���\n");
		builder.setCancelable(false);
		builder.setPositiveButton("����ɲ�β���,���Ѳ�������ͷ", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub								
				// stop The Scan thread 
				doDevicesScan = false ; 
				System.out.println("CheckDevices Success" );					
			
			}
		});
		
		builder.create().show();
				
		// ��⵽�豸
		if(CaptureDevice != null){
			
			// ����jni��ӿڴ��豸 �����Ųɼ���������
			// ������Ҫʹ��android�㲥�ţ���Ϊ������pcm�豸��androidϵͳ����ռ��
		}		
		
	}
	
	// do devices scan 
	// ��һ���߳�һֱɨ�� path�ڵ��豸��ɨ��ģ�� ���ڶ�����class ͬʱ��videoʹ��
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
	
	

}
