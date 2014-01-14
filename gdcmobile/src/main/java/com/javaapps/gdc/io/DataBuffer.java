package com.javaapps.gdc.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.javaapps.gdc.io.DataFile;
import com.javaapps.gdc.Constants;
import com.javaapps.gdc.model.Config;
import com.javaapps.gdc.model.GenericData;
import com.javaapps.gdc.model.SystemMonitor;
import com.javaapps.gdc.types.DataType;
import com.javaapps.gdc.utils.DataCollectorUtils;

public class DataBuffer {

	private static DataBuffer dataBuffer;

	private DataType dataType;

	private List<GenericData> dataList = new ArrayList<GenericData>();

	private DataFile dataFile;

	// private List<GForceData> shortTermGForceDataList = new
	// ArrayList<GForceData>();
	private final static float VARIANCE = 0.2f;

	private GenericData lastGenericData = null;

	public static DataBuffer getInstance() throws FileNotFoundException,
			IOException {
		if (dataBuffer == null) {
			dataBuffer = new DataBuffer();
		}
		return dataBuffer;
	}

	private DataBuffer() throws FileNotFoundException, IOException {
		Log.i(Constants.GENERIC_COLLECTOR_TAG, "opening gforce internal file");
		dataFile = new DataFile(dataType, Config.getInstance()
				.getDataFileExtension());
		Log.i(Constants.GENERIC_COLLECTOR_TAG, "opened gforce internal file");
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
		long currentTime = System.currentTimeMillis();
		dataList.add(lastGenericData);
		lastGenericData = null;
		SystemMonitor.getInstance().getMonitor(dataType)
				.incrementTotalPointsLogged(1);
		SystemMonitor.getInstance().getMonitor(dataType)
				.setPointsInBuffer(dataList.size());
		dataFile.updateTimeStamp();
		if (dataList.size() > Config.getInstance()
				.getGforceListenerBufferSize()) {
			flushBuffer();
		}
	}

}
