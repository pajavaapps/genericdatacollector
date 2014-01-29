package com.javaapps.gdc.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.javaapps.gdc.dao.GenericDataDAO;
import com.javaapps.gdc.model.GPS;
import com.javaapps.gdc.model.GenericDataUpload;
import com.javaapps.gdc.model.GenericWrapper;
import com.javaapps.gdc.model.GenericData;
import com.javaapps.gdc.types.DataType;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/unittest.xml" })
public class GenericDataDAOTest {

	@Resource
	private GenericDataDAO genericDataDAO;
	private final static String TEST_DEVICE_ID="123";
	private static final int TEST_VERSION = 0;
	@Before
	public void setup(){
		try
		{
		genericDataDAO.delete(TEST_DEVICE_ID);
		genericDataDAO.deleteAll();
		}catch(Exception ex){
			fail("GenericDataDAOTest failed because "+ex.getMessage());
		}
	}
	
	@After 
	public void tearDown(){
	}
	
	@Test
	public void testSaveObject()
	{
		try
		{
		GenericDataUpload genericDataUpload=createTestUploadData();
		genericDataDAO.saveGenericData(genericDataUpload);
		List<GenericData> genericDataList=genericDataDAO.get(DataType.GPS,TEST_DEVICE_ID,new Date(),GenericDataDAO.DATE_GRANULARITY);
		assertNotNull(genericDataList);
		assertTrue(genericDataList.size()>0);
		for (GenericData genericData:genericDataList)
		{
		System.out.println(genericData);
		}
		genericDataDAO.delete(TEST_DEVICE_ID);
		genericDataUpload.setCustomIdentifier(null);
		genericDataDAO.saveGenericData(genericDataUpload);
		genericDataList=genericDataDAO.get(DataType.GPS,TEST_DEVICE_ID,new Date(),GenericDataDAO.DATE_GRANULARITY);
		assertNotNull(genericDataList);
		assertTrue(genericDataList.size()>0);
		genericDataDAO.delete(TEST_DEVICE_ID);
	/*	genericDataUpload.setCustomIdentifier("");
		genericDataDAO.saveGenericData(genericDataUpload);
		genericDataList=genericDataDAO.get(TEST_CUSTOM_IDENTIFIER,new Date(),GenericDataDAO.DATE_GRANULARITY);
		assertNotNull(genericDataList);
		assertTrue(genericDataList.size()>0);*/
		}catch(Exception ex){
			fail("testSave failed because "+ex.getMessage());
		}
	}
	
	
	@Test public void testGetDeviceIds()
	{
		try
		{
		GenericDataUpload genericDataUpload=createTestUploadData();
		genericDataDAO.saveGenericData(genericDataUpload);
		List<GenericWrapper>retList=genericDataDAO.getDeviceIds();
		assertTrue(retList.size() > 0);
		System.out.println(retList);
	}catch(Exception ex){
		fail("testGetDeviceIds failed because "+ex.getMessage());
	}
	}

		@Test public void testGetDates()
		{
			try
			{
			GenericDataUpload genericDataUpload=createTestUploadData();
			genericDataDAO.saveGenericData(genericDataUpload);
			List<GenericWrapper>retList=genericDataDAO.getDates(DataType.GPS,TEST_DEVICE_ID);
			assertTrue(retList.size() > 0);
			System.out.println(retList);
		}catch(Exception ex){
			fail("testGetDates failed because "+ex.getMessage());
		}
	}
	
	
	@Test
	public void testSaveString()
	{
		try
		{
		GenericDataUpload genericDataUpload=createTestUploadData();
		ObjectMapper objectMapper=new ObjectMapper();
		String jsonStr=objectMapper.writeValueAsString(genericDataUpload);
		genericDataDAO.saveGenericData(DataType.GPS,jsonStr);
		List<GenericData> genericDataList=genericDataDAO.get(DataType.GPS,TEST_DEVICE_ID,new Date(),GenericDataDAO.DATE_GRANULARITY);
		assertNotNull(genericDataList);
		assertTrue(genericDataList.size()>0);
		for (GenericData genericData:genericDataList)
		{
		System.out.println(genericData);
		}
		}catch(Exception ex){
			fail("testSave failed because "+ex.getMessage());
		}
	}
	
	private GenericDataUpload createTestUploadData(){
		List<GenericData> genericDataList=new ArrayList<GenericData>();
		for ( int ii=0;ii<100;ii++)
		{
			double latitude=40.0+Math.random()*0.0001;
			double longitude=-80.0+Math.random()*0.0001;
			GenericData genericData=new GenericData();
			float speed=40.0f;
			long sampleDateTime=System.currentTimeMillis();
			double altitude=20.0;
			float bearing=180.0f;
			GPS gps=new GPS(latitude,  longitude, speed,
					bearing,  altitude,  sampleDateTime);
			genericDataList.add(gps);
		}
		return new GenericDataUpload(DataType.GPS,TEST_DEVICE_ID,new Date(),genericDataList);
	}
	
}
