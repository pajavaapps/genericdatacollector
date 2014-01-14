package com.javaapps.gdc.model;

import java.io.File;

public class Config {

	private static Config config;

	private File filesDir;

	private String deviceId;

	private String customIdentifier;

	private int testStatusCode = 200;

	private int gforceUploadPeriod = 300000;

	private int gforceUploadDelay = 300000;

	private int locationUploadPeriod = 500000;

	private int locationUploadDelay = 500000;

	private long minimumLoggingIntervals = 400;

	private int locationListenerBufferSize = 100;

	private int gforceListenerBufferSize = 100;

	private long httpTimeout = 4000;

	private int uploadBatchSize = 1000;

	private int version = 0;
	
	private String dataFileExtension="obj";

	private String serverURL="http://legaltrackerserver.myjavaapps.cloudbees.net";
	
	private String externalIP;
	
	public String getLocationDataEndpoint() {
		return serverURL+"/backend/uploadLocationData";
	}

	public String getGforceDataEndpoint() {
		return serverURL+"/backend/uploadGForceData";
	}
	
	public String getExternalIPEndpoint() {
		return serverURL+"/backend/getExternalIP";
	}

	public String getServerURL() {
		return serverURL;
	}

	public void setServerURL(String serverURL) {
		this.serverURL = serverURL;
	}

	public int getLocationListenerBufferSize() {
		return locationListenerBufferSize;
	}

	public void setLocationListenerBufferSize(int locationListenerBufferSize) {
		this.locationListenerBufferSize = locationListenerBufferSize;
	}

	public long getHttpTimeout() {
		return httpTimeout;
	}

	public void setHttpTimeout(long httpTimeout) {
		this.httpTimeout = httpTimeout;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public long getMinimumLoggingIntervals() {
		return minimumLoggingIntervals;
	}

	public void setMinimumLoggingIntervals(long minimumLoggingIntervals) {
		this.minimumLoggingIntervals = minimumLoggingIntervals;
	}

	public int getLocationUploadPeriod() {
		return locationUploadPeriod;
	}

	public void setLocationUploadPeriod(int locationUploadPeriod) {
		this.locationUploadPeriod = locationUploadPeriod;
	}

	public int getLocationUploadDelay() {
		return locationUploadDelay;
	}

	public void setLocationUploadDelay(int locationUploadDelay) {
		this.locationUploadDelay = locationUploadDelay;
	}

	public int getUploadBatchSize() {
		return uploadBatchSize;
	}

	public void setUploadBatchSize(int uploadBatchSize) {
		this.uploadBatchSize = uploadBatchSize;
	}


	public void setFilesDir(File filesDir) {
		this.filesDir = filesDir;
	}

	public int getTestStatusCode() {
		return testStatusCode;
	}

	public void setTestStatusCode(int testStatusCode) {
		this.testStatusCode = testStatusCode;
	}

	public int getGforceUploadPeriod() {
		return gforceUploadPeriod;
	}

	public void setGforceUploadPeriod(int gforceUploadPeriod) {
		this.gforceUploadPeriod = gforceUploadPeriod;
	}

	public int getGforceUploadDelay() {
		return gforceUploadDelay;
	}

	public void setGforceUploadDelay(int gforceUploadDelay) {
		this.gforceUploadDelay = gforceUploadDelay;
	}

	public int getGforceListenerBufferSize() {
		return gforceListenerBufferSize;
	}

	public void setGforceListenerBufferSize(int gforceListenerBufferSize) {
		this.gforceListenerBufferSize = gforceListenerBufferSize;
	}

	public String getCustomIdentifier() {
		return customIdentifier;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public void setCustomIdentifier(String customIdentifier) {
		this.customIdentifier = customIdentifier;
	}

	public synchronized static Config getInstance() {
		if (config == null) {
			config = new Config();
		}
		return (config);
	}

	private Config() {

	}

	public void setExternalIP(String externalIP) {
		this.externalIP=externalIP;
	}

	public String getExternalIP() {
		return externalIP;
	}

	public File getFilesDir() {
		return filesDir;
	}

	public String getDataFileExtension() {
		return dataFileExtension;
	}

	public void setDataFileExtension(String dataFileExtension) {
		this.dataFileExtension = dataFileExtension;
	}
	
	
   
}
