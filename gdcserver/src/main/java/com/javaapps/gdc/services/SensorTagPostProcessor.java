package com.javaapps.gdc.services;

import java.util.ArrayList;
import java.util.List;

import com.javaapps.gdc.model.GenericData;
import com.javaapps.gdc.model.GenericDataUpload;
import com.javaapps.gdc.model.PostProcessedBluetoothData;

public class SensorTagPostProcessor extends PostProcessor {
	
	/* (non-Javadoc)
	 * @see com.javaapps.gdc.services.PostProcessor#postProcess(com.javaapps.gdc.model.GenericData)
	 */
	@Override
	public GenericData convertToPostProcessedData(
			GenericData genericData) {
		PostProcessedBluetoothData processedData=new PostProcessedBluetoothData();
		return processedData;
	}

}
