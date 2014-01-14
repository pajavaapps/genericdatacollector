package com.javaapps.gdc.utils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;



public class HttpClientFactoryImpl implements HttpClientFactory {

	public HttpClient getHttpClient() {
		HttpClient client= new DefaultHttpClient();
		final HttpParams httpParameters = client.getParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, 15 * 1000);
		return client;
	}

	
}
