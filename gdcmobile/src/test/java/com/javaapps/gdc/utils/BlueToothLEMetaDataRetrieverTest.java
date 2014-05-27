package com.javaapps.gdc.utils;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import com.javaapps.gdc.probes.BlueToothLEMetaData;
import com.javaapps.gdc.probes.BlueToothLEService;

@RunWith(RobolectricTestRunner.class)
public class BlueToothLEMetaDataRetrieverTest {

	@Test
	public void testLoadBlueToothLEMetaDataFromFile()
	{
		File testFile=new File("src/test/resources/SensorTag.json");
		BlueToothLEMetaData blueToothLEMetaData=BlueToothLEMetaDataRetriever.loadBlueToothLEMetaDataFromFile(testFile);
		assertNotNull(blueToothLEMetaData);
		assertEquals(2,blueToothLEMetaData.getServiceMetaDataMap().size() );
		assertEquals("Humidity",blueToothLEMetaData.getServiceMetaDataMap().get("f000aa20-0451-4000-b000-000000000000").getServiceName());
		BlueToothLEService barometerService=blueToothLEMetaData.getServiceMetaDataMap().get("f000aa40-0451-4000-b000-000000000000");
		assertEquals("Barometer", barometerService.getServiceName());
		assertEquals("Barometer", barometerService.getCharacteristicMap().get("f000aa41-0451-4000-b000-000000000000").getCharacteristicName());
		assertEquals("BarometerCalibration", barometerService.getCharacteristicMap().get("f000aa43-0451-4000-b000-000000000000").getCharacteristicName());
	}
}
