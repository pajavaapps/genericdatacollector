package com.javaapps.gdc.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.javaapps.gdc.Constants;
import com.javaapps.gdc.R;
import com.javaapps.gdc.db.DBAdapter;
import com.javaapps.gdc.exceptions.BlueToothNotSupportedException;
import com.javaapps.gdc.pojos.*;
import com.javaapps.gdc.pojos.SensorMetaData.Service;
import com.javaapps.gdc.probes.BlueToothLEMetaData;
import com.javaapps.gdc.probes.BlueToothLEService;
import com.javaapps.gdc.probes.BluetoothProbe;
import com.javaapps.gdc.receivers.DataCollectorReceiver;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SensorConfigurationActivity extends Activity {

	public static final String SENSOR_META_DATA_KEY = "sensorMetaDataKey";
	private DBAdapter dbAdapter;
	private ServiceArrayAdapter serviceArrayAdapter;

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
	public void onCreate(Bundle sensorMetaDataState) {
		super.onCreate(sensorMetaDataState);
		try {
			Intent intent = this.getIntent();
			SensorMetaData sensorMetaData = (SensorMetaData) intent
					.getSerializableExtra(SENSOR_META_DATA_KEY);
			Log.i(Constants.GENERIC_COLLECTOR_TAG2,
					"Intent received with sensor meta data " + sensorMetaData);
			BlueToothLEMetaData blueToothLEMetaData = (BlueToothLEMetaData) intent
					.getSerializableExtra(BluetoothProbe.BLUE_TOOTH_META_DATA);
			if (blueToothLEMetaData != null) {
				Log.i(Constants.GENERIC_COLLECTOR_TAG2,
						"Intent received with bluetooth meta data "
								+ blueToothLEMetaData);
				addBlueToothServiceData(sensorMetaData, blueToothLEMetaData);
			}
			this.setContentView(R.layout.sensors_configuration);
			this.serviceArrayAdapter = new ServiceArrayAdapter(this,
					sensorMetaData.getServiceList());
			ListView listView = (ListView) findViewById(R.id.bluetoothServiceListView);
			listView.setAdapter(serviceArrayAdapter);
			EditText descriptionView = (EditText) findViewById(R.id.configDescription);
			descriptionView.setText(sensorMetaData.getDescription());
			EditText samplingPeriodView = (EditText) findViewById(R.id.configSamplingPeriod);
			samplingPeriodView.setText(String.valueOf(sensorMetaData
					.getSamplingPeriod()));
			EditText aggregationPeriodView = (EditText) findViewById(R.id.configAggregationPeriod);
			aggregationPeriodView.setText(String.valueOf(sensorMetaData
					.getAggregationPeriod()));
			EditText conversionFactorView = (EditText) findViewById(R.id.configConversionFactor);
			conversionFactorView.setText(String.valueOf(sensorMetaData
					.getConversionFactor()));
			Button saveButton = (Button) findViewById(R.id.configSave);
			saveButton
					.setOnClickListener(new ConfigSaveListener(sensorMetaData));
			Button cancelButton = (Button) findViewById(R.id.configCancel);
			cancelButton.setOnClickListener(new ConfigCancelListener());
		} catch (Exception ex) {
			Log.e(Constants.GENERIC_COLLECTOR_TAG2,
					"Unable to initialize configure activity because "
							+ ex.getMessage(), ex);
		}
	}

	private void addBlueToothServiceData(SensorMetaData sensorMetaData,
			BlueToothLEMetaData blueToothLEMetaData) {
		Log.i(Constants.GENERIC_COLLECTOR_TAG2,
				"Adding bluetooth service list data ");
		sensorMetaData.getServiceList().clear();
		for (BlueToothLEService bluetoothService : blueToothLEMetaData
				.getServiceMetaDataMap().values()) {
			Service service = new Service(bluetoothService.getServiceName(),
					bluetoothService.getServiceUUID(), false);
			sensorMetaData.getServiceList().add(service);
		}
		Log.i(Constants.GENERIC_COLLECTOR_TAG2,
				"Added bluetooth service list data "
						+ sensorMetaData.getServiceList());

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
		return true;
	}

	class ConfigSaveListener implements OnClickListener {
		private SensorMetaData sensorMetaData;

		private ConfigSaveListener(SensorMetaData sensorMetaData) {
			this.sensorMetaData = sensorMetaData;
		}

		public void onClick(View view) {
			DBAdapter dbAdapter = null;
			try {
				EditText descriptionView = (EditText) findViewById(R.id.configDescription);
				sensorMetaData.setDescription(descriptionView.getText()
						.toString());
				EditText samplingPeriodView = (EditText) findViewById(R.id.configSamplingPeriod);
				sensorMetaData.setSamplingPeriod(Integer
						.parseInt(samplingPeriodView.getText().toString()));
				EditText aggregationPeriodView = (EditText) findViewById(R.id.configAggregationPeriod);
				sensorMetaData.setAggregationPeriod(Integer
						.parseInt(aggregationPeriodView.getText().toString()));
				EditText conversionFactorView = (EditText) findViewById(R.id.configConversionFactor);
				sensorMetaData
						.setConversionFactor(Double
								.parseDouble(conversionFactorView.getText()
										.toString()));
				sensorMetaData
						.setAggregationType(SensorMetaData.DEFAULT_AGGREGATION_TYPE);
				sensorMetaData.dehyrdrateServiceString();
				dbAdapter = new DBAdapter(SensorConfigurationActivity.this);
				dbAdapter.open();
				dbAdapter.updateSensorMetaData(sensorMetaData);
				SensorMetaData sensorMetaData = dbAdapter
						.getSensorMetaData(this.sensorMetaData.getId());
				DataCollectorReceiver.updateProbe(SensorConfigurationActivity.this,sensorMetaData.getId());
				Intent intent = new Intent(SensorConfigurationActivity.this,
						GenericCollectorActivity.class);
				startActivity(intent);
			} catch (Exception ex) {
				Log.e(Constants.GENERIC_COLLECTOR_TAG3,
						"Could not save sensor meta data because "
								+ ex.getMessage(), ex);
				Toast.makeText(
						SensorConfigurationActivity.this,
						"Was unable to update sensor meta data because "
								+ ex.getMessage(), Toast.LENGTH_LONG);
			} finally {
				if (dbAdapter != null) {
					dbAdapter.close();
				}
			}
		}
	}

	class ConfigCancelListener implements OnClickListener {
		private SensorMetaData sensorMetaData;

		public void onClick(View arg0) {
			Intent intent = new Intent(SensorConfigurationActivity.this,
					GenericCollectorActivity.class);
			startActivity(intent);
		}
	}

	private class ServiceArrayAdapter extends ArrayAdapter<Service> {

		private List<Service> serviceList;

		public ServiceArrayAdapter(Context context, List<Service> serviceList) {
			super(context, R.layout.sensors_configuration, serviceList);
			this.serviceList = serviceList;
		}

		@Override
		public View getView(int position, View serviceView, ViewGroup parent) {
			try {
				LayoutInflater inflater = (LayoutInflater) SensorConfigurationActivity.this
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				serviceView = inflater.inflate(R.layout.bluetooth_services,
						null);
				TextView serviceNameText = (TextView) serviceView
						.findViewById(R.id.serviceName);
				TextView serviceUUIDText = (TextView) serviceView
						.findViewById(R.id.serviceUUID);
				CheckBox serviceCheckbox = (CheckBox) serviceView
						.findViewById(R.id.serviceActive);
				OnCheckedChangeListener listener;
				final Service service = serviceList.get(position);
				serviceCheckbox
						.setOnCheckedChangeListener(new OnCheckedChangeListener() {
							public void onCheckedChanged(CompoundButton button,
									boolean isChecked) {
									service.setActive(isChecked);
							}

						});
				serviceNameText.setText(service.getServiceName());
				serviceUUIDText.setText(service.getServiceUUID().toString());
				if (service.isActive()) {
					serviceCheckbox.setChecked(true);
				} else {
					serviceCheckbox.setChecked(false);
				}
			} catch (Exception ex) {
				Log.e(Constants.GENERIC_COLLECTOR_TAG,
						"Unable to display bluetooth services because "
								+ ex.getMessage(), ex);
			}
			return serviceView;
		}
	}
}
