package com.javaapps.gdc.services;

import java.util.ArrayList;
import java.util.List;

import com.javaapps.gdc.model.GenericData;
import com.javaapps.gdc.model.GenericDataUpload;

public abstract class PostProcessor {

	public void postProcess(GenericDataUpload genericDataUpload)
	{
		List<GenericData>processedList=new ArrayList<GenericData>();
		 for (GenericData genericData:genericDataUpload.getGenericDataList()){
			 processedList.add(convertToPostProcessedData(genericData));
		 }
		 genericDataUpload.setGenericDataList(processedList);
	}
	
	public abstract GenericData convertToPostProcessedData(GenericData genericData);

}