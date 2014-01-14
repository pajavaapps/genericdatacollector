package com.javaapps.gdc.factories;

import android.util.Log;

import com.javaapps.gdc.Constants;
import com.javaapps.gdc.model.GForce;
import com.javaapps.gdc.model.GPS;
import com.javaapps.gdc.model.GenericData;
import com.javaapps.gdc.types.DataType;

public class GenericDataFactory {

	public static GenericData createGenericData(DataType dataType,
			String csvLine) {
		if (dataType == DataType.GPS) {
			return new GPS(csvLine);
		} else if (dataType == DataType.GPS) {
			return new GForce(csvLine);
		} 
		else if (dataType == DataType.GENERIC) {
			return new GenericData(csvLine);
		}
		else {
			Log.e(Constants.GENERIC_COLLECTOR_TAG, "Unsupported data type");
			return null;
		}
	}

}
