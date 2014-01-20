package com.javaapps.gdc.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings.Secure;
import android.util.Log;

import com.javaapps.gdc.Constants;
import com.javaapps.gdc.db.DBAdapter;
import com.javaapps.gdc.model.Config;
import com.javaapps.gdc.pojos.DeviceMetaData;
import com.javaapps.gdc.utils.DataCollectorUtils;



public class LaunchAllReceiver extends BroadcastReceiver {

	private static String GENERIC_DATA_INTENT = "genericDataIntent";
	private static final long TEN_MINUTES = 1000 * 60 * 10;
	private Config config = Config.getInstance();
	private DataCollector dataCollector=new DataCollector();

	@Override
	public void onReceive(Context context, Intent i) {
		Log.i(Constants.GENERIC_COLLECTOR_TAG,
				"LaunchAllReceiver received intent " + i.getAction());
		try {
			DBAdapter dbAdapter = new DBAdapter(context);
			dbAdapter.open();
			DeviceMetaData deviceMetaData=dbAdapter.getDeviceMetaData();
			/*Config.getInstance().setVersion(android.os.Build.VERSION.SDK_INT);
			String deviceId = Secure.getString(context.getContentResolver(),
					Secure.ANDROID_ID);
			config.setDeviceId(deviceMetaData.getDeviceId());*/
			dbAdapter.close();
			dataCollector.collectData(context);
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
			Intent i = new Intent(GENERIC_DATA_INTENT);
			PendingIntent locationDataIntent = PendingIntent.getBroadcast(
					context, 0, i, 0);
			mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 0,
					config.getLocationUploadPeriod(), locationDataIntent);
			Log.i(Constants.GENERIC_COLLECTOR_TAG, "fileuploads scheduled");
		} catch (Throwable ex) {
			Log.e(Constants.GENERIC_COLLECTOR_TAG,
					"Could not schedule file uploads because "
							+ DataCollectorUtils.getStackTrackElement(ex));
		}
	}


}
