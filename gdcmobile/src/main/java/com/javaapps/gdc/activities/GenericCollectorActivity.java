package com.javaapps.gdc.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.javaapps.gdc.Constants;
import com.javaapps.gdc.R;
import com.javaapps.gdc.db.DBAdapter;
import com.javaapps.gdc.exceptions.BlueToothNotSupportedException;
import com.javaapps.gdc.pojos.*;
import com.javaapps.gdc.probes.AdRecord;
import com.javaapps.gdc.receivers.DataCollectorReceiver;
import com.javaapps.gdc.types.DataType;

import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class GenericCollectorActivity extends ListActivity implements
		LeScanCallback {

	private final static int REQUEST_ENABLE_BT = 1;
	private static final String LAUNCH_COLLECTOR = "launchCollector";
	private DBAdapter dbAdapter;
	private List<SensorMetaData> sensorMetaDataList = new ArrayList<SensorMetaData>();

	private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			try {
				String action = intent.getAction();
				Log.i(Constants.GENERIC_COLLECTOR_TAG,
						"Received bluetooth intent");
				// When discovery finds a device
				if (BluetoothDevice.ACTION_FOUND.equals(action)) {
					Log.i(Constants.GENERIC_COLLECTOR_TAG,
							"Received bluetooth found intent");
					// Get the BluetoothDevice object from the Intent
					BluetoothDevice device = intent
							.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					// Add the name and address to an array adapter to show in a
					// ListView
					GenericCollectorActivity.this.addBluetoothDevice(device);
					Log.i(Constants.GENERIC_COLLECTOR_TAG,
							"Getting bonded devices");
				}
			} catch (Exception ex) {
				Log.e(Constants.GENERIC_COLLECTOR_TAG,
						"Error receiving bluetooth intent " + ex.getMessage(),
						ex);
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			if (!checkForConfiguration()) {
				Intent intent = new Intent(this, ConfigurationActivity.class);
				startActivity(intent);
				return;
			}
			Intent i = new Intent();
			i.setAction(LAUNCH_COLLECTOR);
			this.sendBroadcast(i);
			IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
			registerReceiver(bluetoothReceiver, filter);
			setSensorArrayAdapter();
		} catch (Exception ex) {
			String errorStr = "Error initializing GenericCollectorActivity because "
					+ ex.getMessage();
			Log.e(Constants.GENERIC_COLLECTOR_TAG, errorStr);
		}
	}

	private boolean checkForConfiguration() {
		DBAdapter dbAdapter = new DBAdapter(this);
		try {
			dbAdapter.open();
			DeviceMetaData deviceMetaData = dbAdapter.getDeviceMetaData();
			Log.i(Constants.GENERIC_COLLECTOR_TAG,
					"Retrieved device meta data " + deviceMetaData);
			return (deviceMetaData != null);
		} catch (Exception ex) {
			String errorStr = "Cannot retrieve device meta data because "
					+ ex.getMessage();
			Toast.makeText(this, errorStr, Toast.LENGTH_LONG);
			Log.e(Constants.GENERIC_COLLECTOR_TAG, errorStr, ex);
			return false;
		} finally {
			dbAdapter.close();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (bluetoothReceiver != null) {
			unregisterReceiver(bluetoothReceiver);
		}
	}

	private boolean addBluetoothDevice(BluetoothDevice device) {
		boolean retValue = false;
		try {
			dbAdapter = new DBAdapter(this);
			dbAdapter = dbAdapter.open();
			String deviceId = device.getAddress();
			SensorMetaData sensorMetaData = dbAdapter
					.getSensorMetaData(deviceId);
			Log.i(Constants.GENERIC_COLLECTOR_TAG + "2", "Scanned in device "
					+ device.getName());
			if (sensorMetaData == null) {
				sensorMetaData = new SensorMetaData(deviceId, DataType.GENERIC,
						"N");
				sensorMetaData.setDescription(device.getName());
				sensorMetaData.setId(device.getAddress());
				dbAdapter.insertSensorMetaData(sensorMetaData);
				sensorMetaDataList.add(sensorMetaData);
				Log.i(Constants.GENERIC_COLLECTOR_TAG + "2",
						"Inserted device into DB " + device);
				retValue = true;
			}
		} catch (Exception ex) {
			Log.e(Constants.GENERIC_COLLECTOR_TAG + "2",
					"Could not register bluetooth device " + device.getName()
							+ "because " + ex.getMessage(), ex);
		} finally {
			dbAdapter.close();
		}
		return retValue;
	}

	private void setSensorMetaDataList() {
		try {
			dbAdapter = new DBAdapter(this);
			dbAdapter = dbAdapter.open();
			sensorMetaDataList = dbAdapter.getAllSensorMetaData();
		} catch (Exception ex) {
			Log.e(Constants.GENERIC_COLLECTOR_TAG,
					"Unable to initialize sensor list activity because "
							+ ex.getMessage(), ex);
			Toast.makeText(this,
					"Cannot load activity because " + ex.getMessage(),
					Toast.LENGTH_LONG);
		} finally {
			dbAdapter.close();
		}
	}

	private void setSensorArrayAdapter() {
		Log.i(Constants.GENERIC_COLLECTOR_TAG, "Setting sensor array adapter");
		try {
			setSensorMetaDataList();
			SensorViewAdapter adapter = new SensorViewAdapter(this,
					sensorMetaDataList);
			this.setListAdapter(adapter);
		} catch (Exception ex) {
			String errorStr = "Cannot set sensor array adapter because "
					+ ex.getMessage();
			Log.e(Constants.GENERIC_COLLECTOR_TAG, errorStr, ex);
			Toast.makeText(this, errorStr, Toast.LENGTH_LONG);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.scanBluetooth:
			try {
				enableAndScanBluetooth();
				enableAndScanLowEnergyBluetooth();
			} catch (Exception e) {
				Log.e(Constants.GENERIC_COLLECTOR_TAG,
						"Could not scan bluetooth devices because "
								+ e.getMessage());
				Toast.makeText(
						this,
						"Could not scan in bluetooth devices because "
								+ e.getMessage(), Toast.LENGTH_LONG);
			}
			return true;
		case R.id.configureDevice:
			Intent intent = new Intent(this, ConfigurationActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent dataa) {
		Log.i(Constants.GENERIC_COLLECTOR_TAG,
				"Received response from bluetooth adapter");
		if (resultCode == RESULT_OK) {
			Toast.makeText(this, "Bluetooth is enabled ", Toast.LENGTH_LONG);
		} else {
			Toast.makeText(this, "Bluetooth is not enabled ", Toast.LENGTH_LONG);
			Log.i(Constants.GENERIC_COLLECTOR_TAG,
					"Receive and incorrect result code when enabling blue tooth");
		}
	}

	private BluetoothAdapter getBluetoothAdapter()
			throws BlueToothNotSupportedException {
		BluetoothManager manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
		BluetoothAdapter bluetoothAdapter = manager.getAdapter();
		if (bluetoothAdapter == null) {
			Log.e(Constants.GENERIC_COLLECTOR_TAG, "blue tooth adapter is null");
			throw new BlueToothNotSupportedException(
					"Device does not support bluetooth connectivity");
		}
		if (!bluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		return bluetoothAdapter;
	}

	private void enableAndScanLowEnergyBluetooth()
			throws BlueToothNotSupportedException {
		BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();
		if (!getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(GenericCollectorActivity.this,
					"Low energy blue tooth scan not avaible on this device",
					Toast.LENGTH_LONG);
			Log.i(Constants.GENERIC_COLLECTOR_TAG + "2",
					"Feature not enabled to scan in low energy devices");
			return;
		}
		Log.i(Constants.GENERIC_COLLECTOR_TAG + "2",
				"Scanning in low energy devices");
		bluetoothAdapter.startLeScan(this);
		Log.i(Constants.GENERIC_COLLECTOR_TAG + "2", "Started le scan");
		setSensorArrayAdapter();
	}

	
	private void enableAndScanBluetooth() throws BlueToothNotSupportedException {
		BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();
		Log.i(Constants.GENERIC_COLLECTOR_TAG,
				"Starting bluetooth device discovery");
		bluetoothAdapter.startDiscovery();
		Log.i(Constants.GENERIC_COLLECTOR_TAG, "Getting bonded devices");
		Set<BluetoothDevice> deviceSet = bluetoothAdapter.getBondedDevices();
		Log.i(Constants.GENERIC_COLLECTOR_TAG, "Retrieved " + deviceSet.size()
				+ " bonding devices");
		boolean foundNewDevice = false;
		for (BluetoothDevice device : deviceSet) {
			if (addBluetoothDevice(device)) {
				foundNewDevice = true;
			}
		}
		if (foundNewDevice) {
			Log.i(Constants.GENERIC_COLLECTOR_TAG,
					"reset sensor array adapter after scanning in new device");
		}
		setSensorArrayAdapter();
	}

	private class SensorViewAdapter extends ArrayAdapter {

		private List<SensorMetaData> sensorMetaData;

		public SensorViewAdapter(Context context,
				List<SensorMetaData> sensorMetaDataList) {
			super(context, R.layout.sensors_activity, sensorMetaDataList);
			this.sensorMetaData = sensorMetaDataList;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			try {
				LayoutInflater inflater = (LayoutInflater) GenericCollectorActivity.this
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.sensors_activity,
						parent, false);
				TextView text1 = (TextView) convertView
						.findViewById(R.id.Description);
				CheckBox checkbox = (CheckBox) convertView
						.findViewById(R.id.sensorActive);
				text1.setText(sensorMetaData.get(position).getDescription());
				Button button = (Button) convertView
						.findViewById(R.id.configure);
				button.setOnClickListener(new SensorConfigureListener(
						sensorMetaData.get(position)));
				Button deleteButton = (Button) convertView
						.findViewById(R.id.delete);
				deleteButton.setOnClickListener(new SensorDeleteListener(
						sensorMetaData.get(position)));
				if (sensorMetaData.get(position).getDataType() != DataType.GENERIC) {
					deleteButton.setVisibility(View.GONE);
				}
				if (sensorMetaData.get(position).getActive().equals("Y")) {
					checkbox.setChecked(true);
				} else {
					checkbox.setChecked(false);
				}
				checkbox.setOnCheckedChangeListener(new ActivityListener(
						position));
			} catch (Exception ex) {
				Log.e(Constants.GENERIC_COLLECTOR_TAG,
						"Cannot set view because " + ex.getMessage(), ex);
			}
			return convertView;
		}

		class ActivityListener implements OnCheckedChangeListener {
			private int position;

			ActivityListener(int position) {
				this.position = position;
			}

			public void onCheckedChanged(CompoundButton compoundButton,
					boolean checked) {
				try {
					SensorMetaData sensorMetaData = (SensorMetaData) SensorViewAdapter.this
							.getItem(position);
					if (checked) {
						Log.i(Constants.GENERIC_COLLECTOR_TAG, "device  "
								+ sensorMetaData.getDescription()
								+ " turned on");
						sensorMetaData.setActive("Y");
					} else {
						Log.i(Constants.GENERIC_COLLECTOR_TAG, "device  "
								+ sensorMetaData.getDescription()
								+ " turned off");
						sensorMetaData.setActive("N");
					}
					GenericCollectorActivity.this.dbAdapter.open();
					GenericCollectorActivity.this.dbAdapter
							.updateSensorMetaData(sensorMetaData);
				} catch (Exception ex) {
					Log.e(Constants.GENERIC_COLLECTOR_TAG,
							"Unable to change sensor meta data activity");
				} finally {
					GenericCollectorActivity.this.dbAdapter.close();
					Intent startCollectingIntent = new Intent();
					startCollectingIntent
							.setAction(DataCollectorReceiver.COLLECT_GENERIC_DATA);
					GenericCollectorActivity.this
							.sendBroadcast(startCollectingIntent);
				}
			}
		}

	}

	private class SensorConfigureListener implements OnClickListener {
		SensorMetaData sensorMetaData;

		SensorConfigureListener(SensorMetaData sensorMetaData) {
			this.sensorMetaData = sensorMetaData;
		}

		public void onClick(View view) {
			Intent intent = new Intent(GenericCollectorActivity.this,
					SensorConfigurationActivity.class);
			intent.putExtra(SensorConfigurationActivity.SENSOR_META_DATA_KEY,
					sensorMetaData);
			startActivity(intent);

		}

	}

	private class SensorDeleteListener implements OnClickListener {
		SensorMetaData sensorMetaData;

		SensorDeleteListener(SensorMetaData sensorMetaData) {
			this.sensorMetaData = sensorMetaData;
		}

		public void onClick(View view) {
			try {
				GenericCollectorActivity.this.dbAdapter.open();
				GenericCollectorActivity.this.dbAdapter.delete(sensorMetaData);
				GenericCollectorActivity.this.setSensorArrayAdapter();
			} finally {
				GenericCollectorActivity.this.dbAdapter.close();
			}
		}

	}

	public void onLeScan(BluetoothDevice bluetoothLEDevice, int rssi,
			byte[] scanRecord) {
		Log.i(Constants.GENERIC_COLLECTOR_TAG + "2",
				"received notification from le device");
		Log.i(Constants.GENERIC_COLLECTOR_TAG + "2",
				"Scanned in low energy device " + bluetoothLEDevice + " rssi "
						+ rssi + " scanRecord " + scanRecord);
		List<AdRecord> records = AdRecord.parseScanRecord(scanRecord);
		if (records.size() == 0) {
			Log.i(Constants.GENERIC_COLLECTOR_TAG + "2", "Scan Record Empty");
		} else {
			Log.i(Constants.GENERIC_COLLECTOR_TAG + "2", "Scan Record: "
					+ TextUtils.join(",", records));
		}
		Log.i(Constants.GENERIC_COLLECTOR_TAG + "2", "Finished scanning ad record");
		addBluetoothDevice(bluetoothLEDevice);
	}

}
