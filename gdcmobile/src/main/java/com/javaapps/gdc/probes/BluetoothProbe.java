package com.javaapps.gdc.probes;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

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
import android.util.Log;

import com.javaapps.gdc.Constants;
import com.javaapps.gdc.pojos.SensorMetaData;
import com.javaapps.gdc.utils.BlueToothLEMetaDataRetriever;

public class BluetoothProbe extends Probe {
	private static final UUID uuid = UUID
			.fromString("0001101-0000-1000-8000-00805f9b34fb");
	private static final UUID CONFIG_DESCRIPTOR = UUID
			.fromString("00002902-0000-1000-8000-00805f9b34fb");

	private BluetoothDevice bluetoothDevice;

	public BluetoothProbe(SensorMetaData sensorMetaData,
			BluetoothDevice bluetoothDevice) {
		super(sensorMetaData);
		this.bluetoothDevice = bluetoothDevice;
	}

	public void collectData(Context context) {
		try {
			if (bluetoothDevice == null) {
				Log.e(Constants.GENERIC_COLLECTOR_TAG + "2",
						"Bluetooth device not found for " + sensorMetaData);
				return;
			}
			if (bluetoothDevice.getType() == BluetoothDevice.DEVICE_TYPE_LE) {
				collectLEBluetoothData(context, bluetoothDevice);
			} else {
				collectBluetoothData(bluetoothDevice);
			}

		} catch (Exception ex) {
			Log.e(Constants.GENERIC_COLLECTOR_TAG + "2",
					"Unable to create bluetooth socket because "
							+ ex.getMessage() + " " + ex.getCause(), ex);
		}
	}

	private void collectBluetoothData(BluetoothDevice bluetoothDevice)
			throws IOException {
		Log.i(Constants.GENERIC_COLLECTOR_TAG + "2", "Connecting to "
				+ bluetoothDevice + " with UUID " + uuid);
		BluetoothSocket bluetoothSocket = bluetoothDevice
				.createInsecureRfcommSocketToServiceRecord(uuid);
		if (bluetoothSocket != null) {
			Log.i(Constants.GENERIC_COLLECTOR_TAG + "2",
					"connecting to bluetooth socket ");
			bluetoothSocket.connect();
			Log.i(Constants.GENERIC_COLLECTOR_TAG + "2",
					"connected to bluetooth socket ");
		} else {
			Log.i(Constants.GENERIC_COLLECTOR_TAG + "2",
					"bluetooth socket is null");
		}
	}

	@Override
	public void stopCollectingData(Context context) {
		Log.i(Constants.GENERIC_COLLECTOR_TAG + "2",
				"disconnectingconnecting to bluetooth le device ");

	}

	private void collectLEBluetoothData(Context context,
			BluetoothDevice bluetoothDevice) throws IOException {
		BluetoothClass bluetoothClass = bluetoothDevice.getBluetoothClass();
		Log.i(Constants.GENERIC_COLLECTOR_TAG + "2",
				"connecting to bluetooth le device major/contents/device "
						+ bluetoothClass.getMajorDeviceClass() + ":"
						+ bluetoothClass.describeContents() + ":"
						+ bluetoothClass.getDeviceClass());
		BluetoothGatt gatt = bluetoothDevice.connectGatt(context, true,
				new GattCallback("SensorTag"));
		Log.i(Constants.GENERIC_COLLECTOR_TAG + "2", "connecting to device");
	}

	private class GattCallback extends BluetoothGattCallback {

		private BlueToothLEMetaDataFacade metaDataFacade;
		
		private final String sensorMetaDataFile;

		private GattCallback(String sensorMetaDataFile) {
				this.sensorMetaDataFile=sensorMetaDataFile;
		}

		private void enable(BluetoothGatt gatt, UUID serviceUUID,
				UUID enableUUID) {
			Log.i(Constants.GENERIC_COLLECTOR_TAG + "2", "Enabling "
					+ enableUUID);
			BluetoothGattCharacteristic characteristic = gatt.getService(
					serviceUUID).getCharacteristic(enableUUID);
			characteristic.setValue(new byte[] { 1 });
			if (!gatt.writeCharacteristic(characteristic)) {
				Log.i(Constants.GENERIC_COLLECTOR_TAG + "2",
						"Could not write characteristic "
								+ characteristic.getUuid());
			} else {
				Log.i(Constants.GENERIC_COLLECTOR_TAG + "2",
						"Could write characteristic "
								+ characteristic.getUuid() + " "
								+ characteristic.getValue()[0]
								+ " permissions "
								+ characteristic.getPermissions());

			}
		}

		private void setNotify(BluetoothGatt gatt, UUID serviceUUID,
				UUID characteristicUUID) {
			Log.i(Constants.GENERIC_COLLECTOR_TAG + "2", "Set notify "
					+ characteristicUUID);
			BluetoothGattCharacteristic characteristic = gatt.getService(
					serviceUUID)
					.getCharacteristic(characteristicUUID);
			for (BluetoothGattDescriptor descriptor : characteristic
					.getDescriptors()) {
				Log.i(Constants.GENERIC_COLLECTOR_TAG + "2", " descriptor "
						+ descriptor.getUuid());
			}
			gatt.setCharacteristicNotification(characteristic, true);
			BluetoothGattDescriptor desc = characteristic
					.getDescriptor(CONFIG_DESCRIPTOR);
			desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
			if (gatt.writeDescriptor(desc)) {
				Log.i(Constants.GENERIC_COLLECTOR_TAG + "2",
						"wrote to descriptor");
			} else {
				Log.i(Constants.GENERIC_COLLECTOR_TAG + "2",
						"did not write  to descriptor");
			}
		}

		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int connectionState) {
			Log.i(Constants.GENERIC_COLLECTOR_TAG + "2",
					"Connection State Change: " + status
							+ " -> connectionState  " + connectionState);
			if (status == BluetoothGatt.GATT_SUCCESS
					&& connectionState == BluetoothProfile.STATE_CONNECTED) {
				Log.i(Constants.GENERIC_COLLECTOR_TAG + "2", "device connected");
				gatt.discoverServices();
			} else if (status == BluetoothGatt.GATT_SUCCESS
					&& connectionState == BluetoothProfile.STATE_DISCONNECTED) {
				Log.i(Constants.GENERIC_COLLECTOR_TAG + "2",
						"device disconnected");
			} else if (status != BluetoothGatt.GATT_SUCCESS) {
				Log.i(Constants.GENERIC_COLLECTOR_TAG + "2",
						"connection failure");
				gatt.disconnect();
			}
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			Log.i(Constants.GENERIC_COLLECTOR_TAG + "2", "Value is "
					+new String(characteristic.getValue()));
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			BlueToothLEMetaData blueToothLEMetaData = BlueToothLEMetaDataRetriever
					.getBlueToothLEMetaData(sensorMetaDataFile);
			Log.i(Constants.GENERIC_COLLECTOR_TAG + "2",
					"retrieved blueToothCharacteristicMetaData "
							+ blueToothLEMetaData);
			metaDataFacade = new BlueToothLEMetaDataFacade(blueToothLEMetaData);
			Log.i(Constants.GENERIC_COLLECTOR_TAG + "2",
					"Services Discovered: status is " + status+" facade "+metaDataFacade.getBlueToothLEService());
			for (BlueToothLEService blueToothLEService : metaDataFacade
					.getBlueToothLEService()) {
				Log.i(Constants.GENERIC_COLLECTOR_TAG + "2",
						"Service discovered  " +  blueToothLEService);
				for (BlueToothLECharacteristic blueToothCharacteristic : blueToothLEService
						.getCharacteristicMap().values()) {
					Log.i(Constants.GENERIC_COLLECTOR_TAG + "2",
							"Enabling  " + blueToothCharacteristic.getCharacteristicName());
					enable(gatt, blueToothLEService.getServiceUUID(),
							blueToothCharacteristic
									.getEnableCharacteristicUUID());
				}
			}
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			Log.i(Constants.GENERIC_COLLECTOR_TAG + "2", "Read characterisitc"
					+ characteristic.getUuid());
		}

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			// After writing the enable flag, next we read the initial value
			Log.i(Constants.GENERIC_COLLECTOR_TAG + "2",
					"Writing characteristic with characteristic  "
							+ characteristic.getUuid() + " and status "
							+ status + " with value "
							+ characteristic.getValue()[0]);
			for (BlueToothLEService blueToothLEService : metaDataFacade
					.getBlueToothLEService()) {
				for (BlueToothLECharacteristic blueToothCharacteristic : blueToothLEService
						.getCharacteristicMap().values()) {
			setNotify(gatt,blueToothLEService.getServiceUUID(),blueToothCharacteristic.getCharacteristicUUID());
				}
			}
		}

		private void read(BluetoothGatt gatt, String serviceName,
				String characteristicName) {
			Log.i(Constants.GENERIC_COLLECTOR_TAG + "2", "reading "
					+ characteristicName);
			BlueToothLECharacteristic blueToothCharacteristicMetaData = metaDataFacade
					.getBlueToothLECharacteristic(serviceName,
							characteristicName);
			BluetoothGattCharacteristic characteristic = gatt.getService(
					metaDataFacade.getBlueToothLEServiceUUID(serviceName))
					.getCharacteristic(
							blueToothCharacteristicMetaData
									.getCharacteristicUUID());
			Log.i(Constants.GENERIC_COLLECTOR_TAG + "2", "value  is "
					+ characteristic.getValue());
		}

		@Override
		public void onDescriptorWrite(BluetoothGatt gatt,
				BluetoothGattDescriptor descriptor, int status) {
			Log.i(Constants.GENERIC_COLLECTOR_TAG + "2",
					"writing to descriptor with status " + status
							+ " descriptor " + descriptor.getUuid());

		}

		@Override
		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
			Log.i(Constants.GENERIC_COLLECTOR_TAG + "2", "Remote RSSI: " + rssi);
		}

		private String connectionState(int status) {
			switch (status) {
			case BluetoothProfile.STATE_CONNECTED:
				return "Connected";
			case BluetoothProfile.STATE_DISCONNECTED:
				return "Disconnected";
			case BluetoothProfile.STATE_CONNECTING:
				return "Connecting";
			case BluetoothProfile.STATE_DISCONNECTING:
				return "Disconnecting";
			default:
				return String.valueOf(status);
			}
		}
	}
}
