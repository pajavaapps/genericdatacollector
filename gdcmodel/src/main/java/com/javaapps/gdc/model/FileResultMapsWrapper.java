package com.javaapps.gdc.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class FileResultMapsWrapper {

	private static FileResultMapsWrapper fileResultMapWrapper;
	
	private Map<String,FileResultMap>fileResultMaps=new HashMap<String,FileResultMap>();

	public static FileResultMapsWrapper getInstance(){
		if (fileResultMapWrapper == null){
			fileResultMapWrapper =new  FileResultMapsWrapper();
		}
		return fileResultMapWrapper;
	}
	
	private FileResultMapsWrapper()
	{
	}
	
	
	
	public Map<String, FileResultMap> getFileResultMaps() {
		return fileResultMaps;
	}


}


