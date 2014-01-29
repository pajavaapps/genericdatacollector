package com.javaapps.gdc.pojos;
public class DeviceMetaData {
	
	private String email;
	private String deviceId;
	private String customIdentifier="mydevicename";
	private String dataEndpoint;
	private int uploadBatchSize=100;
	

	public final static String DEFAULT_DATA_ENDPOINT="http://genericdataserver.pajavaapps.cloudbees.net/gdcserver";
	
	public DeviceMetaData(String email, String deviceId,
			String customIdentifier,String dataEndpoint) {
		this.email = email;
		this.deviceId = deviceId;
		this.customIdentifier = customIdentifier;
		this.dataEndpoint=dataEndpoint;
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

	@Override
	public String toString() {
		return "DeviceMetaData [email=" + email + ", deviceId=" + deviceId
				+ ", customIdentifier=" + customIdentifier + ", dataEndpoint="
				+ dataEndpoint + ", uploadBatchSize=" + uploadBatchSize + "]";
	}

	
	
	
	

}
