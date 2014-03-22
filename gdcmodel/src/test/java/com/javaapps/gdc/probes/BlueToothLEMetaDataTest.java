package com.javaapps.gdc.probes;

import static org.junit.Assert.*;

import java.util.UUID;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

public class BlueToothLEMetaDataTest {
	
	@Before
	public void setup()
	{
		
	}
	
	@Test
	public void buildTest()
	{
		try
		{
		BlueToothLEMetaData.Builder metaDataBuilder=new BlueToothLEMetaData.Builder();
		metaDataBuilder.addCharacteristic("service1","00000000-0000-0000-0000-000000001234",
				"char1","00000000-0000-0000-0000-000000012345","00000000-0000-0000-0000-000000012346",1,2,false);
		metaDataBuilder.addCharacteristic("service1", "00000000-0000-0000-0000-000000001234",
				"char2","00000000-0000-0000-0000-000000022345","00000000-0000-0000-0000-000000022346",3,4,false);
		metaDataBuilder.addCharacteristic("service2", "00000000-0000-0000-0000-000000002234",
				"char1","00000000-0000-0000-0000-000000032345","00000000-0000-0000-0000-000000033346",5,6,false);
		BlueToothLEMetaData blueToothLEMetaData=metaDataBuilder.build();
		BlueToothLEMetaDataFacade facade=new BlueToothLEMetaDataFacade(blueToothLEMetaData);
        assertEquals("00000000-0000-0000-0000-000000001234",facade.getBlueToothLEServiceUUID("service1").toString());
        assertEquals("00000000-0000-0000-0000-000000002234",facade.getBlueToothLEServiceUUID("service2").toString());
        BlueToothLECharacteristic char1=facade.getBlueToothLECharacteristic("service1", "char1");
        assertEquals("00000000-0000-0000-0000-000000012345",char1.getCharacteristicUUID().toString());
        assertEquals("00000000-0000-0000-0000-000000012346",char1.getEnableCharacteristicUUID().toString());
        assertEquals(1,char1.getEnableCharacteristicValue());
        assertEquals(2,char1.getDisableCharacteristicValue());
        BlueToothLECharacteristic char2=facade.getBlueToothLECharacteristic("service1", "char2");
        assertEquals("00000000-0000-0000-0000-000000022345",char2.getCharacteristicUUID().toString());
        assertEquals("00000000-0000-0000-0000-000000022346",char2.getEnableCharacteristicUUID().toString());
        assertEquals(3,char2.getEnableCharacteristicValue());
        assertEquals(4,char2.getDisableCharacteristicValue());
        BlueToothLECharacteristic char3=facade.getBlueToothLECharacteristic("service2", "char1");
        assertEquals("00000000-0000-0000-0000-000000032345",char3.getCharacteristicUUID().toString());
        assertEquals("00000000-0000-0000-0000-000000033346",char3.getEnableCharacteristicUUID().toString());
        assertEquals(5,char3.getEnableCharacteristicValue());
        assertEquals(6,char3.getDisableCharacteristicValue());
        
        ObjectMapper objectMapper=new ObjectMapper();
        String jsonStr=objectMapper.writeValueAsString(blueToothLEMetaData);
        blueToothLEMetaData=objectMapper.readValue(jsonStr.getBytes(), BlueToothLEMetaData.class);
        assertNotNull( blueToothLEMetaData);
        facade=new BlueToothLEMetaDataFacade(blueToothLEMetaData);
        assertEquals("00000000-0000-0000-0000-000000001234",facade.getBlueToothLEServiceUUID("service1").toString());
        assertEquals("00000000-0000-0000-0000-000000002234",facade.getBlueToothLEServiceUUID("service2").toString());
        char1=facade.getBlueToothLECharacteristic("service1", "char1");
        assertEquals("00000000-0000-0000-0000-000000012345",char1.getCharacteristicUUID().toString());
        assertEquals("00000000-0000-0000-0000-000000012346",char1.getEnableCharacteristicUUID().toString());
        assertEquals(1,char1.getEnableCharacteristicValue());
        assertEquals(2,char1.getDisableCharacteristicValue());
        char2=facade.getBlueToothLECharacteristic("service1", "char2");
        assertEquals("00000000-0000-0000-0000-000000022345",char2.getCharacteristicUUID().toString());
        assertEquals("00000000-0000-0000-0000-000000022346",char2.getEnableCharacteristicUUID().toString());
        assertEquals(3,char2.getEnableCharacteristicValue());
        assertEquals(4,char2.getDisableCharacteristicValue());
        char3=facade.getBlueToothLECharacteristic("service2", "char1");
        assertEquals("00000000-0000-0000-0000-000000032345",char3.getCharacteristicUUID().toString());
        assertEquals("00000000-0000-0000-0000-000000033346",char3.getEnableCharacteristicUUID().toString());
        assertEquals(5,char3.getEnableCharacteristicValue());
        assertEquals(6,char3.getDisableCharacteristicValue());
        System.out.println(jsonStr);
 		}catch(Exception ex){
			fail("buildTest failed because "+ex.getMessage());
		}
	}

	@Test
	public void loadTest()
	{
		try
		{
		BlueToothLEMetaData blueToothLEMetaData=BlueToothLEMetaDataManager.get("UnitTestMetaData");
		assertNotNull(blueToothLEMetaData);
		BlueToothLEMetaDataFacade facade=new BlueToothLEMetaDataFacade(blueToothLEMetaData);
        assertEquals("00000000-0000-0000-0000-000000001234",facade.getBlueToothLEServiceUUID("service1").toString());
        assertEquals("00000000-0000-0000-0000-000000002234",facade.getBlueToothLEServiceUUID("service2").toString());
        BlueToothLECharacteristic char1=facade.getBlueToothLECharacteristic("service1", "char1");
        assertEquals("00000000-0000-0000-0000-000000012345",char1.getCharacteristicUUID().toString());
        assertEquals("00000000-0000-0000-0000-000000012346",char1.getEnableCharacteristicUUID().toString());
        assertEquals(1,char1.getEnableCharacteristicValue());
        assertEquals(2,char1.getDisableCharacteristicValue());
        BlueToothLECharacteristic char2=facade.getBlueToothLECharacteristic("service1", "char2");
        assertEquals("00000000-0000-0000-0000-000000022345",char2.getCharacteristicUUID().toString());
        assertEquals("00000000-0000-0000-0000-000000022346",char2.getEnableCharacteristicUUID().toString());
        assertEquals(3,char2.getEnableCharacteristicValue());
        assertEquals(4,char2.getDisableCharacteristicValue());
        BlueToothLECharacteristic char3=facade.getBlueToothLECharacteristic("service2", "char1");
        assertEquals("00000000-0000-0000-0000-000000032345",char3.getCharacteristicUUID().toString());
        assertEquals("00000000-0000-0000-0000-000000033346",char3.getEnableCharacteristicUUID().toString());
        assertEquals(5,char3.getEnableCharacteristicValue());
        assertEquals(6,char3.getDisableCharacteristicValue());
		}catch(Exception ex){
			fail("loadTest failed because "+ex.getMessage());
		}
	}
	
	@Test
	public void loadSensorTagTest()
	{
		try
		{
		BlueToothLEMetaData blueToothLEMetaData=BlueToothLEMetaDataManager.get("SensorTagTest");
		assertNotNull(blueToothLEMetaData);
			}catch(Exception ex){
			fail("loadSensorTagTest failed because "+ex.getMessage());
		}
	}
}
