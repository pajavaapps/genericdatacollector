package com.javaapps.gdc.db;

import java.util.ArrayList;
import java.util.List;

import com.javaapps.gdc.Constants;
import com.javaapps.gdc.pojos.SensorMetaData;
import com.javaapps.gdc.pojos.DeviceMetaData;
import com.javaapps.gdc.types.DataType;
import com.javaapps.gdc.utils.DataCollectorUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {

	DatabaseHelper genericDBHelper;
	SQLiteDatabase genericDB;

	private final Context context;

	public static final String DEVICE_TABLE = "CONFIG";
	public static final String SENSOR_TABLE = "SENSOR";
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "GENERIC_COLLECTOR_DB";
	private final static String DEVICE_TABLE_DROP = "drop table "
			+ DEVICE_TABLE;
	private final static String SENSOR_TABLE_DROP = "drop table "
			+ SENSOR_TABLE;
	private final static String CONFIG_TABLE_CREATE = "create table "
			+ DEVICE_TABLE
			+ " ( COMPANY text not null,DEVICE_ID text not null unique, CUSTOM_IDENTIFIER text not null unique)";
	private final static String SENSOR_TABLE_CREATE = "create table "
			+ SENSOR_TABLE
			+ " ( ID  text not null unique, DATA_TYPE text not null, DATA_SUBTYPE text,SAMPLING_PERIOD integer default 100,DESCRIPTION text,"
			+ "AGGREGATION_METHOD text default 'MAX', AGGREGATION_PERIOD integer default 1000 ,ACTIVE text not null default 'N',CONVERSION_FACTOR real default 1.0)";
	private final static String SENSOR_COLUMNS[] = { "ID", "DATA_TYPE",
			"DATA_SUBTYPE", "SAMPLING_PERIOD", "DESCRIPTION",
			"AGGREGATION_METHOD", "AGGREGATION_PERIOD", "ACTIVE",
			"CONVERSION_FACTOR" };
	private final static String DEVICE_COLUMNS[] = { "COMPANY", "DEVICE_ID",
			"CUSTOM_IDENTIFIER" };
	private final static String INSERT_GPS="insert into "+SENSOR_TABLE+ "(ID,DATA_TYPE,ACTIVE,DESCRIPTION) values ('1','GPS','N','GPS Sensor')";
	private final static String INSERT_GFORCE="insert into "+SENSOR_TABLE+ "(ID,DATA_TYPE,ACTIVE,DESCRIPTION) values ('2','GFORCE','N','GForce Sensor')";
	
	public DBAdapter(Context context) {
		this.context = context;
	};

	public long insertSensorMetaData(SensorMetaData sensorMetaData) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("ID", sensorMetaData.getId());
		contentValues.put("DATA_TYPE", sensorMetaData.getDataType().toString());
		contentValues.put("DATA_SUBTYPE", sensorMetaData.getDataSubType());
		contentValues
				.put("SAMPLING_PERIOD", sensorMetaData.getSamplingPeriod());
		contentValues.put("DESCRIPTION", sensorMetaData.getDescription());
		contentValues.put("AGGREGATION_METHOD",
				sensorMetaData.getAggregationMethod());
		contentValues.put("AGGREGATION_PERIOD",
				sensorMetaData.getAggregationPeriod());
		contentValues.put("ACTIVE", sensorMetaData.getActive());
		contentValues.put("CONVERSION_FACTOR",
				sensorMetaData.getConversionFactor());
		return (genericDB.insertOrThrow(SENSOR_TABLE, null, contentValues));
	}

	public SensorMetaData getSensorMetaData(String id) {
		SensorMetaData sensorMetaData = null;
		Cursor cursor = genericDB.query(this.SENSOR_TABLE, SENSOR_COLUMNS,
				"ID=?", new String[] { id }, null, null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			sensorMetaData = createSensorMetaData(cursor);
		}
		return sensorMetaData;
	}

	public List<SensorMetaData> getActiveSensorMetaData() {
		List<SensorMetaData> sensorMetaDataList = new ArrayList<SensorMetaData>();
		Cursor cursor = genericDB.query(SENSOR_TABLE, SENSOR_COLUMNS,
				"active=?", new String[] { "Y" }, null, null, null, null);
		while (cursor != null && cursor.moveToNext()) {
			sensorMetaDataList.add(createSensorMetaData(cursor));
		}
		return sensorMetaDataList;
	}

	public List<SensorMetaData> getAllSensorMetaData() {
		List<SensorMetaData> sensorMetaDataList = new ArrayList<SensorMetaData>();
		Cursor cursor = genericDB.query(SENSOR_TABLE, SENSOR_COLUMNS, null,
				null, null, null, null, null);
		while (cursor != null && cursor.moveToNext()) {
			sensorMetaDataList.add(createSensorMetaData(cursor));
		}
		return sensorMetaDataList;
	}

	private SensorMetaData createSensorMetaData(Cursor cursor) {
		SensorMetaData sensorMetaData = new SensorMetaData(cursor.getString(0),
				cursor.getString(1), cursor.getString(2), cursor.getInt(3),
				cursor.getString(4), cursor.getString(5), cursor.getInt(6),
				cursor.getString(7), cursor.getDouble(8));
		return sensorMetaData;
	}

	public long insertDeviceMetaData(DeviceMetaData deviceMetaData) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("COMPANY", deviceMetaData.getCompany());
		contentValues.put("DEVICE_ID", deviceMetaData.getDeviceId());
		contentValues.put("CUSTOM_IDENTIFIER",
				deviceMetaData.getCustomIdentifier());
		if (getAllDeviceMetaData().size() == 0) {
			return (genericDB.insert(DEVICE_TABLE, null, contentValues));
		} else {
			return (genericDB.update(DEVICE_TABLE, contentValues,null,null));
		}
	}

	public DeviceMetaData getDeviceMetaData() {
		DeviceMetaData deviceMetaData = null;
		Cursor cursor = genericDB.query(DEVICE_TABLE, DEVICE_COLUMNS, null,
				null, null, null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			deviceMetaData = createDeviceMetaData(cursor);
		}
		return deviceMetaData;
	}

	public List<DeviceMetaData> getAllDeviceMetaData() {
		List<DeviceMetaData> deviceMetaDataList = new ArrayList<DeviceMetaData>();
		Cursor cursor = genericDB.query(DEVICE_TABLE, DEVICE_COLUMNS, null,
				null, null, null, null, null);
		while (cursor != null && cursor.moveToNext()) {
			deviceMetaDataList.add(createDeviceMetaData(cursor));
		}
		return deviceMetaDataList;
	}

	private DeviceMetaData createDeviceMetaData(Cursor cursor) {
		DeviceMetaData deviceMetaData = new DeviceMetaData(cursor.getString(0),
				cursor.getString(1), cursor.getString(2));
		return deviceMetaData;
	}

	public DBAdapter open() throws SQLException {
		genericDBHelper = new DatabaseHelper(context);
		genericDB = genericDBHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		genericDBHelper.close();
	}

	static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		void drop(SQLiteDatabase db) {
			try {
				db.execSQL(DEVICE_TABLE_DROP);
			} catch (Exception ex) {

			}
			try {
				db.execSQL(SENSOR_TABLE_DROP);
			} catch (Exception ex) {

			}
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CONFIG_TABLE_CREATE);
			db.execSQL(SENSOR_TABLE_CREATE);
			db.execSQL(INSERT_GPS);
			db.execSQL(INSERT_GFORCE);
		}

		

		@Override
		public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
			// not used yet
		}

	}

}