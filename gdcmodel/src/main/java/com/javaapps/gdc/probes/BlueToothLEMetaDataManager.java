package com.javaapps.gdc.probes;

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
	private static Map<String,BlueToothLEMetaData> blueToothLEMetaDataMap=new HashMap<String,BlueToothLEMetaData>();

	public synchronized static BlueToothLEMetaData get(String sensorName) throws JsonParseException, JsonMappingException, IOException{
		BlueToothLEMetaData blueToothLEMetaData=blueToothLEMetaDataMap.get(sensorName);
		if (blueToothLEMetaData == null){
				InputStream is=BlueToothLEMetaData.class.getResourceAsStream(SENSOR_META_DATA_DIR+sensorName+SUFFIX);
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
