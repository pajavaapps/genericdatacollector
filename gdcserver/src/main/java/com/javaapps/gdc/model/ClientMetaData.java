package com.javaapps.gdc.model;

import java.util.ArrayList;
import java.util.List;

public class ClientMetaData {

	private final String normalizedEmail;
	
	private final List<DeviceMetaData>deviceMetaDataList;

	public ClientMetaData(String normalizedEmail,
			List<DeviceMetaData> deviceMetaDataList) {
		super();
		this.normalizedEmail = normalizedEmail;
		this.deviceMetaDataList = deviceMetaDataList;
	}

	@Override
	public String toString() {
		return "ClientMetaData [normalizedEmail=" + normalizedEmail
				+ ", deviceMetaDataList=" + deviceMetaDataList + "]";
	}
	
	
	
}
