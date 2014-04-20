package com.javaapps.gdc.dao;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.junit.Test;

import com.javaapps.gdc.model.BluetoothData;
import com.javaapps.gdc.model.GForce;
import com.javaapps.gdc.model.GPS;
import com.javaapps.gdc.model.GenericData;
import com.javaapps.gdc.model.GenericDataUpload;
import com.javaapps.gdc.types.DataType;

public class GenericDataDaoIsolatedTest {

	@Test
	public void testSplitListsByDayAndService() {
		Date now = new Date();
		try {
			GenericDataDAO genericDataDAO = new GenericDataDAO();
			GenericDataUpload genericDataUpload = new GenericDataUpload();
			genericDataUpload.setDataType(DataType.BLUETOOTH_DATA);
			genericDataUpload.setUploadDate(now);
			Scanner scanner = new Scanner(new FileInputStream(
					"src/test/resources/testdata/bluetooth_unittest.obj"));
			while (scanner.hasNext()) {
				String csvString = scanner.nextLine();
				BluetoothData bluetoothData = new BluetoothData(csvString);
				genericDataUpload.getGenericDataList().add(bluetoothData);
			}
			List<GenericDataUpload> uploadLists = genericDataDAO
					.splitListsByDayAndService(genericDataUpload);
			assertEquals(4,uploadLists.size() );
			int beginSize=genericDataUpload.getGenericDataList().size();
			assertTrue(beginSize>0);
			int endSize=0;
			for (GenericDataUpload tmpUploadData:uploadLists)
			{
				endSize=endSize+tmpUploadData.getGenericDataList().size();
			}
			assertEquals(beginSize,endSize);
			scanner.close();
		} catch (Exception ex) {
			fail("testGetDailyCollectionName failed because " + ex.getMessage());
		}

	}

	@Test
	public void testGetDailyCollectionNames() {
		try {
			GenericDataDAO genericDataDAO = new GenericDataDAO();
			Date now = new Date();
			GenericData genericData=new GForce();
			genericData.setSampleDate(now);
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String dateStr = dateFormat.format(now);
			GenericDataUpload genericDataUpload = new GenericDataUpload();
			genericDataUpload.setDataType(DataType.GFORCE);
			genericDataUpload.setUploadDate(now);
				String collectionName = genericDataDAO.getDailyCollectionName(
					genericDataUpload, genericData);
			assertEquals(collectionName, "gforce_" + dateStr);

			genericDataUpload.setDataType(DataType.GPS);
			genericData=new GPS();
			genericData.setSampleDate(now);
			collectionName = genericDataDAO.getDailyCollectionName(
					genericDataUpload, genericData);
			assertEquals(collectionName, "gps_" + dateStr);

			BluetoothData bluetoothData = new BluetoothData();
			bluetoothData.setSampleDate(now);
			bluetoothData.setSensorId("sensorId");
			bluetoothData.setServiceName("Bluetooth Service");
			genericDataUpload.setDataType(DataType.BLUETOOTH_DATA);
			collectionName = genericDataDAO.getDailyCollectionName(
					genericDataUpload, bluetoothData);
			assertEquals(collectionName, "bluetooth_sensorId_BluetoothService_"
					+ dateStr);
		} catch (Exception ex) {
			fail("testGetDailyCollectionName failed because " + ex.getMessage());
		}

	}

	@Test(expected = UnsupportedOperationException.class)
	public void testIncorrectGenericDataTest() {
		GenericDataDAO genericDataDAO = null;
		try {
			genericDataDAO = new GenericDataDAO();
		} catch (Exception ex) {
			// ignore
		}
		Date now = new Date();
		GenericDataUpload genericDataUpload = new GenericDataUpload();
		genericDataUpload.setDataType(DataType.GFORCE);
		genericDataUpload.setDataType(DataType.BLUETOOTH_DATA);
		genericDataUpload.setUploadDate(now);
		GenericData genericData = new GForce();
		genericData.setSampleDate(now);
		String collectionName = genericDataDAO.getDailyCollectionName(
				genericDataUpload, genericData);

	}

}
