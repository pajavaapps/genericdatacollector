package com.javaapps.gdc.factories;

import android.hardware.SensorEvent;
import android.location.Location;
import android.util.Log;

import com.javaapps.gdc.Constants;
import com.javaapps.gdc.model.BluetoothData;
import com.javaapps.gdc.model.GForce;
import com.javaapps.gdc.model.GPS;
import com.javaapps.gdc.model.GenericData;
import com.javaapps.gdc.types.DataType;

public class GenericDataFactory {

	public static GenericData createGenericData(DataType dataType,
			String csvLine) {
		if (dataType == DataType.GPS) {
			return new GPS(csvLine);
		} else if (dataType == DataType.GFORCE) {
			return new GForce(csvLine);
		} 
		else if (dataType == DataType.BLUETOOTH_DATA) {
			return new BluetoothData(csvLine);
		} 
		else {
			Log.e(Constants.GENERIC_COLLECTOR_TAG, "Unsupported data type");
			return null;
		}
	}

	public static GenericData createGenericData(
			Location location) {
		GPS gps=new GPS(location.getLatitude(), location.getLongitude(), location.getSpeed(),
				location.getBearing(), location.getAltitude(), location.getTime());
		return gps;
	}

	public static GenericData createGenericData(
			SensorEvent sensorEvent) {
		GForce gforce=new GForce(sensorEvent.values[0],sensorEvent.values[1],sensorEvent.values[2],System.currentTimeMillis());
		return gforce;
	}

}
