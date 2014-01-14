package com.javaapps.gdc.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.javaapps.gdc.Constants;
import com.javaapps.gdc.model.DataUpload;
import com.javaapps.gdc.model.GForce;
import com.javaapps.gdc.model.GPS;
import com.javaapps.gdc.model.GenericData;
import com.javaapps.gdc.types.DataType;

public class JSONUtils {

	private static JSONObject convertToJSON(GPS gps) throws JSONException {
		JSONObject arrayObject=new JSONObject();
		arrayObject.put("sampleDateInMillis",gps.getSampleDate().getTime());
		arrayObject.put("latitude",gps.getLatitude());
		arrayObject.put("longitude",gps.getLongitude());
		arrayObject.put("bearing",gps.getBearing());
		arrayObject.put("speed",gps.getSpeed());
		arrayObject.put("altitude",gps.getAltitude());
		return arrayObject;
	}

	private static JSONObject convertToJSON(GForce gforce) throws JSONException {
		JSONObject arrayObject = new JSONObject();
		arrayObject.put("sampleDateInMillis",
				gforce.getSampleDateInMillis());
		arrayObject.put("x", gforce.getX());
		arrayObject.put("y", gforce.getY());
		arrayObject.put("z", gforce.getZ());
		return arrayObject;
	}

	private static JSONObject convertToJSON(GenericData genericData) throws JSONException {
		JSONObject arrayObject = new JSONObject();
		arrayObject.put("sampleDateInMillis",
				genericData.getSampleDateInMillis());
		arrayObject.put("value", genericData.getValue());
		return arrayObject;
	}
	public static String convertToJSON(DataType dataType, DataUpload dataUpload) throws JSONException {
		JSONObject jsonObj = new JSONObject();
		DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		jsonObj.put("version", dataUpload.getVersion());
		jsonObj.put("customIdentifier", dataUpload.getCustomIdentifier());
		jsonObj.put("deviceId", dataUpload.getDeviceId());
		Date uploadDate=dataUpload.getDate();
		jsonObj.put("uploadDate", (uploadDate != null )?dateFormat.format(uploadDate):null);
		JSONArray jsonArray=new JSONArray();
		jsonObj.put("dataList", jsonArray);
		for (GenericData genericData:dataUpload.getDataList()){
			if (dataType==DataType.GPS){
				jsonArray.put(convertToJSON((GPS) genericData));
			}else if (dataType==DataType.GFORCE){
				jsonArray.put(convertToJSON((GForce) genericData));
			}
			else if (dataType==DataType.GFORCE){
				jsonArray.put(convertToJSON( genericData));
			}
			else{
				Log.e(Constants.GENERIC_COLLECTOR_TAG,"Could not convert data list to json because data type is not supported");
			}
		}
		return(jsonObj.toString());
	}
}
