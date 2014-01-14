package com.javaapps.gdc.utils;

import org.apache.http.client.HttpClient;

public interface HttpClientFactory {

	public abstract HttpClient getHttpClient();

}