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
			+ " ( EMAIL text not null,DEVICE_ID text not null unique, CUSTOM_IDENTIFIER text not null unique,DATA_ENDPOINT text not null,BATCH_UPLOAD_SIZE integer not null)";
	private final static String SENSOR_TABLE_CREATE = "create table "
			+ SENSOR_TABLE
			+ " ( ID  text not null unique, DATA_TYPE text not null, DATA_SUBTYPE text,SAMPLING_PERIOD integer default 100,DESCRIPTION text,"
			+ "AGGREGATION_METHOD text default 'SIMPLE', AGGREGATION_PERIOD integer default 1000 ,ACTIVE text not null default 'N',CONVERSION_FACTOR real default 1.0,SERVICE_STRING text)";
	private final static String SENSOR_COLUMNS[] = { "ID", "DATA_TYPE",
			"DATA_SUBTYPE", "SAMPLING_PERIOD", "DESCRIPTION",
			"AGGREGATION_METHOD", "AGGREGATION_PERIOD", "ACTIVE",
			"CONVERSION_FACTOR","SERVICE_STRING"};
	private final static String DEVICE_COLUMNS[] = { "EMAIL", "DEVICE_ID",
			"CUSTOM_IDENTIFIER","DATA_ENDPOINT","BATCH_UPLOAD_SIZE"};
	private final static String INSERT_GPS="insert into "+SENSOR_TABLE+ "(ID,DATA_TYPE,ACTIVE,DESCRIPTION) values ('1','GPS','N','GPS Sensor')";
	private final static String INSERT_GFORCE="insert into "+SENSOR_TABLE+ "(ID,DATA_TYPE,ACTIVE,DESCRIPTION) values ('2','GFORCE','N','GForce Sensor')";
	
	public DBAdapter(Context context) {
		this.context = context;
	};

	private ContentValues getContentValues(SensorMetaData sensorMetaData){
		ContentValues contentValues = new ContentValues();
		contentValues.put("ID", sensorMetaData.getId());
		contentValues.put("DATA_TYPE", sensorMetaData.getDataType().toString());
		contentValues.put("DATA_SUBTYPE", sensorMetaData.getDataSubType());
		if (sensorMetaData.getSamplingPeriod() > 0)
		{
		contentValues
				.put("SAMPLING_PERIOD", sensorMetaData.getSamplingPeriod());
		}else{
			contentValues
			.put("SAMPLING_PERIOD", SensorMetaData.DEFAULT_SAMPLING_PERIOD);
		}
		contentValues.put("DESCRIPTION", sensorMetaData.getDescription());
		if (sensorMetaData.getAggregationType() != null)
		{
		contentValues.put("AGGREGATION_METHOD",
				sensorMetaData.getAggregationType().toString());
		}else{
			contentValues.put("AGGREGATION_METHOD",
					SensorMetaData.DEFAULT_AGGREGATION_TYPE.toString());
		}
		if (sensorMetaData.getAggregationPeriod() > 0)
		{
		contentValues.put("AGGREGATION_PERIOD",
				sensorMetaData.getAggregationPeriod());
		}else{
			contentValues.put("AGGREGATION_PERIOD",
					SensorMetaData.DEFAULT_AGGREGATION_PERIOD);
		}
		contentValues.put("ACTIVE", sensorMetaData.getActive());
		if (sensorMetaData.getSamplingPeriod() != 0.0)
		{
		contentValues.put("CONVERSION_FACTOR",
				sensorMetaData.getConversionFactor());
		}else{
			contentValues.put("CONVERSION_FACTOR",
					SensorMetaData.DEFAULT_CONVERSION_FACTOR);
		}
		contentValues.put("SERVICE_STRING", sensorMetaData.getServiceString());
		return(contentValues);
	}
	
	public long insertSensorMetaData(SensorMetaData sensorMetaData) {
		ContentValues contentValues= getContentValues( sensorMetaData);
		return (genericDB.insertOrThrow(SENSOR_TABLE, null, contentValues));
	}

	public long updateSensorMetaData(SensorMetaData sensorMetaData) {
		ContentValues contentValues= getContentValues( sensorMetaData);
		return (genericDB.update(SENSOR_TABLE,  contentValues,"ID=?", new String[] { sensorMetaData.getId() }));
	}

	public SensorMetaData getSensorMetaData(String id) {
		SensorMetaData sensorMetaData = null;
		Cursor cursor = genericDB.query(this.SENSOR_TABLE, SENSOR_COLUMNS,
				"ID=?", new String[] { id }, null, null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			sensorMetaData = createSensorMetaData(cursor);
		}
		if ( sensorMetaData != null)
		{
		sensorMetaData.hydrateServiceString();
		}
		return sensorMetaData;
	}

	public void delete(SensorMetaData sensorMetaData) {
		 genericDB.delete(this.SENSOR_TABLE,"ID=?", new String[] { sensorMetaData.getId()});
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
			SensorMetaData sensorMetaData=createSensorMetaData(cursor);
			sensorMetaData.hydrateServiceString();
			sensorMetaDataList.add(sensorMetaData);
		}
		return sensorMetaDataList;
	}

	private SensorMetaData createSensorMetaData(Cursor cursor) {
		SensorMetaData sensorMetaData = new SensorMetaData(cursor.getString(0),
				cursor.getString(1), cursor.getString(2), cursor.getInt(3),
				cursor.getString(4), cursor.getString(5), cursor.getInt(6),
				cursor.getString(7), cursor.getDouble(8),cursor.getString(9));
		return sensorMetaData;
	}

	public long insertDeviceMetaData(DeviceMetaData deviceMetaData) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("EMAIL", deviceMetaData.getEmail());
		contentValues.put("DEVICE_ID", deviceMetaData.getDeviceId());
		contentValues.put("CUSTOM_IDENTIFIER",
				deviceMetaData.getCustomIdentifier());
		contentValues.put("DATA_ENDPOINT",
				deviceMetaData.getDataEndpoint());
		contentValues.put("BATCH_UPLOAD_SIZE",
				deviceMetaData.getUploadBatchSize());
		if (getAllDeviceMetaData().size() == 0) {
			Log.i(Constants.GENERIC_COLLECTOR_TAG,"Inserting device rows");
			return (genericDB.insert(DEVICE_TABLE, null, contentValues));
		} else {
			Log.i(Constants.GENERIC_COLLECTOR_TAG,"Update device row");
			return (genericDB.update(DEVICE_TABLE, contentValues,null,null));
		}
	}

	public DeviceMetaData getDeviceMetaData() {
		DeviceMetaData deviceMetaData = null;
		Cursor cursor = genericDB.query(DEVICE_TABLE, DEVICE_COLUMNS, null,
				null, null, null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			deviceMetaData = createDeviceMetaData(cursor);
		}else{
			Log.i(Constants.GENERIC_COLLECTOR_TAG,"Cursor has not rows "+cursor);
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
				cursor.getString(1), cursor.getString(2),cursor.getString(3));
		deviceMetaData.setUploadBatchSize(cursor.getInt(4));
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
               Log.e(Constants.GENERIC_COLLECTOR_TAG,"Cannot drop device table because "+ex.getMessage());
			}
			try {
				db.execSQL(SENSOR_TABLE_DROP);
			} catch (Exception ex) {
	              Log.e(Constants.GENERIC_COLLECTOR_TAG,"Cannot drop sensor table because "+ex.getMessage());
			}
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			try
			{
			db.execSQL(CONFIG_TABLE_CREATE);
			}catch(Exception ex){
				Log.e(Constants.GENERIC_COLLECTOR_TAG,"Could not create config table because "+ex.getMessage());
			}
			try
			{
			db.execSQL(SENSOR_TABLE_CREATE);
			}catch(Exception ex){
				Log.e(Constants.GENERIC_COLLECTOR_TAG,"Could not create senor table because "+ex.getMessage());
			}
            try
            {
			db.execSQL(INSERT_GPS);
			db.execSQL(INSERT_GFORCE);
			}catch(Exception ex){
				Log.e(Constants.GENERIC_COLLECTOR_TAG,"Could not insert sensor data because "+ex.getMessage());
			}
		}

		

		@Override
		public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
			// not used yet
		}

	}

}