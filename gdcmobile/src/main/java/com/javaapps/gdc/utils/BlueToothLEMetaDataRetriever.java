package com.javaapps.gdc.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import android.os.StrictMode;
import android.util.Log;

import com.javaapps.gdc.Constants;
import com.javaapps.gdc.pojos.Config;
import com.javaapps.gdc.probes.BlueToothLEMetaData;
import com.javaapps.gdc.probes.BlueToothLEMetaDataManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

public class BlueToothLEMetaDataRetriever {

	private static final  String META_DATA_EXTENSION=".meta";
	private static final String EXTERNAL_BLUETOOTH_META_DATA = "/backend/getBlueToothMetaData?sensorKey=";

	public static BlueToothLEMetaData getBlueToothLEMetaData(String sensorKey) {
		BlueToothLEMetaData blueToothLEMetaData = null;
		String testEndpoint = Config.getInstance().getDataEndpoint()
				+ EXTERNAL_BLUETOOTH_META_DATA + sensorKey;
		HttpClientFactory httpClientFactory = new HttpClientFactoryImpl();
		File cachedFile=new File(Config.getInstance().getFilesDir(),sensorKey+META_DATA_EXTENSION);
		Log.i(Constants.GENERIC_COLLECTOR_TAG2,
				"getting bluetooth meta data at endpoint  " + testEndpoint);
		HttpClient httpClient = httpClientFactory.getHttpClient();
		if (httpClient != null) {
			try {
				HttpGet httpGet = new HttpGet(testEndpoint);
				HttpResponse response = httpClient.execute(httpGet);
				int statusCode = response.getStatusLine().getStatusCode();
				boolean retValue = (statusCode / 100) == 2;
				Log.i(Constants.GENERIC_COLLECTOR_TAG2,
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
					FileWriter fileWriter=new FileWriter(cachedFile);
					fileWriter.write(sb.toString());
					fileWriter.close();
					Log.i(Constants.GENERIC_COLLECTOR_TAG2,
							"got meta data  " + sb.toString()+" bluetooth meta data "+blueToothLEMetaData);
				}
			} catch (Throwable ex) {
				Log.i(Constants.GENERIC_COLLECTOR_TAG2,
						"Unable to connect to blue tooth meta data url because "+ex.getMessage(),ex);
			}
		} else {
			Log.i(Constants.GENERIC_COLLECTOR_TAG2,
					"Cannot retrieve bluetooth meta data because no wifi connection");
		}
		if ( blueToothLEMetaData == null && cachedFile.exists()){
			Scanner scanner=null;
			try {
				scanner = new Scanner(new FileReader(cachedFile));
			} catch (FileNotFoundException e) {
				return null; //should never happen because we test for the existence of the file
			}
			StringBuilder sb=new StringBuilder();
			while ( scanner.hasNext()){
				sb.append(scanner.next());
			}
			if ( sb.length() > 0)
			{
			try {
				blueToothLEMetaData = BlueToothLEMetaDataManager.convertToObject(sb
						.toString());
				Log.i(Constants.GENERIC_COLLECTOR_TAG2,
						"Retrieving meta data from cache "+blueToothLEMetaData);
			} catch (Exception ex) {
				Log.e(Constants.GENERIC_COLLECTOR_TAG2,
						"Could not retrieve meta data from cache because "+ex.getMessage(),ex);
				return null;
			} 
			}
			scanner.close();
		}
		return blueToothLEMetaData;
	}
}
