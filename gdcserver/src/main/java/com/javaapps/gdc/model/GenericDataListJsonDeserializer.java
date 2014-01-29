package com.javaapps.gdc.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.type.TypeReference;

public class GenericDataListJsonDeserializer  extends JsonDeserializer<List<GenericData>>
{
	 final TypeReference<List<GenericData>> listType = new TypeReference<List<GenericData>>() {};
		
	@Override
	public List<GenericData> deserialize(JsonParser jsonParser,
			DeserializationContext context) throws IOException,
			JsonProcessingException {
		List<GenericData> genericDataList=new ArrayList<GenericData>();
		return genericDataList;
	}


}