package com.javaapps.gdc.pojos;

import java.io.File;

import com.javaapps.gdc.exceptions.ConfigNotInitializedException;

public class Config {

	private static Config config;

	private DeviceMetaData deviceMetaData;

	private String externalIP;

	private int version=0;
	
	private File filesDir;
	
	private int uploadBatchSize=100;
	
	public synchronized static Config getInstance() {
		if (config == null) {
			throw new ConfigNotInitializedException("config is not initialized");
		}
		return (config);
	}

	public synchronized static void setConfigInstance(DeviceMetaData deviceMetaData){
		config=new Config(deviceMetaData);
	}
	
	private Config(DeviceMetaData deviceMetaData) {
        this.deviceMetaData=deviceMetaData;
	}


	public String getDataFileExtension() {
		return "obj";
	}



	public int getUploadBatchSize() {
		return uploadBatchSize;
	}


	public String getCustomIdentifier() {
		return deviceMetaData.getCustomIdentifier();
	}

	public File getFilesDir() {
		return filesDir;
	}


	public void setFilesDir(File filesDir) {
		this.filesDir = filesDir;
	}


	public void setVersion(int version) {
		this.version = version;
	}

	public int getVersion() {
		return version;
	}


	public String getDeviceId() {
		return deviceMetaData.getDeviceId();
	}


	public String getDataEndpoint() {
		return deviceMetaData.getDataEndpoint(); 
	}


	public String getExternalIPEndpoint() {
			return this.externalIP;
	}


	public void setExternalIP(String externalIP) {
		 this.externalIP=externalIP;
	}


	public void setUploadBatchSize(int uploadBatchSize) {
		this.uploadBatchSize=uploadBatchSize;
	}

}
