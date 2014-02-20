package com.javaapps.gdc.model;

import java.util.ArrayList;
import java.util.List;

public class DeviceMetaData {

	private final String deviceName;
	
	private final List<DayMetaData> dayDataList;

	public DeviceMetaData(String deviceName, List<DayMetaData> dayDataList) {
		this.deviceName = deviceName;
		this.dayDataList = dayDataList;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public List<DayMetaData> getDayDataList() {
		return dayDataList;
	}

	@Override
	public String toString() {
		return "DeviceMetaData [deviceName=" + deviceName + ", dayDataList="
				+ dayDataList + "]";
	}
	
	
	
}
