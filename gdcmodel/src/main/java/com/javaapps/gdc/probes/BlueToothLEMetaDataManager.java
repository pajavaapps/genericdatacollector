package com.javaapps.gdc.probes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class BlueToothLEMetaDataManager {
	
	private static final String SENSOR_META_DATA_DIR = "/sensorMetaData/";
	private static final String SUFFIX = ".json";
	private static final String SENSOR_ABSOLUTE_PATH="/tmp"+SENSOR_META_DATA_DIR;
	private static Map<String,BlueToothLEMetaData> blueToothLEMetaDataMap=new HashMap<String,BlueToothLEMetaData>();
 
	public synchronized static void remove(String sensorName){
		blueToothLEMetaDataMap.remove(sensorName);
	}

	
	public synchronized static BlueToothLEMetaData get(String sensorName) throws JsonParseException, JsonMappingException, IOException{
		BlueToothLEMetaData blueToothLEMetaData=blueToothLEMetaDataMap.get(sensorName);
		if (blueToothLEMetaData == null){
			    String fileName=sensorName+SUFFIX;
			    File file=new File(SENSOR_ABSOLUTE_PATH,fileName);
			    InputStream is=null;
			    if ( file.exists()){
			    	is=new FileInputStream(file);
			    }else{
						is=BlueToothLEMetaData.class.getResourceAsStream(SENSOR_META_DATA_DIR+fileName);
			    }
			if ( is != null){
				Scanner scanner=new Scanner(is);
				StringBuilder sb=new StringBuilder();
				while ( scanner.hasNext()){
					sb.append(scanner.next());
				}
				blueToothLEMetaData=convertToObject(sb.toString());
				blueToothLEMetaDataMap.put(sensorName,blueToothLEMetaData);
			}
		}
		return blueToothLEMetaData;
	}

	public synchronized static BlueToothLEMetaData convertToObject(String jsonStr) throws JsonParseException, JsonMappingException, IOException{
		ObjectMapper objectMapper=new ObjectMapper();
		BlueToothLEMetaData blueToothLEMetaData=objectMapper.readValue(jsonStr.getBytes(), BlueToothLEMetaData.class);
		return  blueToothLEMetaData;
	}
	
}
