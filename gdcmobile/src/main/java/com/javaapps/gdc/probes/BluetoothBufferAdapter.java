package com.javaapps.gdc.probes;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.javaapps.gdc.model.BluetoothData;
import com.javaapps.gdc.model.GenericData;

public class BluetoothBufferAdapter {
	private long lastUpdateTime=System.currentTimeMillis();
    private Map<String,BluetoothDataBuffer> bufferMap=new HashMap<String,BluetoothDataBuffer>();
	
    public void addDataPoint(String sensorMetaDataFile,String serviceName,long sampleDateInMillis,byte[] data){
    	BluetoothDataBuffer dataBuffer=bufferMap.get(serviceName);
    	if ( dataBuffer == null ){
    		dataBuffer=new BluetoothDataBuffer(sensorMetaDataFile);
    		bufferMap.put(serviceName, dataBuffer);
    	}
    	dataBuffer.addDataPoint(sampleDateInMillis,data);
    }
    
    public List<GenericData> convertToGenericData()
    {
    	 List<GenericData> retList=new  ArrayList<GenericData>();
    	 for (String serviceName:bufferMap.keySet())
    	 {
    		 BluetoothDataBuffer dataBuffer=bufferMap.get(serviceName);
    		 for (DataPoint dataPoint:dataBuffer.dataList)
    		 {	 
    		 retList.add(new BluetoothData(dataBuffer.sensorMetaDataFile,serviceName,dataPoint.sampleDateInMillis,dataPoint.data,dataBuffer.calibration));
    		 }
    	 }
    	 return retList;
    }
    
	class BluetoothDataBuffer
	{
    private String sensorMetaDataFile;
	private List<DataPoint> dataList=new ArrayList<DataPoint>();
	private byte[] calibration;
	
	
	
	public BluetoothDataBuffer(String sensorMetaDataFile) {
		super();
		this.sensorMetaDataFile = sensorMetaDataFile;
	}

	void addDataPoint(long sampleDateInMillis,byte[] data){
		dataList.add(new DataPoint(sampleDateInMillis, data));
	}
	
	void clear(){
		dataList.clear();
	}
	
	}
	
	class DataPoint{
		long sampleDateInMillis;
		byte[] data;
		public DataPoint(long sampleDateInMillis, byte[] data) {
			super();
			this.sampleDateInMillis = sampleDateInMillis;
			this.data = data;
		}		
	}
}
