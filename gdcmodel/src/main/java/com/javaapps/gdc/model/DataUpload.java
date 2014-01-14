package com.javaapps.gdc.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class DataUpload implements Serializable {
	private static final long serialVersionUID = 1L;

	private final Date date;

	private final List<GenericData> dataList ;

	private String deviceId;
	
	private String customIdentifier;
	
	private int version;
	
	
	public DataUpload(String deviceId,Date date,
			List<GenericData> dataList) {
		this.date = date;
		this.dataList = dataList;
		this.deviceId=deviceId;
	}

	
	
	public Date getDate() {
		return date;
	}



	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public List<GenericData> getDataList() {
		return dataList;
	}
	
	

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	
	public String getCustomIdentifier() {
		return customIdentifier;
	}

	public void setCustomIdentifier(String customIdentifier) {
		this.customIdentifier = customIdentifier;
	}



}
