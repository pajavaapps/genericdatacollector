package com.javaapps.gdc.activities;

import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.ParcelUuid;
import android.util.Log;

import com.javaapps.gdc.Constants;
import com.javaapps.gdc.pojos.SensorMetaData;

public class BluetoothProbe {
	private final SensorMetaData sensorMetaData;
	private static final UUID uuid = UUID.fromString("0001101-0000-1000-8000-00805f9b34fb");

	public BluetoothProbe(SensorMetaData sensorMetaData) {
		super();
		this.sensorMetaData = sensorMetaData;
	}
	
	public void collectData(BluetoothDevice bluetoothDevice){
		try
		{
			Log.i(Constants.GENERIC_COLLECTOR_TAG,"Connecting to "+bluetoothDevice+ " with UUID "+uuid);
			BluetoothSocket  bluetoothSocket=bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);
			 if ( bluetoothSocket != null ){
				 Log.i(Constants.GENERIC_COLLECTOR_TAG,"connecting to bluetooth socket");
				 bluetoothSocket.connect();
				 
			 }else{
				 Log.i(Constants.GENERIC_COLLECTOR_TAG,"bluetooth socket is null");
			 }
		}catch(Exception ex){
			Log.e(Constants.GENERIC_COLLECTOR_TAG,"Unable to create bluetooth socket because "+ex.getMessage(),ex);
		}
	}

}
