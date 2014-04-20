package com.javaapps.gdc.model;

import java.util.Arrays;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnore;

public class BluetoothData extends GenericData {

	private String serviceName;
	private byte data[];
	private byte calibration[];
	private String sensorType;
	@JsonIgnore
	private String sensorId;
	@JsonIgnore
	private String sensorDescription;

	private final static int SAMPLE_DATE_IDX = 0;
	private final static int DATA_IDX = 1;
	private final static int CALIBRATION_IDX = 2;
	private final static int SERVICE_NAME_IDX = 3;
	private final static int SENSOR_ID_IDX = 4;
	private final static int SENSOR_DESCRIPTION_IDX = 5;

	public BluetoothData(String sensorType, String serviceName,
			long sampleDateInMillis, byte[] data, byte calibration[]) {
		this.sensorType = sensorType;
		this.setSampleDateInMillis(sampleDateInMillis);
		this.serviceName = serviceName;
		this.data = data;
		this.calibration = calibration;
	}
	
	public BluetoothData()
	{
		
	}

	public BluetoothData(String csvString) {
		String props[] = csvString.split("\\,");
		if (props.length < 4) {
			return;
		}
		systemDate = new Date();
		sampleDate = new Date(Long.parseLong(props[0]));
		data = props[DATA_IDX].trim().getBytes();
		calibration = props[CALIBRATION_IDX].trim().getBytes();
		if (props.length > 3) {
			this.serviceName= props[SERVICE_NAME_IDX];
		}
		if (props.length > 4) {
			this.sensorId = props[SENSOR_ID_IDX];
		}
		if (props.length > 5) {
			this.sensorDescription = props[SENSOR_DESCRIPTION_IDX];
		}
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
		StringBuilder sb = new StringBuilder(sampleDate.getTime() + ","
				+ adjustByteData(data) + "," + adjustByteData(calibration));
		if (serviceName != null) {
			sb.append("," + serviceName);
			if (sensorId != null) {
				sb.append("," + sensorId);
				if (sensorDescription != null) {
					sb.append("," + sensorDescription);
				}
			}
		}
		sb.append("\n");
		return sb.toString();
	}

	private String adjustByteData(byte[] bytes) {
		if (bytes == null) {
			return " ";
		}
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		boolean allZeros = true;
		for (byte bt : bytes) {
			if (allZeros && bt != 0) {
				allZeros = false;
			}
			if (!first) {
				sb.append("|");
			}
			sb.append(String.valueOf(bt));
			first = false;
		}
		if (allZeros) {
			return "0";
		} else {
			return sb.toString();
		}
	}

	public String getSensorType() {
		return sensorType;
	}

	public void setSensorType(String sensorType) {
		this.sensorType = sensorType;
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

	@Override
	public String toString() {
		return "BluetoothData [serviceName=" + serviceName + ", data="
				+ Arrays.toString(data) + ", calibration="
				+ Arrays.toString(calibration) + ", sensorType=" + sensorType
				+ "]";
	}

}
