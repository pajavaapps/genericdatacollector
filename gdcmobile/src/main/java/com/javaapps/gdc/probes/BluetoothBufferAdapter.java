package com.javaapps.gdc.probes;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.javaapps.gdc.Constants;
import com.javaapps.gdc.exceptions.OperationNotSupportedException;
import com.javaapps.gdc.io.DataBuffer;
import com.javaapps.gdc.model.BluetoothData;
import com.javaapps.gdc.model.GenericData;
import com.javaapps.gdc.pojos.SensorMetaData;

public class BluetoothBufferAdapter {
	private static final int BLUETOOTH_BUFFER_SIZE = 2;
	private long lastUpdateTime = System.currentTimeMillis();
	private static Map<String, BluetoothDataBuffer> bufferMap = new HashMap<String, BluetoothDataBuffer>();
	private SensorMetaData sensorMetaData;
	private String sensorMetaDataFile;

	public BluetoothBufferAdapter(SensorMetaData sensorMetaData,
			String sensorMetaDataFile) {
		this.sensorMetaData = sensorMetaData;
		this.sensorMetaDataFile = sensorMetaDataFile;
	}

	public BluetoothDataBuffer getBluetoothDataBuffer(String serviceName) {
		BluetoothDataBuffer bluetoothDataBuffer = bufferMap.get(serviceName);
		if (bluetoothDataBuffer == null) {
			bluetoothDataBuffer = new BluetoothDataBuffer();
			bufferMap.put(serviceName, bluetoothDataBuffer);
		}
		return bluetoothDataBuffer;
	}

	class BluetoothDataBuffer {
		private DataBuffer dataBuffer;
		private List<DataPoint> dataList = new ArrayList<DataPoint>();
		private byte[] calibration;
		
		BluetoothDataBuffer()
		{
			try {
				dataBuffer=DataBuffer.getInstance(sensorMetaData);
			} catch (Exception ex) {
				Log.e(Constants.GENERIC_COLLECTOR_TAG3,"Unable to create bluetooth data buffer because "+ex.getMessage(),ex);
			}
		}

		void addDataPoint(long sampleDateInMillis, byte[] data) {
			long now = System.currentTimeMillis();
			if ((now - lastUpdateTime) < sensorMetaData.getSamplingPeriod()) {
				return;
			}
			lastUpdateTime=now;
		   dataList.add(new DataPoint(sampleDateInMillis, data));
			if (dataList.size() > BLUETOOTH_BUFFER_SIZE) {
				List<GenericData> genericDataList = convertToGenericData();
				if ( dataBuffer != null)
				{
				dataBuffer.logData(genericDataList);
				Log.i(Constants.GENERIC_COLLECTOR_TAG3,"logged bluetooth data "+genericDataList);
				}else{
					Log.d(Constants.GENERIC_COLLECTOR_TAG3,"Cannot log bluetooth data "+genericDataList+" because databuffer is null");
				}
				dataList.clear();
			}
		}

		public void setCalibration(byte[] calibration) {
			this.calibration = calibration;
		}

		public List<GenericData> convertToGenericData() {
			List<GenericData> retList = new ArrayList<GenericData>();
			for (String serviceName : bufferMap.keySet()) {
				BluetoothDataBuffer dataBuffer = bufferMap.get(serviceName);
				for (DataPoint dataPoint : dataBuffer.dataList) {
					BluetoothData bluetoothData=new BluetoothData(
							BluetoothBufferAdapter.this.sensorMetaDataFile,
							serviceName, dataPoint.sampleDateInMillis,
							dataPoint.data, dataBuffer.calibration);
					bluetoothData.setSensorDescription(sensorMetaData.getDescription());
					bluetoothData.setSensorId(sensorMetaData.getId());
					retList.add(bluetoothData);
				}
			}
			return retList;
		}

	}

	class DataPoint {
		long sampleDateInMillis;
		byte[] data;

		public DataPoint(long sampleDateInMillis, byte[] data) {
			super();
			this.sampleDateInMillis = sampleDateInMillis;
			this.data = data;
		}
	}

}
