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
import com.javaapps.gdc.model.Config;
import com.javaapps.gdc.model.Monitor;
import com.javaapps.gdc.model.SystemMonitor;
import com.javaapps.gdc.types.DataType;
import com.javaapps.gdc.utils.DataCollectorUtils;

public class DataFile<T> {

	public final static String ARCHIVE_STRING = "_archive_";

	private final ReentrantLock lock = new ReentrantLock();

	private File filesDir;
	private DataType dataType;
	private String extension;

	public DataFile(DataType dataType, String extension)
			throws FileNotFoundException, IOException {
		this.filesDir = Config.getInstance().getFilesDir();
		this.dataType = dataType;
		this.extension = extension;
	}

	public void deleteFiles() {
		for (File file : filesDir.listFiles()) {
			if (file.getName().startsWith(dataType.getPrefix() + ARCHIVE_STRING)) {
				file.delete();
			}
		}
	}

	public List<T> writeToObjectFile(List<T> objectList) throws IOException {
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
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(file,true);
			boolean errorThrown = false;
			for (T object : objectList) {
				try {
					if (errorThrown) {
						retList.add(object);
					} else {
						fileOutputStream.write(((CsvWriter)object).toCSV().getBytes());
					}
				} catch (Exception ex) {
					errorThrown = true;
					Log.e(Constants.GENERIC_COLLECTOR_TAG, "cannot save " + dataType.getPrefix()
							+ " buffer because " +DataCollectorUtils.getStackTrackElement(ex));
					retList.add(object);
				}
			}
		} finally {
			if (fileOutputStream != null) {
				fileOutputStream.flush();
				fileOutputStream.close();
			}
			if (file.getName().startsWith("location")) {
				SystemMonitor.getInstance().getMonitor(dataType).setCurrentFileSize(file.length());
			} 
			setArchiveFileNamesOnMonitor();
			lock.unlock();
		}
		return (retList);
	}

	private String getActiveFileName() {
		return dataType.getPrefix() + "." + extension;
	}

	private String getArchiveFileName() {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		return dataType.getPrefix() + ARCHIVE_STRING + dateFormat.format(new Date()) + "."
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
