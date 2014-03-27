package com.javaapps.gdc.probes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.javaapps.gdc.Constants;
import com.javaapps.gdc.activities.SensorConfigurationActivity;
import com.javaapps.gdc.pojos.SensorMetaData;
import com.javaapps.gdc.pojos.SensorMetaData.Service;
import com.javaapps.gdc.probes.BlueToothLEMetaDataFacade.Duple;
import com.javaapps.gdc.utils.BlueToothLEMetaDataRetriever;

public class BluetoothProbe extends Probe {
	private static final UUID uuid = UUID
			.fromString("0001101-0000-1000-8000-00805f9b34fb");
	private static final UUID CONFIG_DESCRIPTOR = UUID
			.fromString("00002902-0000-1000-8000-00805f9b34fb");

	public static final String BLUE_TOOTH_META_DATA = "bluetoothMetaData";
	
	private BluetoothDevice bluetoothDevice;

	private Context context;
	
	private Map<UUID,Service> serviceActivationMap=new HashMap<UUID,Service>();
	
	public BluetoothProbe(SensorMetaData sensorMetaData,
			BluetoothDevice bluetoothDevice) {
		super(sensorMetaData);
		for ( Service service:sensorMetaData.getServiceList()){
			serviceActivationMap.put(service.getServiceUUID(),service);
		}
		this.bluetoothDevice = bluetoothDevice;
	}

	public void collectData(Context context) {
		try {
			if (bluetoothDevice == null) {
				Log.e(Constants.GENERIC_COLLECTOR_TAG2,
						"Bluetooth device not found for " + sensorMetaData);
				return;
			}
			this.context=context;
			Log.i(Constants.GENERIC_COLLECTOR_TAG2,
					"Initializing bluetooth device for " + sensorMetaData);
			if (bluetoothDevice.getType() == BluetoothDevice.DEVICE_TYPE_LE) {
				collectLEBluetoothData(context, bluetoothDevice);
			} else {
				collectBluetoothData(bluetoothDevice);
			}

		} catch (Exception ex) {
			Log.e(Constants.GENERIC_COLLECTOR_TAG2,
					"Unable to create bluetooth socket because "
							+ ex.getMessage() + " " + ex.getCause(), ex);
		}
	}

	private void collectBluetoothData(BluetoothDevice bluetoothDevice)
			throws IOException {
		Log.i(Constants.GENERIC_COLLECTOR_TAG2, "Connecting to "
				+ bluetoothDevice + " with UUID " + uuid);
		BluetoothSocket bluetoothSocket = bluetoothDevice
				.createInsecureRfcommSocketToServiceRecord(uuid);
		if (bluetoothSocket != null) {
			Log.i(Constants.GENERIC_COLLECTOR_TAG2,
					"connecting to bluetooth socket ");
			bluetoothSocket.connect();
			Log.i(Constants.GENERIC_COLLECTOR_TAG2,
					"connected to bluetooth socket ");
		} else {
			Log.i(Constants.GENERIC_COLLECTOR_TAG2, "bluetooth socket is null");
		}
	}

	@Override
	public void stopCollectingData(Context context) {
		Log.i(Constants.GENERIC_COLLECTOR_TAG2,
				"disconnectingconnecting to bluetooth le device ");

	}

	private void collectLEBluetoothData(Context context,
			BluetoothDevice bluetoothDevice) throws IOException {
		BluetoothClass bluetoothClass = bluetoothDevice.getBluetoothClass();
		Log.i(Constants.GENERIC_COLLECTOR_TAG2,
				"connecting to bluetooth le device major/contents/device "
						+ bluetoothClass.getMajorDeviceClass() + ":"
						+ bluetoothClass.describeContents() + ":"
						+ bluetoothClass.getDeviceClass());
		BluetoothGatt gatt = bluetoothDevice.connectGatt(context, true,
				new GattCallback("SensorTag"));
		Log.i(Constants.GENERIC_COLLECTOR_TAG2, "connecting to device");
	}

	private class GattCallback extends BluetoothGattCallback {


		private BlueToothLEMetaDataFacade metaDataFacade;

		private final String sensorMetaDataFile;

		private CharacteristicStateMachine characteristicStateMachine;
		
		private BluetoothBufferAdapter bluetoothBufferAdapter ; 

		private GattCallback(String sensorMetaDataFile) {
			this.sensorMetaDataFile = sensorMetaDataFile;
			bluetoothBufferAdapter=new BluetoothBufferAdapter(BluetoothProbe.this.sensorMetaData,sensorMetaDataFile);
		}

		private void enable(BlueToothLECharacteristic blueToothCharacteristic,
				BluetoothGatt gatt, UUID serviceUUID, UUID enableUUID) {
			Log.i(Constants.GENERIC_COLLECTOR_TAG2, "Enabling " + blueToothCharacteristic.getCharacteristicName()+" "+blueToothCharacteristic.getCharacteristicUUID());
			BluetoothGattCharacteristic characteristic = gatt.getService(
					serviceUUID).getCharacteristic(enableUUID);
			String labelKey=blueToothCharacteristic.getEnableCharacteristicUUID().toString();
			if ( BluetoothProbe.this.serviceActivationMap.get(serviceUUID).isActive())
			{		
			characteristic.setValue(new byte[] { (byte) blueToothCharacteristic
					.getEnableCharacteristicValue() });
			    labelKey=labelKey+"_"+blueToothCharacteristic.getEnableCharacteristicValue();
			}else{
				characteristic.setValue(new byte[] { (byte) blueToothCharacteristic
						.getDisableCharacteristicValue() });				
			}
			Log.i(Constants.GENERIC_COLLECTOR_TAG2, "Label key is "+labelKey);
			if (!gatt.writeCharacteristic(characteristic)) {
				Log.i(Constants.GENERIC_COLLECTOR_TAG2,
						"Could not write characteristic "
								+ characteristic.getUuid());
			} else {
				Log.i(Constants.GENERIC_COLLECTOR_TAG2,
						"Could write characteristic "
								+ this.metaDataFacade.getLabel(labelKey) + characteristic.getUuid()
								+ " " + characteristic.getValue()[0]
								+ " permissions "
								+ characteristic.getPermissions());

			}
		}

		private void setNotify(BluetoothGatt gatt, Duple duple) {
			Log.i(Constants.GENERIC_COLLECTOR_TAG2,
					"Beginning to set notify  with duple " + duple);
			try {
				BluetoothGattService service = gatt.getService(duple
						.getServiceUUID());
				BluetoothGattCharacteristic characteristic = service
						.getCharacteristic(duple.getCharUUID());
				gatt.setCharacteristicNotification(characteristic, true);
				BluetoothGattDescriptor desc = characteristic
						.getDescriptor(CONFIG_DESCRIPTOR);
				desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
				
				if (gatt.writeDescriptor(desc)) {
					Log.i(Constants.GENERIC_COLLECTOR_TAG2,
							"Characteristic "
									+ this.metaDataFacade
											.getLabel(characteristic.getUuid().toString())+" with UUID "+characteristic.getUuid()+" "
									+ " set to notify");
				} else {
					Log.i(Constants.GENERIC_COLLECTOR_TAG2,
							"Characteristic "
									+ this.metaDataFacade
											.getLabel(characteristic.getUuid().toString())
									+ "could not be set to notify");
				}
				 
			} catch (Exception ex) {
				Log.e(Constants.GENERIC_COLLECTOR_TAG2,
						"Characteristic "
								+ "could not be set to notify because "
								+ ex.getMessage(), ex);
			}
		}

		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int connectionState) {
			Log.i(Constants.GENERIC_COLLECTOR_TAG2, "Connection State Change: "
					+ status + " -> connectionState  " + connectionState);
			if (status == BluetoothGatt.GATT_SUCCESS
					&& connectionState == BluetoothProfile.STATE_CONNECTED) {
				Log.i(Constants.GENERIC_COLLECTOR_TAG2, "device connected");
				gatt.discoverServices();
			} else if (status == BluetoothGatt.GATT_SUCCESS
					&& connectionState == BluetoothProfile.STATE_DISCONNECTED) {
				Log.i(Constants.GENERIC_COLLECTOR_TAG2, "device disconnected");
			} else if (status != BluetoothGatt.GATT_SUCCESS) {
				Log.i(Constants.GENERIC_COLLECTOR_TAG2, "connection failure with status "+status);
				gatt.disconnect();
			}
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			String labelKey=characteristic.getUuid().toString();
			String serviceName= metaDataFacade.getServiceName(labelKey);
			byte data[]=characteristic.getValue();
			Log.i(Constants.GENERIC_COLLECTOR_TAG2, "Characteristic onChanged "
					+ this.metaDataFacade.getLabel(characteristic.getUuid().toString())
					+ " Value is " + new String(data));
			bluetoothBufferAdapter.getBluetoothDataBuffer(serviceName).addDataPoint(System.currentTimeMillis(), data);
		}

		private void activateNextCharacteristic(BluetoothGatt gatt)
		{
			BlueToothLECharacteristic blueToothCharacteristic = characteristicStateMachine
					.next();
			if (blueToothCharacteristic != null) {
				Duple duple = metaDataFacade.getDuple(blueToothCharacteristic
						.getEnableCharacteristicUUID().toString());
				enable(blueToothCharacteristic, gatt, duple.getServiceUUID(),
						blueToothCharacteristic.getEnableCharacteristicUUID());
			}
		}
		
		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			BlueToothLEMetaData blueToothLEMetaData = BlueToothLEMetaDataRetriever
					.getBlueToothLEMetaData(sensorMetaDataFile);
			Log.i(Constants.GENERIC_COLLECTOR_TAG3,"Services discovered");
			if ( sensorMetaData.getServiceList() == null || sensorMetaData.getServiceList().size() != blueToothLEMetaData.getServiceMetaDataMap().size()){
			    try
			    {
				Log.i(Constants.GENERIC_COLLECTOR_TAG3,"No services in meta data, rerouting to sensor activity.  SensorMetaData size "
			    +sensorMetaData.getServiceList().size()+" meta data size is "+blueToothLEMetaData.getServiceMetaDataMap().size());
				Context context=BluetoothProbe.this.context;
				Intent intent = new Intent(context, SensorConfigurationActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra(BLUE_TOOTH_META_DATA, blueToothLEMetaData);
				intent.putExtra(SensorConfigurationActivity.SENSOR_META_DATA_KEY,
						sensorMetaData);
				context.startActivity(intent);
				Log.i(Constants.GENERIC_COLLECTOR_TAG3,"intent sent to sensor activity");
			    }catch(Exception ex){
			    	Log.e(Constants.GENERIC_COLLECTOR_TAG3,"Unable to send intent to sensor activity because "+ex.getMessage(),ex);
			    }
				return;
			}
			Log.i(Constants.GENERIC_COLLECTOR_TAG3,
					"retrieved blueToothCharacteristicMetaData "
							+ blueToothLEMetaData);
			metaDataFacade = new BlueToothLEMetaDataFacade(blueToothLEMetaData);
				characteristicStateMachine = new CharacteristicStateMachine(
					metaDataFacade);
		    activateNextCharacteristic(gatt);
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			Log.i(Constants.GENERIC_COLLECTOR_TAG2, "Characteristic onRead "
					+ this.metaDataFacade.getLabel(characteristic.getUuid().toString())
					+ " Value is " + new String(characteristic.getValue()));
			Duple duple = metaDataFacade.getDuple(characteristic.getUuid().toString());
			BlueToothLECharacteristic blueToothCharacteristic = characteristicStateMachine.current();
			if (blueToothCharacteristic.isCalibration()){
				String labelKey=characteristic.getUuid().toString();
				String serviceName= metaDataFacade.getServiceName(labelKey);
				bluetoothBufferAdapter.getBluetoothDataBuffer(serviceName).setCalibration(characteristic.getValue());
			}
			setNotify(gatt, duple);
		}

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			// After writing the enable flag, next we read the initial value
			short value=characteristic.getValue()[0];
			String labelKey=characteristic.getUuid().toString();
			if ( value != 0 ){
				labelKey=labelKey+"_"+value;
			}
			Log.i(Constants.GENERIC_COLLECTOR_TAG2,
					"Received characteristic write  "
							+ this.metaDataFacade.getLabel(labelKey) + " and status " + status
							+ " with value " + characteristic.getValue()[0]);
			Duple duple = metaDataFacade.getDuple(labelKey);
			BluetoothGattService service = gatt.getService(duple
					.getServiceUUID());
			if (!BluetoothProbe.this.serviceActivationMap.get(duple.getServiceUUID()).isActive() ){
				activateNextCharacteristic(gatt);
				return;
			}
			BluetoothGattCharacteristic dataCharacteristic = service
					.getCharacteristic(duple.getCharUUID());
			gatt.readCharacteristic(dataCharacteristic);
		}

	
		@Override
		public void onDescriptorWrite(BluetoothGatt gatt,
				BluetoothGattDescriptor descriptor, int status) {
			Log.i(Constants.GENERIC_COLLECTOR_TAG2,
					"writing to descriptor with status " + status
							+ " descriptor " + descriptor.getUuid());
		    activateNextCharacteristic(gatt);
		}

		@Override
		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
			Log.i(Constants.GENERIC_COLLECTOR_TAG2, "Remote RSSI: " + rssi);
		}

	
		private class CharacteristicStateMachine {
			private int index = -1;
			private List<BlueToothLECharacteristic> characteristicList = new ArrayList<BlueToothLECharacteristic>();

			public BlueToothLECharacteristic current() {
				if (index<characteristicList.size()){
				   return 	characteristicList.get(index);
				}else{
					return null;
				}				
			}
			
			public BlueToothLECharacteristic next() {
				BlueToothLECharacteristic retChar=null;
				index++;
				if (characteristicList.size() > index) {
					 retChar=characteristicList.get(index);
				} 
					return retChar;
			}

			private CharacteristicStateMachine(
					BlueToothLEMetaDataFacade metaDataFacade) {
				for (BlueToothLEService blueToothLEService : metaDataFacade
						.getBlueToothLEService()) {
					for (BlueToothLECharacteristic blueToothCharacteristic : blueToothLEService
							.getCharacteristicMap().values()) {
						Log.i(Constants.GENERIC_COLLECTOR_TAG2,
								"Adding characteristic  "+blueToothCharacteristic.getCharacteristicName());
						characteristicList.add(blueToothCharacteristic);
					}
				}
			}
		}
	}

}
