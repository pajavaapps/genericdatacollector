package com.javaapps.gdc.pojos;
public class DeviceMetaData {
	
	private String company;
	private String deviceId;
	private String customIdentifier;
	
	
	
	public DeviceMetaData(String company, String deviceId,
			String customIdentifier) {
		this.company = company;
		this.deviceId = deviceId;
		this.customIdentifier = customIdentifier;
	}
	
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
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

	@Override
	public String toString() {
		return "DeviceMetaData [company=" + company + ", deviceId=" + deviceId
				+ ", customIdentifier=" + customIdentifier + "]";
	}
	
	

}
