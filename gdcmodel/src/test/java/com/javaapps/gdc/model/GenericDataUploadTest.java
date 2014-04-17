package com.javaapps.gdc.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import com.javaapps.gdc.types.DataType;

public class GenericDataUploadTest {

	@Test
	public void deserializationTest() {
		try
		{
	    Date now=new Date();
		GForce gforce = new GForce(1.0f, 2.0f, 3.0f, 10l);
		gforce.setSystemDate(now);
		GenericDataUpload genericDataUpload=new GenericDataUpload(DataType.GFORCE, "1","desc","deviceid", now, new ArrayList<GenericData>());
		genericDataUpload.getGenericDataList().add(gforce);
		genericDataUpload.setVersion(1);
		ObjectMapper objectMapper=new ObjectMapper();
		objectMapper.setPropertyNamingStrategy(  
		        new CamelCaseNamingStrategy()); 
		String jsonStr=objectMapper.writeValueAsString(genericDataUpload);
		System.out.println(jsonStr);
		assertEquals(1,genericDataUpload.getGenericDataList().size());
		GenericDataUpload retDataUpload = objectMapper.readValue(
				jsonStr.getBytes(), GenericDataUpload.class);
		assertEquals(1,retDataUpload.getGenericDataList().size());
		GForce retGforce=(GForce)retDataUpload.getGenericDataList().get(0);
		assertEquals(1.0f,retGforce.getX(),0.001);
		assertEquals(2.0f,retGforce.getY(),0.001);
		assertEquals(3.0f,retGforce.getZ(),0.001);
		assertEquals(10l,retGforce.getSampleDateInMillis());
		
		GPS gps=new GPS (40.0, 80.0, 10.0f,
				180.0f, 10.0, 20);
		genericDataUpload.getGenericDataList().clear();
		genericDataUpload.getGenericDataList().add(gps);
		jsonStr=objectMapper.writeValueAsString(genericDataUpload);
		System.out.println(jsonStr);
		retDataUpload = objectMapper.readValue(
				jsonStr.getBytes(), GenericDataUpload.class);
		assertEquals(1,retDataUpload.getGenericDataList().size());
		GPS retGPS=(GPS)retDataUpload.getGenericDataList().get(0);
		assertEquals(40.0,retGPS.getLatitude(),0.001);
		assertEquals(80.0,retGPS.getLongitude(),0.001);
		assertEquals(10.f,retGPS.getSpeed(),0.001);
		assertEquals(180.0f,retGPS.getBearing(),0.001);
		assertEquals(10.0,retGPS.getAltitude(),0.001);
		assertEquals(20l,retGPS.getSampleDateInMillis());
		}catch(Exception ex){
			ex.printStackTrace();
			fail("test failed because "+ex.getMessage());
		}
	}

}
