package com.javaapps.gdc.uploader;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

import com.javaapps.gdc.Constants;
import com.javaapps.gdc.types.DataType;
import com.javaapps.gdc.utils.DataCollectorUtils;
import com.javaapps.gdc.utils.HttpClientFactory;
import com.javaapps.gdc.utils.HttpClientFactoryImpl;
import com.javaapps.gdc.utils.JSONUtils;
import com.javaapps.gdc.utils.WifiConnectionTester;
import com.javaapps.gdc.factories.GenericDataFactory;
import com.javaapps.gdc.io.DataFile;
import com.javaapps.gdc.model.Config;
import com.javaapps.gdc.model.DataUpload;
import com.javaapps.gdc.model.FileResultMap;
import com.javaapps.gdc.model.FileResultMapsWrapper;
import com.javaapps.gdc.model.GenericData;
import com.javaapps.gdc.model.Monitor;
import com.javaapps.gdc.model.SystemMonitor;

public class DataUploader {

	private HttpClientFactory httpClientFactory = new HttpClientFactoryImpl();

	private File filesDir;

	public DataUploader() {
		this.filesDir = Config.getInstance().getFilesDir();
	}

	public void setHttpClientFactory(HttpClientFactory httpClientFactory) {
		this.httpClientFactory = httpClientFactory;
	}

	public void uploadData() {
		if (!WifiConnectionTester.testConnection())
		{
			SystemMonitor.getInstance().setLastUploadStatusCode(Constants.COULD_NOT_GET_WIFI_CONNECTION);
			return;
		}
		for (File file : this.filesDir.listFiles()) {
			if (file.getName().contains(DataFile.ARCHIVE_STRING)) {
				loadFile(file);
			}
		}
	}
	
	private FileResultMap getResultMap(File file) throws FileNotFoundException,
			IOException, ClassNotFoundException {
		String fileName = file.getAbsolutePath();
		FileResultMap fileResultMap = FileResultMapsWrapper.getInstance()
				.getFileResultMaps().get(fileName);
		if (fileResultMap == null) {
			fileResultMap = new FileResultMap(fileName);
			FileResultMapsWrapper.getInstance().getFileResultMaps()
					.put(fileName, fileResultMap);
		}
		DataInputStream inputStream = null;
		try {
			inputStream = new DataInputStream(new FileInputStream(file));
			int bufferCounter = 0;
			int objectCounter = 0;
			int batchSize = Config.getInstance().getUploadBatchSize();
			try {
				while ((inputStream.readLine()) != null) {
					objectCounter++;
					if (objectCounter >= batchSize) {
						bufferCounter++;
						objectCounter = 0;
					}
				}
			} catch (EOFException ex) {
			}
			if (objectCounter > 0) {
				bufferCounter++;
			}
			for (int ii = 0; ii < bufferCounter; ii++) {
				fileResultMap.getResultMap().put(ii, -1);
			}
			return (fileResultMap);
		} finally {
			closeInputStream(inputStream);

		}
	}

	@SuppressWarnings("deprecation")
	private void loadFile(File file) {
		DataType dataType=getDataTypeFromFileName(file.getName());
		if ( dataType == null){
			Log.e(Constants.GENERIC_COLLECTOR_TAG,"Unable to load "+file.getName()+" because it is not a recognized data type");
			return;
		}
		DataInputStream inputStream = null;
		try {
			FileResultMap fileResultMap = getResultMap(file);
			inputStream = new DataInputStream(new FileInputStream(file));
			List<GenericData> dataList = new ArrayList<GenericData>();
			int index = 0;
			try {
				String csvLine = null;
				while ((csvLine = inputStream.readLine()) != null) {
					GenericData dataPoint =  GenericDataFactory.createGenericData(dataType,csvLine);
					if (dataPoint.getSampleDate() == null){
						continue;
					}
					dataList.add(dataPoint);
					if (dataList.size() > Config.getInstance()
							.getUploadBatchSize()) {
						uploadBatch(dataType,fileResultMap, index, dataList);
						index++;
						dataList = new ArrayList<GenericData>();
					}
				}
			} catch (EOFException ex) {
			}
			if (dataList.size() > 0) {
				uploadBatch(dataType,fileResultMap, index, dataList);
			}
		} catch (Throwable ex) {
			SystemMonitor.getInstance().setLastUploadStatusCode(
					Constants.SERIALIZATION_ERROR);
			Log.e(Constants.GENERIC_COLLECTOR_TAG,
					"unable to open location data file because "
							+DataCollectorUtils.getStackTrackElement(ex));
		} finally {
			closeInputStream(inputStream);
		}

	}

	private DataType getDataTypeFromFileName(String name) {
		for ( DataType dataType:DataType.values()){
			if ( name.contains(dataType.getPrefix())){
				return dataType;
			}
		}
		return null;
	}

	private void closeInputStream(InputStream is) {
		try {
			if (is != null) {
				is.close();
			}
		} catch (Exception ex) {
			Log.e(Constants.GENERIC_COLLECTOR_TAG,
					"Could not close archive input stream because "
							+DataCollectorUtils.getStackTrackElement(ex));
		}
	}

	public boolean uploadBatch(DataType dataType,FileResultMap fileResultMap, int index,
			List<GenericData> dataList) {
		if (dataList.size() == 0) {
			return true;
		}
		boolean retValue = true;
		SystemMonitor.getInstance().getMonitor(dataType).incrementTotalPointsUploaded(
				dataList.size());
		// upload timestamp will be the first date in the list
		Config config=Config.getInstance();
		DataUpload dataUpload = new DataUpload(config.getDeviceId(),
				dataList.get(0).getSampleDate(), dataList);
		dataUpload.setVersion(config.getVersion());
		dataUpload.setCustomIdentifier(config.getCustomIdentifier());
		try {
			SystemMonitor.getInstance().setLastUploadDate(new Date());
			String jsonStr = JSONUtils.convertToJSON(dataType,dataUpload);
			DataUploadTask dataUploadTask = new DataUploadTask(dataType,
					dataList.size(), fileResultMap, index, jsonStr);
			dataUploadTask.run();

		} catch (Throwable e) {
			Log.e(Constants.GENERIC_COLLECTOR_TAG,
					"cannot convert upload data to server because because"
							+DataCollectorUtils.getStackTrackElement(e));
		}
		return retValue;
	}

	private class DataUploadTask  {
		private int index;
		private String jsonStr;
		private FileResultMap fileResultMap;
		private int batchSize = 0;
		private DataType dataType;

		public DataUploadTask(DataType dataType,int batchSize, FileResultMap fileResultMap,
				int index, String jsonStr) {
			this.dataType=dataType;
			this.index = index;
			this.batchSize = batchSize;
			this.jsonStr = jsonStr;
			this.fileResultMap = fileResultMap;
		}

		public void run() {
			HttpClient httpClient = httpClientFactory.getHttpClient();
			if (httpClient != null) {
				try {
					HttpPost httppost = new HttpPost(Config.getInstance()

					.getLocationDataEndpoint());
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
							2);
					nameValuePairs.add(new BasicNameValuePair("data", jsonStr));
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					HttpResponse response = httpClient.execute(httppost);
					int statusCode = response.getStatusLine().getStatusCode();
					fileResultMap.getResultMap().put(index, statusCode);
					if (statusCode / 100 == 2) {
						SystemMonitor.getInstance().getMonitor(dataType).incrementTotalPointsProcessed(
								batchSize);
						if (fileResultMap.allBatchesUploaded()) {
							File file = new File(fileResultMap.getFileName());
							if (!file.delete()) {
								Log.i(Constants.GENERIC_COLLECTOR_TAG,
										"Could not delete "
												+ file.getAbsolutePath());
							}
						}
					} 
					SystemMonitor.getInstance().setLastUploadStatusCode(statusCode);
				} catch (Throwable e) {
					SystemMonitor.getInstance().setLastUploadStatusCode(-99);
					SystemMonitor.getInstance()
							.setLastConnectionError(e.getMessage());
					Log.e(Constants.GENERIC_COLLECTOR_TAG,
							"cannot  upload data to server because because"
									+DataCollectorUtils.getStackTrackElement(e));
				}
			} else {
				Log.i(Constants.GENERIC_COLLECTOR_TAG,
						"Could not get http client, in use by other process");
			}
		}
	}

}
