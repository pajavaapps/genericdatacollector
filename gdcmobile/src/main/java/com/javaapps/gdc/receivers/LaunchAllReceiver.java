package com.javaapps.gdc.receivers;

import java.io.File;
import java.io.IOException;

import com.javaapps.gdc.Constants;
import com.javaapps.gdc.db.DBAdapter;
import com.javaapps.gdc.model.Config;
import com.javaapps.gdc.utils.DataCollectorUtils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings.Secure;
import android.util.Log;



public class LaunchAllReceiver extends BroadcastReceiver {

	private static String LOCATION_DATA_INTENT = "locationDataIntent";
	private static String GFORCE_DATA_INTENT = "gforceDataIntent";
	private static final long TEN_MINUTES = 1000 * 60 * 10;
	private Config config = Config.getInstance();

	@Override
	public void onReceive(Context context, Intent i) {
		Log.i(Constants.GENERIC_COLLECTOR_TAG,
				"LaunchAllReceiver received intent " + i.getAction());
		try {
			DBAdapter dbAdapter = new DBAdapter(context);
			dbAdapter.open();
			
			Config.getInstance().setVersion(android.os.Build.VERSION.SDK_INT);
			String deviceId = Secure.getString(context.getContentResolver(),
					Secure.ANDROID_ID);
			config.setDeviceId(deviceId);
			dbAdapter.close();

			scheduleFileUploads(context);
		} catch (Exception ex) {
			Log.e(Constants.GENERIC_COLLECTOR_TAG,
					"Could not find custom identifier in DB becuase "
							+ DataCollectorUtils.getStackTrackElement(ex));
		}
	}



	private void scheduleFileUploads(Context context) {
		try {
			Log.i(Constants.GENERIC_COLLECTOR_TAG, "scheduling file uploads");
			AlarmManager mgr = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			Intent i = new Intent(LOCATION_DATA_INTENT);
			PendingIntent locationDataIntent = PendingIntent.getBroadcast(
					context, 0, i, 0);
			mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 0,
					config.getLocationUploadPeriod(), locationDataIntent);
			Intent gForceIntent = new Intent(GFORCE_DATA_INTENT);
			PendingIntent pendingGForceIntent = PendingIntent.getBroadcast(
					context, 0, gForceIntent, 0);
			mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 0,
					config.getGforceUploadPeriod(), pendingGForceIntent);
			Log.i(Constants.GENERIC_COLLECTOR_TAG, "fileuploads scheduled");
		} catch (Throwable ex) {
			Log.e(Constants.GENERIC_COLLECTOR_TAG,
					"Could not schedule file uploads because "
							+ DataCollectorUtils.getStackTrackElement(ex));
		}
	}


}
