package com.javaapps.gdc.receivers;

import java.util.List;

import com.javaapps.gdc.Constants;
import com.javaapps.gdc.activities.BluetoothProbe;
import com.javaapps.gdc.db.DBAdapter;
import com.javaapps.gdc.pojos.SensorMetaData;
import com.javaapps.gdc.types.DataType;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;

public class DataCollector {
	public void collectData(Context context) {
		Log.i(Constants.GENERIC_COLLECTOR_TAG,"Collecting data");
		DBAdapter dbAdapter = new DBAdapter(context);
		dbAdapter.open();
		List<SensorMetaData> sensorMetaDataList = dbAdapter
				.getActiveSensorMetaData();

		for (SensorMetaData sensorMetaData : sensorMetaDataList) {
			if (sensorMetaData.getDataType() == DataType.GENERIC) {
				collectGenericData(sensorMetaData);
			} else if (sensorMetaData.getDataType() == DataType.GPS) {

			} else if (sensorMetaData.getDataType() == DataType.GFORCE) {

			}
		}
		dbAdapter.close();
	}

	private void collectGenericData(SensorMetaData sensorMetaData) {
		try {
			BluetoothAdapter bluetoothAdapter = BluetoothAdapter
					.getDefaultAdapter();
			if (bluetoothAdapter == null) {
				Log.i(Constants.GENERIC_COLLECTOR_TAG,
						"blue tooth adapter is null, cant connect to blue tooth devices");
				return;
			}
			BluetoothProbe probe = new BluetoothProbe(sensorMetaData);
			BluetoothDevice device = bluetoothAdapter
					.getRemoteDevice(sensorMetaData.getId());
			if (device != null) {
				probe.collectData(device);
			} else {
				Log.i(Constants.GENERIC_COLLECTOR_TAG, "Bluetooth device "
						+ sensorMetaData.getId() + " is not found");
			}
		} catch (Exception ex) {
			Log.e(Constants.GENERIC_COLLECTOR_TAG,
					"Error connecting to bluetooth device " + ex.getMessage());
		}
	}
}
