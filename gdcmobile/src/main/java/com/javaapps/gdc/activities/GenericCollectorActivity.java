package com.javaapps.gdc.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.javaapps.gdc.Constants;
import com.javaapps.gdc.R;
import com.javaapps.gdc.db.DBAdapter;
import com.javaapps.gdc.exceptions.BlueToothNotSupportedException;
import com.javaapps.gdc.pojos.*;
import com.javaapps.gdc.types.DataType;

import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class GenericCollectorActivity extends ListActivity {

	private final static int REQUEST_ENABLE_BT = 1;
	private DBAdapter dbAdapter;
	private List<SensorMetaData> sensorMetaDataList = new ArrayList<SensorMetaData>();

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState
	 *            If the activity is being re-initialized after previously being
	 *            shut down then this Bundle contains the data it most recently
	 *            supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it
	 *            is null.</b>
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setSensorArrayAdapter();
	}

	private void  setSensorMetaDataList()
	{
		try {
			dbAdapter = new DBAdapter(this);
			dbAdapter = dbAdapter.open();
			sensorMetaDataList = dbAdapter.getAllSensorMetaData();
		}
			catch (Exception ex) {
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
		
	private void setSensorArrayAdapter()
	{
		setSensorMetaDataList();
			SensorViewAdapter adapter = new SensorViewAdapter(this,
					sensorMetaDataList);
			this.setListAdapter(adapter);
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
				scanBlueToothDevices();
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
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent dataa) {
		Log.i(Constants.GENERIC_COLLECTOR_TAG,"Received response from bluetooth adapter");
		if (resultCode == RESULT_OK) {
			Log.i(Constants.GENERIC_COLLECTOR_TAG,"Result OK");
			dbAdapter = new DBAdapter(this);
			dbAdapter = dbAdapter.open();
			BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
					.getDefaultAdapter();
			Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
					.getBondedDevices();
			// If there are paired devices
			if (pairedDevices.size() > 0) {
				// Loop through paired devices

				for (BluetoothDevice device : pairedDevices) {
					String deviceId = device.getAddress();
					SensorMetaData sensorMetaData = dbAdapter
							.getSensorMetaData(deviceId);
					Log.i(Constants.GENERIC_COLLECTOR_TAG,"Scanned in device "+device.getName());
					if (sensorMetaData == null) {
						sensorMetaData = new SensorMetaData(deviceId,
								DataType.GENERIC, "N");
						sensorMetaData.setDescription(device.getName());
						dbAdapter.insertSensorMetaData(sensorMetaData);
						sensorMetaDataList.add(sensorMetaData);
						Log.i(Constants.GENERIC_COLLECTOR_TAG,"Inserted device into DB "+device.getName());
					}
				}
			}
			dbAdapter.close();
			Toast.makeText(this, "Bluetooth devices scanned ", Toast.LENGTH_LONG);
		}else{
			Toast.makeText(this, "Bluetooth devices not scanned ", Toast.LENGTH_LONG);
			Log.i(Constants.GENERIC_COLLECTOR_TAG,"Receive and incorrect result code when scanning for blue tooth devices");
		}
		setSensorArrayAdapter();
	}

	private void scanBlueToothDevices() throws BlueToothNotSupportedException {
		List<SensorMetaData> deviceList = new ArrayList<SensorMetaData>();
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			throw new BlueToothNotSupportedException(
					"Device does not support bluetooth connectivity");
		}
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			Toast.makeText(this, "Starting to scan Bluetooth devices ", Toast.LENGTH_LONG);
		}else
		{
			Toast.makeText(this, "Bluetooth not enabled ", Toast.LENGTH_LONG);
		}
	}

	class SensorViewAdapter extends ArrayAdapter {

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
						.findViewById(R.id.sensorDescription);
				CheckBox checkbox = (CheckBox) convertView
						.findViewById(R.id.sensorActive);
				text1.setText(sensorMetaData.get(position).getDescription());
				if (sensorMetaData.get(position).getActive().equals("Y")) {
					checkbox.setChecked(true);
				} else {
					checkbox.setChecked(false);
				}
			} catch (Exception ex) {
				Log.e(Constants.GENERIC_COLLECTOR_TAG,
						"Cannot set view because " + ex.getMessage(), ex);
			}
			return convertView;
		}
	}
}
