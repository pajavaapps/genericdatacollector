package com.javaapps.gdc.receivers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.javaapps.gdc.Constants;
import com.javaapps.gdc.db.DBAdapter;
import com.javaapps.gdc.pojos.SensorMetaData;
import com.javaapps.gdc.probes.BluetoothProbe;
import com.javaapps.gdc.probes.GForceProbe;
import com.javaapps.gdc.probes.GPSProbe;
import com.javaapps.gdc.probes.Probe;
import com.javaapps.gdc.types.DataType;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DataCollectorReceiver extends BroadcastReceiver {

	public static final String COLLECT_GENERIC_DATA = "collectGenericData";

	private static Map<String, Probe> probeMap = new HashMap<String, Probe>();
	
	public static void removeProbe(String sensorMetaDataId){
		probeMap.remove(sensorMetaDataId);
	}
	
	public void collectData(Context context) {
		Log.i(Constants.GENERIC_COLLECTOR_TAG2, "Collecting data");
		DBAdapter dbAdapter = new DBAdapter(context);
		dbAdapter.open();
		List<SensorMetaData> sensorMetaDataList = dbAdapter
				.getAllSensorMetaData();

		for (SensorMetaData sensorMetaData : sensorMetaDataList) {
			if (sensorMetaData.getDataType() == DataType.BLUETOOTH_DATA) {
				collectBluetoothData(context, sensorMetaData);
			} else if (sensorMetaData.getDataType() == DataType.GPS) {
				collectGPSData(context, sensorMetaData);
			} else if (sensorMetaData.getDataType() == DataType.GFORCE) {
				collectGForceData(context, sensorMetaData);
			}
		}
		dbAdapter.close();
	}

	private void collectGPSData(Context context, SensorMetaData sensorMetaData) {
		Log.i(Constants.GENERIC_COLLECTOR_TAG, "Collecting data for "
				+ sensorMetaData);
		Probe probe = probeMap.get(sensorMetaData.getId());
		if (probe == null) {
			probe = new GPSProbe(sensorMetaData);
			probeMap.put(sensorMetaData.getId(), probe);
		}
		if (sensorMetaData.getActive().equalsIgnoreCase("Y")) {
			probe.collectData(context);
		} else {
			probe.stopCollectingData(context);
		}
	}

	private void collectGForceData(Context context,
			SensorMetaData sensorMetaData) {
		Log.i(Constants.GENERIC_COLLECTOR_TAG, "Collecting data for "
				+ sensorMetaData);
		Probe probe = probeMap.get(sensorMetaData.getId());
		if (probe == null) {
			probe = new GForceProbe(sensorMetaData);
			probeMap.put(sensorMetaData.getId(), probe);
		}
		if (sensorMetaData.getActive().equalsIgnoreCase("Y")) {
			probe.collectData(context);
		} else {
			probe.stopCollectingData(context);
		}
	}

	private void collectBluetoothData(Context context,
			SensorMetaData sensorMetaData) {
		try {
			Log.i(Constants.GENERIC_COLLECTOR_TAG, "Collecting data for "
					+ sensorMetaData);
			BluetoothAdapter bluetoothAdapter = BluetoothAdapter
					.getDefaultAdapter();
			if (bluetoothAdapter == null) {
				Log.i(Constants.GENERIC_COLLECTOR_TAG,
						"blue tooth adapter is null, cant connect to blue tooth devices");
				return;
			}
			BluetoothDevice device = bluetoothAdapter
					.getRemoteDevice(sensorMetaData.getId());
			Probe probe = probeMap.get(sensorMetaData.getId());
			if (probe == null) {
				probe = new BluetoothProbe(sensorMetaData, device);
				probeMap.put(sensorMetaData.getId(), probe);
			}
			if (sensorMetaData.getActive().equalsIgnoreCase("Y")) {
				probe.collectData(context);
			} else {
				probe.stopCollectingData(context);
			}
		} catch (Exception ex) {
			Log.e(Constants.GENERIC_COLLECTOR_TAG,
					"Error connecting to bluetooth device " + ex.getMessage());
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		collectData(context);
	}
}
