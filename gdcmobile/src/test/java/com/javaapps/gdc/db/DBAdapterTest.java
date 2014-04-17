package com.javaapps.gdc.db;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowApplication;

import com.javaapps.gdc.activities.GenericCollectorActivity;
import com.javaapps.gdc.pojos.DeviceMetaData;
import com.javaapps.gdc.pojos.SensorMetaData;
import com.javaapps.gdc.types.AggregationType;
import com.javaapps.gdc.types.DataType;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

@RunWith(RobolectricTestRunner.class)
public class DBAdapterTest extends AndroidTestCase {

	private static final String TEST_FILE_PREFIX = "test_";

	private DBAdapter dbAdapter;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		try {
			RenamingDelegatingContext context = new RenamingDelegatingContext(
					getContext(), TEST_FILE_PREFIX);
			assertNotNull(context);
			dbAdapter = new DBAdapter(context);
			assertNotNull(dbAdapter);
			/*
			 * Activity activity =
			 * Robolectric.getShadowApplication().buildActivity
			 * (HelloAndroidActivity.class).create().get(); DBAdapter
			 * dbAdapter=new DBAdapter(activity.getApplicationContext());
			 */
			dbAdapter = dbAdapter.open();
			assertNotNull(dbAdapter.genericDBHelper);
			assertNotNull(dbAdapter.genericDB);
			dbAdapter.genericDBHelper.drop(dbAdapter.genericDB);
			dbAdapter.genericDBHelper.onCreate(dbAdapter.genericDB);
			System.out.println("Setup successful");

		} catch (Exception ex) {
			System.out.println(ex.getCause());
			ex.printStackTrace();
			fail("Unable to initialize sql lite becuase " + ex.getMessage());

		}
	}

	@Before
	public void init() {
		try {
			this.setUp();
		} catch (Exception ex) {
			fail("failed because " + ex.getMessage());
		}
	}

	@Test
	public void test() {
		String serviceStr="HUMIDITY,f000aa20-0451-4000-b000-000000000000,true\nBAROMETER,f000aa21-0451-4000-b000-000000000000,false\n";
		SensorMetaData sensorMetaData = new SensorMetaData("id",
				DataType.GPS.toString(), "dataSubType", 1, "description",
				AggregationType.SIMPLE.toString(), 2, "Y", 3.0,serviceStr);
		System.out.println(sensorMetaData);
		assertTrue(dbAdapter.insertSensorMetaData(sensorMetaData) != -1);
		sensorMetaData = dbAdapter.getSensorMetaData("id");
		assertNotNull(sensorMetaData);
		assertEquals("id", sensorMetaData.getId());
		assertEquals("GPS", sensorMetaData.getDataType().toString());
		assertEquals("dataSubType", sensorMetaData.getDataSubType());
		assertEquals("description", sensorMetaData.getDescription());
		assertTrue(AggregationType.SIMPLE== sensorMetaData.getAggregationType());
		assertEquals(1, sensorMetaData.getSamplingPeriod());
		assertEquals(2, sensorMetaData.getAggregationPeriod());
		assertEquals(3.0, sensorMetaData.getConversionFactor());
		assertEquals("Y", sensorMetaData.getActive());
		assertTrue(dbAdapter.getAllSensorMetaData().size() > 0);
		assertTrue(dbAdapter.getActiveSensorMetaData().size() > 0);
		assertTrue(sensorMetaData.getServiceList().size() > 1);
		
		sensorMetaData.setActive("N");
		dbAdapter.updateSensorMetaData(sensorMetaData);
		sensorMetaData = dbAdapter.getSensorMetaData("id");
		assertEquals("N", sensorMetaData.getActive());

		
		SensorMetaData sensorMetaData1 = new SensorMetaData("id2",
				DataType.GPS, "Y");
		assertTrue(dbAdapter.insertSensorMetaData(sensorMetaData1) != -1);
		sensorMetaData1= dbAdapter.getSensorMetaData("id2");
		System.out.println(sensorMetaData1);
		assertEquals(5000, sensorMetaData1.getSamplingPeriod());
		assertEquals(1.0, sensorMetaData1.getConversionFactor());
		assertEquals(5000, sensorMetaData1.getAggregationPeriod());
		assertTrue(AggregationType.SIMPLE == sensorMetaData1.getAggregationType());
		 dbAdapter.delete(sensorMetaData1);
		 assertNull( dbAdapter.getSensorMetaData("id2"));
		
		DeviceMetaData deviceMetaData = new DeviceMetaData("company",
				"deviceId", "customIdentifier","dataendpoint");
		System.out.println(deviceMetaData);
		assertTrue(dbAdapter.insertDeviceMetaData(deviceMetaData) != -1);
		deviceMetaData = dbAdapter.getDeviceMetaData();
		assertNotNull(deviceMetaData);
		assertEquals("company", deviceMetaData.getEmail());
		assertEquals("deviceId", deviceMetaData.getDeviceId());
		assertEquals("customIdentifier", deviceMetaData.getCustomIdentifier());
		assertEquals("dataendpoint", deviceMetaData.getDataEndpoint());
		assertEquals(100, deviceMetaData.getUploadBatchSize());
		deviceMetaData = new DeviceMetaData("company1", "deviceId1",
				"customIdentifier1","dataendpoint");

		assertTrue(dbAdapter.insertDeviceMetaData(deviceMetaData) != -1);
		deviceMetaData = dbAdapter.getDeviceMetaData();
		assertEquals(1,dbAdapter.getAllDeviceMetaData().size() );
		assertNotNull(deviceMetaData);
		assertEquals("company1", deviceMetaData.getEmail());
		assertEquals("deviceId1", deviceMetaData.getDeviceId());
		
		assertEquals("customIdentifier1", deviceMetaData.getCustomIdentifier());
	}

}
