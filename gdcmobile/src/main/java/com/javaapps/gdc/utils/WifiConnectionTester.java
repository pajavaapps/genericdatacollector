package com.javaapps.gdc.utils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import com.javaapps.gdc.Constants;
import com.javaapps.gdc.pojos.Config;

import android.util.Log;

public class WifiConnectionTester {

	public static boolean testMode = false;
    private final static String EXTERNAL_IP_CONTEXT="/backend/getExternalIP";
	
	public static boolean testConnection() {
		boolean retValue = false;
		if (testMode) {
			return true;
		}
		String testEndpoint=Config.getInstance().getDataEndpoint()+EXTERNAL_IP_CONTEXT;
		Log.i(Constants.GENERIC_COLLECTOR_TAG,"Testing endpoint to backend server "+testEndpoint);
		HttpClientFactory httpClientFactory = new HttpClientFactoryImpl();
		HttpClient httpClient = httpClientFactory.getHttpClient();
		if (httpClient != null) {
			try {
				HttpGet httpGet = new HttpGet(testEndpoint);
				HttpResponse response = httpClient.execute(httpGet);
				int statusCode = response.getStatusLine().getStatusCode();
				retValue = (statusCode / 100) == 2;
				if (retValue) {
					httpGet = new HttpGet(testEndpoint);
					response = httpClient.execute(httpGet);
					statusCode = response.getStatusLine().getStatusCode();
					if ((statusCode / 100) == 2) {
						byte[] buffer = new byte[50];
						response.getEntity().getContent().read(buffer);
						String responseString = (new String(buffer)).trim();
						if (responseString.contains("javaapps")) {
							String ipParts[] = responseString.split("\\,");
							if (ipParts.length > 2) {
								Config.getInstance().setExternalIP(
										ipParts[1] + ":" + ipParts[2]);
							}
						} else {
							retValue = false;
						}
					}
				}
			} catch (Throwable ex) {
				Log.i(Constants.GENERIC_COLLECTOR_TAG,
						"Unable to connect to test url");
			}
		}
		return retValue;
	}
}
