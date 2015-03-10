package com.example.devicechecker;

public class DeviceErrorMsg {
	public String DeviceType ; 
	public String ErrorMsg ;
	public int result ; 
	
	public DeviceErrorMsg()
	{
		this.result = 0 ; // 0 is OK , -1 is Error
		this.DeviceType = ""; 
		this.ErrorMsg = "";
	}
	
	public boolean isErrorHappen()
	{
		if(this.result == 0)
		{
			return false ; 
		}
		else 
		{
			return true ;
		}
	}
	
	public String getErrorMSG()
	{			
		return this.ErrorMsg ;
	}
		
}
