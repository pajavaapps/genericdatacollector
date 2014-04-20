package com.javaapps.gdc.services;

import com.javaapps.gdc.model.GenericData;
import com.javaapps.gdc.model.GenericDataUpload;

public class DefaultPostProcessor extends PostProcessor {
	
	/* (non-Javadoc)
	 * @see com.javaapps.gdc.services.PostProcessor#postProcess(com.javaapps.gdc.model.GenericData)
	 */
	@Override
	public void postProcess(GenericDataUpload genericDataUpload)
	{
	}

	@Override
	public GenericData convertToPostProcessedData(GenericData genericData) {
		return genericData;
	}

}
