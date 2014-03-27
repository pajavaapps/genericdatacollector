package com.javaapps.gdc.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import android.os.Environment;
import android.util.Log;

import com.javaapps.gdc.Constants;
import com.javaapps.gdc.interfaces.CsvWriter;
import com.javaapps.gdc.model.Monitor;
import com.javaapps.gdc.model.SystemMonitor;
import com.javaapps.gdc.pojos.Config;
import com.javaapps.gdc.pojos.SensorMetaData;
import com.javaapps.gdc.types.DataType;
import com.javaapps.gdc.utils.DataCollectorUtils;

public class DataFile<T> {

	public final static String ARCHIVE_STRING = "_archive_";

	private final ReentrantLock lock = new ReentrantLock();

	private File filesDir;
	private SensorMetaData sensorMetaData;
	private String extension;

	public DataFile(SensorMetaData sensorMetaData, String extension)
			throws FileNotFoundException, IOException {
		filesDir=Config.getInstance().getFilesDir();
        if ( ! filesDir.exists()){
        	if ( ! filesDir.mkdirs()){
        		Log.e(Constants.GENERIC_COLLECTOR_TAG,"Cannot create sdcard data directory");
        	}
        }
		this.sensorMetaData = sensorMetaData;
		this.extension = extension;
	}

	public void deleteFiles() {
		DataType dataType=sensorMetaData.getDataType();
		for (File file : filesDir.listFiles()) {
			if (file.getName().startsWith(dataType.getPrefix() + ARCHIVE_STRING)) {
				file.delete();
			}
		}
	}

	public List<T> writeToObjectFile(List<T> objectList) throws IOException {
		DataType dataType=sensorMetaData.getDataType();
		List<T> retList = new ArrayList<T>();
		boolean isNotLocked = lock.tryLock();
		// If it is locked then just return the list and try to save it another
		// time
		if (!isNotLocked) {
			return objectList;
		}
		File file = new File(filesDir, getActiveFileName());
		if ( file.length() > 10000 )
			{
			if (file.renameTo(new File(filesDir, getArchiveFileName()))) {
			Log.i(Constants.GENERIC_COLLECTOR_TAG, dataType.getPrefix()
					+ " file successfully archived");
			}else{
				Log.e(Constants.GENERIC_COLLECTOR_TAG, dataType.getPrefix()
						+ " was not able to archive file");
			}
		} 
		file = new File(filesDir, getActiveFileName());
		Log.i(Constants.GENERIC_COLLECTOR_TAG,"Opening buffer file "+file.getAbsolutePath());
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(file,true);
			Log.i(Constants.GENERIC_COLLECTOR_TAG,"Opening output stream "+fileOutputStream);
			boolean errorThrown = false;
			for (T object : objectList) {
				try {
					if ( object == null ){
						Log.i(Constants.GENERIC_COLLECTOR_TAG,"cannot save null object to file");
						continue;
					}
					if (errorThrown) {
						retList.add(object);
					} else {
						fileOutputStream.write(((CsvWriter)object).toCSV().getBytes());
					}
				} catch (Exception ex) {
					errorThrown = true;
					Log.e(Constants.GENERIC_COLLECTOR_TAG, "cannot save " + dataType.getPrefix()
							+ " buffer because " +DataCollectorUtils.getStackTrackElement(ex),ex);
					retList.add(object);
				}
			}
		} finally {
			if (fileOutputStream != null) {
				fileOutputStream.flush();
				fileOutputStream.close();
			}
			if (file.getName().startsWith("location")) {
				SystemMonitor.getInstance().getMonitor(sensorMetaData.getId()).setCurrentFileSize(file.length());
			} 
			setArchiveFileNamesOnMonitor();
			lock.unlock();
		}
		return (retList);
	}

	private String getActiveFileName() {
		
		return sensorMetaData.getDataType().getPrefix()+"_"+ getNormalizedSensorId(sensorMetaData)+ "." + extension;
	}
	
	private String getNormalizedSensorId(SensorMetaData sensorMetaData)
	{
		StringBuilder sb=new StringBuilder();
		String sensorId=sensorMetaData.getId();
		for (int  ii=0;ii<sensorId.length();ii++){
			char ch=sensorId.charAt(ii);
			if ( Character.isLetterOrDigit(ch)){
				sb.append(ch);
			}
		}
		return sb.toString().trim();
	}

	private String getArchiveFileName() {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		return sensorMetaData.getDataType().getPrefix()+"_"+ getNormalizedSensorId(sensorMetaData) + ARCHIVE_STRING + dateFormat.format(new Date()) + "."
				+ extension;
	}

	private void setArchiveFileNamesOnMonitor() {
		StringBuilder sb = new StringBuilder();
		for (File file : filesDir.listFiles()) {
			if (file.getName().contains(ARCHIVE_STRING)) {
				sb.append(file.getName() + " " + file.length() + "\n");
			}
		}
		SystemMonitor.getInstance().setArchiveFiles(sb.toString());
	}

	public void updateTimeStamp() {
		File file=new File(getActiveFileName());
		file.setLastModified(System.currentTimeMillis());
	}

	public static long lastTimeStamp() {
		File mostRecentFile=null;
		for (File file: Environment.getExternalStorageDirectory().listFiles()){
			if ( file.getName().contains(ARCHIVE_STRING)|| (!file.getName().endsWith("obj"))){
				continue;
			}
			if ( (mostRecentFile == null)|| (file.lastModified()>mostRecentFile.lastModified())){
				 mostRecentFile=file;
			}
		}
		if ( mostRecentFile == null)
		{
			return 0;
		}else{
		return mostRecentFile.lastModified();
		}
	}

}
