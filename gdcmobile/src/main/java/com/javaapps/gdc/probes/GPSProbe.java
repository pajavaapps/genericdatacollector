package com.javaapps.gdc.probes;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.javaapps.gdc.types.DataType;
import com.javaapps.gdc.Constants;
import com.javaapps.gdc.factories.GenericDataFactory;
import com.javaapps.gdc.io.DataBuffer;
import com.javaapps.gdc.model.GenericData;
import com.javaapps.gdc.pojos.SensorMetaData;

public class GPSProbe extends Probe implements LocationListener {

	private final static double GEO_VARIANCE = 0.000002;
	private final static long MILLI_VARIANCE = 75;// milli seconds

	private static long lastDate = 0;
	private static long lastSampleDate = 0;
	private static double lastLatitude = 0;
	private static double lastLongitude = 0;

	public GPSProbe(SensorMetaData sensorMetaData) {
		super(sensorMetaData);
	}

	@Override
	public void collectData(Context context) {
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				1000, 10, this);
		Log.i(Constants.GENERIC_COLLECTOR_TAG, "LocationPolling scheduled");
	}

	public void onLocationChanged(Location location) {
		try {
			Log.d(Constants.GENERIC_COLLECTOR_TAG, "logging data point");
			long minimumLoggingIntervals = sensorMetaData.getSamplingPeriod();
			if (System.currentTimeMillis() - lastDate >= minimumLoggingIntervals) {

				if (!sampleDateHasChanged(location)) {
					return;
				}
				if (!locationHasChanged(location)) {
					return;
				}
				lastSampleDate = location.getTime();
				lastLatitude = location.getLatitude();
				lastLongitude = location.getLongitude();
				lastDate = System.currentTimeMillis();
				GenericData genericData = GenericDataFactory
						.createGenericData(location);
				DataBuffer.getInstance(sensorMetaData).logData(genericData);
				Log.d(Constants.GENERIC_COLLECTOR_TAG, "logged data point");
			}
		} catch (Throwable ex) {
			Log.e(Constants.GENERIC_COLLECTOR_TAG,
					"Unable to log location because " + ex.getMessage(), ex);
		}

	}

	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}

	private boolean locationHasChanged(Location location) {
		boolean retValue = Math.abs(location.getLatitude() - lastLatitude) > GEO_VARIANCE
				|| Math.abs(location.getLongitude() - lastLongitude) > GEO_VARIANCE;
		return true;
	}

	private boolean sampleDateHasChanged(Location location) {
		boolean retValue = Math.abs(location.getTime() - lastSampleDate) > MILLI_VARIANCE;
		return true;
	}

	@Override
	public void stopCollectingData(Context context) {
		try {
		   	Log.i(Constants.GENERIC_COLLECTOR_TAG, "unregistering gps probe");
			LocationManager locationManager = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);
			locationManager.removeUpdates(this);
		} catch (Exception ex) {
			Log.e(Constants.GENERIC_COLLECTOR_TAG,
					"Unable to unregister gps probe because " + ex.getMessage());
		}

	}
}
