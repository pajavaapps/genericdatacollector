package com.javaapps.gdc.utils;

import java.io.InputStream;

import android.os.StrictMode;
import android.util.Log;

import com.javaapps.gdc.Constants;
import com.javaapps.gdc.pojos.Config;
import com.javaapps.gdc.probes.BlueToothLEMetaData;
import com.javaapps.gdc.probes.BlueToothLEMetaDataManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

public class BlueToothLEMetaDataRetriever {

	private static final String EXTERNAL_BLUETOOTH_META_DATA = "/backend/getBlueToothMetaData?sensorKey=";

	public static BlueToothLEMetaData getBlueToothLEMetaData(String sensorKey) {
		BlueToothLEMetaData blueToothLEMetaData = new BlueToothLEMetaData();
		String testEndpoint = Config.getInstance().getDataEndpoint()
				+ EXTERNAL_BLUETOOTH_META_DATA + sensorKey;
		HttpClientFactory httpClientFactory = new HttpClientFactoryImpl();
		Log.i(Constants.GENERIC_COLLECTOR_TAG + "2",
				"getting bluetooth meta data at endpoint  " + testEndpoint);
		HttpClient httpClient = httpClientFactory.getHttpClient();
		if (httpClient != null) {
			try {
				HttpGet httpGet = new HttpGet(testEndpoint);
				HttpResponse response = httpClient.execute(httpGet);
				int statusCode = response.getStatusLine().getStatusCode();
				boolean retValue = (statusCode / 100) == 2;
				Log.i(Constants.GENERIC_COLLECTOR_TAG + "2",
						"getting bluetooth meta data at endpoint with status code " + statusCode);
				if (retValue) {
					StringBuilder sb = new StringBuilder();
					byte[] buffer = new byte[5000];
					InputStream is = response.getEntity().getContent();
					while (is.read(buffer) > 0) {
						sb.append((new String(buffer)).trim());
					}
					blueToothLEMetaData = BlueToothLEMetaDataManager.convertToObject(sb
							.toString());
					Log.i(Constants.GENERIC_COLLECTOR_TAG + "2",
							"got meta data  " + sb.toString()+" bluetooth meta data "+blueToothLEMetaData);
				}
			} catch (Throwable ex) {
				Log.i(Constants.GENERIC_COLLECTOR_TAG + "2",
						"Unable to connect to blue tooth meta data url because "+ex.getMessage(),ex);
			}
		} else {
			Log.i(Constants.GENERIC_COLLECTOR_TAG + "2",
					"Cannot retrieve bluetooth meta data because no wifi connection");
		}
		return blueToothLEMetaData;
	}
}
