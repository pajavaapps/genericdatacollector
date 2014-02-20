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
import org.codehaus.jackson.map.ObjectMapper;
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
import com.javaapps.gdc.utils.WifiConnectionTester;
import com.javaapps.gdc.factories.GenericDataFactory;
import com.javaapps.gdc.io.DataFile;
import com.javaapps.gdc.pojos.Config;
import com.javaapps.gdc.model.GenericDataUpload;
import com.javaapps.gdc.model.FileResultMap;
import com.javaapps.gdc.model.FileResultMapsWrapper;
import com.javaapps.gdc.model.GenericData;
import com.javaapps.gdc.model.Monitor;
import com.javaapps.gdc.model.SystemMonitor;

public class DataUploader {

	private HttpClientFactory httpClientFactory = new HttpClientFactoryImpl();

	private File filesDir;

	private static final String UPLOAD_DATA_CONTEXT_PATH ="/backend/uploadGenericData";

	public DataUploader() {
		this.filesDir = Config.getInstance().getFilesDir();
	}

	public void setHttpClientFactory(HttpClientFactory httpClientFactory) {
		this.httpClientFactory = httpClientFactory;
	}

	public void uploadData() {
		if (!WifiConnectionTester.testConnection())
		{
			Log.i(Constants.GENERIC_COLLECTOR_TAG,"Cannot get wifi connection at endpoint "+Config.getInstance().getDataEndpoint()+".  Skipping upload");
			SystemMonitor.getInstance().setLastUploadStatusCode(Constants.COULD_NOT_GET_WIFI_CONNECTION);
			return;
		}
		Log.i(Constants.GENERIC_COLLECTOR_TAG,"Got wifi connection.  Beginning data upload from directory "+this.filesDir.getAbsolutePath());
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
		Log.i(Constants.GENERIC_COLLECTOR_TAG,"uploading file "+file.getAbsolutePath()+" with data type "+dataType);
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
					"unable to open location data file "+file.getAbsolutePath()+" and data type "+dataType+"because "
							+DataCollectorUtils.getStackTrackElement(ex),ex);
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
		// upload timestamp will be the first date in the list
		Config config=Config.getInstance();
		GenericDataUpload dataUpload = new GenericDataUpload(dataType,config.getDeviceId(),
				dataList.get(0).getSampleDate(), dataList);
		dataUpload.setVersion(config.getVersion());
		dataUpload.setCustomIdentifier(config.getCustomIdentifier());
		try {
			SystemMonitor.getInstance().setLastUploadDate(new Date());
			ObjectMapper objectMapper=new ObjectMapper();
			String jsonStr = objectMapper.writeValueAsString(dataUpload);
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

		private String getUploadDataEndpoint()
		{
			return Config.getInstance().getDataEndpoint()+UPLOAD_DATA_CONTEXT_PATH;
		}
		
		public void run() {
			HttpClient httpClient = httpClientFactory.getHttpClient();
			if (httpClient != null) {
				try {
					HttpPost httppost = new HttpPost(getUploadDataEndpoint());
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
							2);
					nameValuePairs.add(new BasicNameValuePair("data", jsonStr));
					nameValuePairs.add(new BasicNameValuePair("dataType", dataType.name()));
					nameValuePairs.add(new BasicNameValuePair("normalizedEmail", Config.getInstance().getNormalizedEmail()));
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					HttpResponse response = httpClient.execute(httppost);
					int statusCode = response.getStatusLine().getStatusCode();
					fileResultMap.getResultMap().put(index, statusCode);
					if (statusCode / 100 == 2) {
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
