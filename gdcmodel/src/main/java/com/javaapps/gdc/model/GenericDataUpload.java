package com.javaapps.gdc.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.javaapps.gdc.types.DataType;


public class GenericDataUpload implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private  Date uploadDate;

	private  List<GenericData>genericDataList =new ArrayList<GenericData>();

	private String deviceId;
	
	private String customIdentifier;
	
	private int version;
	
	private DataType dataType;
	
	private String sensorId;
	
	private String sensorDescription;
	
	@JsonIgnore
	private String dailyCollectionName;
	
	public GenericDataUpload() {
	}

	public GenericDataUpload(DataType dataType,String sensorId, String sensorDescription,String deviceId,Date uploadDate,
			List<GenericData> genericDataList) {
		this.sensorId=sensorId;
		this.sensorDescription=sensorDescription;
		this.uploadDate = uploadDate;
		this.genericDataList= genericDataList;
		this.deviceId=deviceId;
		this.dataType=dataType;
	}

	
	public Date getUploadDate() {
		return uploadDate;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	
	public List<GenericData> getGenericDataList() {
		return genericDataList;
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


	public DataType getDataType() {
		return dataType;
	}


	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}

	public void setGenericDataList(List<GenericData> genericDataList) {
		this.genericDataList = genericDataList;
	}

	public String getSensorId() {
		return sensorId;
	}

	public void setSensorId(String sensorId) {
		this.sensorId = sensorId;
	}

	public String getSensorDescription() {
		return sensorDescription;
	}

	public void setSensorDescription(String sensorDescription) {
		this.sensorDescription = sensorDescription;
	}

	public String getDailyCollectionName() {
		return dailyCollectionName;
	}

	public void setDailyCollectionName(String dailyCollectionName) {
		this.dailyCollectionName = dailyCollectionName;
	}
	
	
	
}
