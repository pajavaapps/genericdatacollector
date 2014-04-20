package com.javaapps.gdc.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.javaapps.gdc.types.DataType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GenericDataStore implements Serializable {
	private static final long serialVersionUID = 1L;
	private Pattern commaPattern = Pattern.compile("\\,");
	private Pattern lineFeedPattern = Pattern.compile("\n");

	private Date uploadDate;

	private String dataBlob;

	private String deviceId;

	private int version;

	private String customIdentifier;

	public GenericDataStore() {

	}

	public GenericDataStore(GenericDataUpload dataUpload) {
		this.uploadDate = dataUpload.getUploadDate();
		this.customIdentifier= dataUpload.getCustomIdentifier();
		this.version= dataUpload.getVersion();
		this.dataBlob = convertListToBlob(dataUpload
				.getGenericDataList());
		this.deviceId = dataUpload.getDeviceId();
	}

	private String convertListToBlob(List<GenericData> generidDataList) {
		StringBuilder sb = new StringBuilder();
		for (GenericData genericData : generidDataList) {
			sb.append(genericData.toCSV());
		}
		return sb.toString();
	}

	public List<GenericData> hydrateGenericDataBlob(DataType dataType) {
		List<GenericData> retList = new ArrayList<GenericData>();
		if (dataBlob == null) {
			return retList;
		}
		for (String dataStr : lineFeedPattern.split(this.dataBlob)) {
			try {
				if ( dataStr.trim().length() == 0)
				{
					continue;
				}
				GenericData genericData =null;
	                if ( dataType == DataType.GPS)
	                { 	
					genericData = new GPS(dataStr);
	                }else if ( dataType==DataType.GFORCE){
	                	genericData = new GForce(dataStr);
	                }else if ( dataType == DataType.BLUETOOTH_DATA){
	                	genericData=new PostProcessedBluetoothData(dataStr);
	                }
					retList.add(genericData);
			} catch (Exception ex) {
				// no logging for now, just try to get all the good points
			}
		}
		return retList;
	}

	public Date getUploadDate() {
		return uploadDate;
	}

	public String getDataBlob() {
		return dataBlob;
	}

	public void setDataBlob(String dataBlob) {
		this.dataBlob = dataBlob;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
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
