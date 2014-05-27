package com.javaapps.gdc.services;

import static org.junit.Assert.*;

import java.io.FileReader;
import java.util.Scanner;

import org.junit.Test;

import com.javaapps.gdc.model.BluetoothData;
import com.javaapps.gdc.model.GenericData;
import com.javaapps.gdc.model.PostProcessedBluetoothData;

public class SensorTagPostProcessorTest {

	@Test
	public void test()
	{
		SensorTagPostProcessor sensorTagPostProcessor =new SensorTagPostProcessor();
		try
		{
		Scanner scanner =new Scanner(new FileReader("src/test/resources/testdata/bluetooth_unittest.obj"));
		while ( scanner.hasNextLine())
		{
			String line=scanner.nextLine();
			System.out.println(line);
			BluetoothData bluetoothData=new BluetoothData(line);
			GenericData postProcessedBluetoothData=sensorTagPostProcessor.convertToPostProcessedData(bluetoothData);
			System.out.println( bluetoothData.getServiceName()+" "+postProcessedBluetoothData);
		}
		
	}
	catch(Exception ex){
		fail("sensor tag test failed because"+ex.getMessage());
	}
	}
}
