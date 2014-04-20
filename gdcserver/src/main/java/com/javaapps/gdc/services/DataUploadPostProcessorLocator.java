package com.javaapps.gdc.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

public class DataUploadPostProcessorLocator {
	private Map<String,PostProcessor> dataUploadPostProcessorMap=new HashMap<String,PostProcessor>();
    private PostProcessor defaultPostProcessor=new DefaultPostProcessor();
	
		
	public PostProcessor getPostProcessor(String sensorName){
		PostProcessor postProcessor=dataUploadPostProcessorMap.get(sensorName);
		if (postProcessor == null){
			return defaultPostProcessor;
		}else{
			return postProcessor;
		}
	}


	public void setDataUploadPostProcessorMap(
			Map<String, PostProcessor> dataUploadPostProcessorMap) {
		this.dataUploadPostProcessorMap = dataUploadPostProcessorMap;
	}


	public void setDefaultPostProcessor(PostProcessor defaultPostProcessor) {
		this.defaultPostProcessor = defaultPostProcessor;
	}
	
	

}
