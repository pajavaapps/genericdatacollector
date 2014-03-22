package com.javaapps.gdc.model;

import java.util.Arrays;
import java.util.Date;

public class BluetoothData extends GenericData {

	private String serviceName;
	private byte data[];
	private byte calibration[];
	private String sensorType;

	public BluetoothData(String sensorType,String serviceName, long sampleDateInMillis,
			byte[] data, byte calibration[]) {
		this.sensorType=sensorType;
		this.setSampleDateInMillis(sampleDateInMillis);
		this.serviceName = serviceName;
		this.data = data;
		this.calibration = calibration;
	}

	public BluetoothData(String csvString) {
		String props[] = csvString.split("\\,");
		if (props.length < 3) {
			return;
		}
		systemDate = new Date();
		sampleDate = new Date(Long.parseLong(props[0]));
		data = props[1].getBytes();
		calibration = props[2].getBytes();
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public byte[] getCalibration() {
		return calibration;
	}

	public void setCalibration(byte[] calibration) {
		this.calibration = calibration;
	}

	@Override
	public String toCSV() {
		return sampleDate.getTime() + "," + data + "," + calibration + "\n";
	}

	
	
	public String getSensorType() {
		return sensorType;
	}

	public void setSensorType(String sensorType) {
		this.sensorType = sensorType;
	}

	@Override
	public String toString() {
		return "BluetoothData [serviceName=" + serviceName + ", data="
				+ Arrays.toString(data) + ", calibration="
				+ Arrays.toString(calibration) + ", sensorType=" + sensorType
				+ "]";
	}

	
}
