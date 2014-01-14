package com.javaapps.gdc.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.javaapps.gdc.types.DataType;

public class SystemMonitor {
	
	private Map<DataType, Monitor> monitorMap=new HashMap<DataType, Monitor>();
	
	private static SystemMonitor systemMonitor=new SystemMonitor();
	
	private String archiveFiles;

	private int lastUploadStatusCode;

	private Date lastUploadDate;

	private String lastConnectionError;

	private String wifiStatus;

	private int uploadStatusCode;
	
	public static SystemMonitor getInstance(){
		return systemMonitor;
	}
	
	public Monitor getMonitor(DataType dataType){
		Monitor monitor=monitorMap.get(dataType);
		if ( monitor == null ){
			monitor =new Monitor();
			monitorMap.put(dataType, monitor);
		}
		return monitor;
	}

	public String getArchiveFiles() {
		return archiveFiles;
	}

	public void setArchiveFiles(String archiveFiles) {
		this.archiveFiles = archiveFiles;
	}

	public void setLastUploadStatusCode(int lastUploadStatusCode) {
		this.lastUploadStatusCode=lastUploadStatusCode;
	}

	public int getLastUploadStatusCode() {
		return lastUploadStatusCode;
	}

	public void setLastUploadDate(Date lastUploadDate) {
		this.lastUploadDate=lastUploadDate;
	}

	public Date getLastUploadDate() {
		return lastUploadDate;
	}

	public void setLastConnectionError(String lastConnectionError) {
		this.lastConnectionError=lastConnectionError;
	}

	public String getLastConnectionError() {
		return lastConnectionError;
	}

	public void setWifiStatus(String wifiStatus) {
		this.wifiStatus=wifiStatus;
	}

	public String getWifiStatus() {
		return wifiStatus;
	}

	public void setUploadStatusCode(int uploadStatusCode) {
		this.uploadStatusCode=uploadStatusCode;
	}

	public int getUploadStatusCode() {
		return uploadStatusCode;
	}
	
	

}
