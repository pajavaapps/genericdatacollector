package com.javaapps.gdc.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.javaapps.gdc.pojos.Config;
import com.javaapps.gdc.aggregators.Aggregator;
import com.javaapps.gdc.exceptions.OperationNotSupportedException;
import com.javaapps.gdc.factories.AggregatorFactory;
import com.javaapps.gdc.io.DataFile;
import com.javaapps.gdc.Constants;
import com.javaapps.gdc.model.GenericData;
import com.javaapps.gdc.model.SystemMonitor;
import com.javaapps.gdc.pojos.SensorMetaData;
import com.javaapps.gdc.types.AggregationType;
import com.javaapps.gdc.types.DataType;
import com.javaapps.gdc.utils.DataCollectorUtils;

public class DataBuffer {

	private static Map<String,DataBuffer> dataBufferMap=new HashMap<String,DataBuffer>();
	
	private SensorMetaData sensorMetaData;
	
	private List<GenericData> dataList = new ArrayList<GenericData>();

	private DataFile dataFile;
	
	private Aggregator aggregator;


	private final static float VARIANCE = 0.2f;

	public synchronized static DataBuffer getInstance(SensorMetaData sensorMetaData) throws FileNotFoundException,
			IOException, OperationNotSupportedException {
		DataBuffer dataBuffer=dataBufferMap.get(sensorMetaData.getId());
		if (dataBuffer == null) {
			dataBuffer = new DataBuffer(sensorMetaData);
			dataBuffer.aggregator=AggregatorFactory.createAggregator(sensorMetaData);
			dataBufferMap.put(sensorMetaData.getId(), dataBuffer);
		}
		return dataBuffer;
	}

	private DataBuffer(SensorMetaData sensorMetaData) throws FileNotFoundException, IOException {
		this.sensorMetaData=sensorMetaData;
		Log.i(Constants.GENERIC_COLLECTOR_TAG, "opening internal file for "+sensorMetaData);
		dataFile = new DataFile(sensorMetaData, Config.getInstance()
				.getDataFileExtension());
		Log.i(Constants.GENERIC_COLLECTOR_TAG, "opened  internal file for "+sensorMetaData);
	}

	public void flushBuffer() {
		try {
			if (dataList.size() > 0) {

				dataList = dataFile.writeToObjectFile(dataList);
				Log.i(Constants.GENERIC_COLLECTOR_TAG, "Saved " + dataList.size()
						+ " gforce points to buffer");
			}
		} catch (IOException e) {
			Log.e(Constants.GENERIC_COLLECTOR_TAG,
					"Unable to save gforce buffer because "
							+ DataCollectorUtils.getStackTrackElement(e));
		}
	}

	public void logData(GenericData genericData) {
		genericData=aggregator.getAggregatedValue(genericData);
		if ( genericData == null){
			return;
		}
		long currentTime = System.currentTimeMillis();
		dataList.add(genericData);
		SystemMonitor.getInstance().getMonitor(sensorMetaData.getId())
				.incrementTotalPointsLogged(1);
		SystemMonitor.getInstance().getMonitor(sensorMetaData.getId())
				.setPointsInBuffer(dataList.size());
		dataFile.updateTimeStamp();
		Log.d(Constants.GENERIC_COLLECTOR_TAG,"logging data "+genericData+"\n buffer size "+dataList.size());
		if (dataList.size() > 10) {//TODO setup config
			Log.i(Constants.GENERIC_COLLECTOR_TAG,"flushing buffer for "+sensorMetaData);
			flushBuffer();
		}
	}

}
