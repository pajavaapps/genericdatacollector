package com.javaapps.gdc.probes;

import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;

import com.javaapps.gdc.Constants;
import com.javaapps.gdc.pojos.SensorMetaData;

public class BluetoothProbe extends Probe {
	private static final UUID uuid = UUID.fromString("0001101-0000-1000-8000-00805f9b34fb");
   
	private BluetoothDevice bluetoothDevice;
	
	public BluetoothProbe(SensorMetaData sensorMetaData, BluetoothDevice bluetoothDevice) {
		super( sensorMetaData);
		this.bluetoothDevice=bluetoothDevice;
	}
	
	public void collectData(Context context){
		try
		{
			if ( bluetoothDevice== null){
				Log.e(Constants.GENERIC_COLLECTOR_TAG,"Bluetooth device not found for "+sensorMetaData);
				return;
			}
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

	@Override
	public void stopCollectingData(Context context) {
		// TODO Auto-generated method stub
		
	}

}
