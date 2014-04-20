package com.javaapps.gdc.pojos;

import com.javaapps.gdc.utils.DataCollectorUtils;

public class DeviceMetaData {

	private String email;
	private String deviceId;
	private String customIdentifier = "mydevicename";
	private String dataEndpoint;
	private int uploadBatchSize = 100;
	
	public final static String DEFAULT_DATA_ENDPOINT = "http://192.168.1.3:8080/gdcserver";
	//public final static String DEFAULT_DATA_ENDPOINT = "http://genericdataserver.pajavaapps.cloudbees.net/gdcserver";

	public DeviceMetaData(String email, String deviceId,
			String customIdentifier, String dataEndpoint) {
		this.email = email;
		this.deviceId = deviceId;
		this.customIdentifier = customIdentifier;
		this.dataEndpoint = dataEndpoint;
	}

	public DeviceMetaData() {
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String company) {
		this.email = company;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getCustomIdentifier() {
		return customIdentifier;
	}

	public void setCustomIdentifier(String customIdentifier) {
		this.customIdentifier = customIdentifier;
	}

	public int getUploadBatchSize() {
		return uploadBatchSize;
	}

	public void setUploadBatchSize(int uploadBatchSize) {
		this.uploadBatchSize = uploadBatchSize;
	}

	public String getDataEndpoint() {
		return dataEndpoint;
	}

	public void setDataEndpoint(String dataEndpoint) {
		this.dataEndpoint = dataEndpoint;
	}

	public String getNormalizedEmail() {
		String retStr = "notset";
		if (email != null) {
			retStr =DataCollectorUtils.getNormalizedString(email);
		}
		return retStr;
	}

	@Override
	public String toString() {
		return "DeviceMetaData [email=" + email + ", deviceId=" + deviceId
				+ ", customIdentifier=" + customIdentifier + ", dataEndpoint="
				+ dataEndpoint + ", uploadBatchSize=" + uploadBatchSize + "]";
	}

}
