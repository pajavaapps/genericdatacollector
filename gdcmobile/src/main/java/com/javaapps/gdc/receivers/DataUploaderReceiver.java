package com.javaapps.gdc.receivers;



import com.javaapps.gdc.Constants;
import com.javaapps.gdc.model.SystemMonitor;
import com.javaapps.gdc.uploader.DataUploader;
import com.javaapps.gdc.utils.DataCollectorUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

public class DataUploaderReceiver extends BroadcastReceiver implements Runnable {
     private Context context;

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(Constants.GENERIC_COLLECTOR_TAG, "beginning upload");
		this.context=context;
		Thread thread=new Thread(this);
		thread.start();
	}

	public void run() {
		try {
			WifiManager wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			if (!wifiManager.isWifiEnabled()) {
				Log.i(Constants.GENERIC_COLLECTOR_TAG,
						"cannot upload gforce data because wifi is not enabled");
				SystemMonitor.getInstance().setWifiStatus("wifi not enabled");
				SystemMonitor.getInstance().setUploadStatusCode(Constants.WIFI_NOT_ENABLED);
				return;
			}
			SystemMonitor.getInstance().setWifiStatus("pinging wifi connection");
			if (!wifiManager.pingSupplicant()) {
				Log.i(Constants.GENERIC_COLLECTOR_TAG,
						"cannot upload gforce data because could not could to wifi");
				SystemMonitor.getInstance().setWifiStatus("could not ping backend server");
				SystemMonitor.getInstance().setLastUploadStatusCode(Constants.COULD_NOT_GET_WIFI_CONNECTION);
				return;
			}
			SystemMonitor.getInstance().setWifiStatus("Wifi OK");
			DataUploader dataUploade=new DataUploader();
			Log.i(Constants.GENERIC_COLLECTOR_TAG, "uploaded gforce file");
		} catch (Exception ex) {
			Log.e(Constants.GENERIC_COLLECTOR_TAG, "unable to retrieve GForce data file because "
					+ DataCollectorUtils.getStackTrackElement(ex));
		}
		
	}

}
