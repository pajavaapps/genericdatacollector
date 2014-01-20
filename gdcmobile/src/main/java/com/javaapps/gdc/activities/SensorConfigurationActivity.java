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
			Log.d(Constants.GENERIC_COLLECTOR_TAG, "with intent " + intent
					+ " " + intent.getSerializableExtra(SENSOR_META_DATA_KEY));
			SensorMetaData sensorMetaData = (SensorMetaData) intent
					.getSerializableExtra(SENSOR_META_DATA_KEY);
			this.setContentView(R.layout.sensors_configuration);
			EditText descriptionView = (EditText) findViewById(R.id.configDescription);
			descriptionView.setText(sensorMetaData.getDescription());
			EditText samplingPeriodView = (EditText) findViewById(R.id.configSamplingPeriod);
			samplingPeriodView.setText(String.valueOf(sensorMetaData.getSamplingPeriod()));
			EditText aggregationPeriodView = (EditText) findViewById(R.id.configAggregationPeriod);
			aggregationPeriodView.setText(String.valueOf(sensorMetaData.getAggregationPeriod()));
			EditText conversionFactorView = (EditText) findViewById(R.id.configConversionFactor);
			conversionFactorView.setText(String.valueOf(sensorMetaData
					.getConversionFactor()));
			Button saveButton=(Button)findViewById(R.id.configSave);
			saveButton.setOnClickListener(new ConfigSaveListener(sensorMetaData));
			Button cancelButton=(Button)findViewById(R.id.configCancel);
			cancelButton.setOnClickListener(new ConfigCancelListener());
			
		} catch (Exception ex) {
			Log.e(Constants.GENERIC_COLLECTOR_TAG,
					"Unable to initialize configure activity because "
							+ ex.getMessage(),ex);
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
		return true;
	}

	class ConfigSaveListener implements OnClickListener{
		private  SensorMetaData sensorMetaData;

		private ConfigSaveListener(SensorMetaData sensorMetaData){
			this.sensorMetaData=sensorMetaData;
		}
		public void onClick(View view) {
			DBAdapter dbAdapter=null;
			try
			{
			EditText descriptionView = (EditText) findViewById(R.id.configDescription);
			sensorMetaData.setDescription(descriptionView.getText().toString());
			EditText samplingPeriodView = (EditText) findViewById(R.id.configSamplingPeriod);
			sensorMetaData.setSamplingPeriod(Integer.parseInt(samplingPeriodView.getText().toString()));
			EditText aggregationPeriodView = (EditText) findViewById(R.id.configAggregationPeriod);
			sensorMetaData.setAggregationPeriod(Integer.parseInt(aggregationPeriodView.getText().toString()));
			EditText conversionFactorView = (EditText) findViewById(R.id.configConversionFactor);
			sensorMetaData.setConversionFactor(Double.parseDouble(conversionFactorView.getText().toString()));
			sensorMetaData.setAggregationMethod(SensorMetaData.DEFAULT_AGGREGATION_METHOD);
			dbAdapter=new DBAdapter(SensorConfigurationActivity.this);
			dbAdapter.open();
			dbAdapter.updateSensorMetaData(sensorMetaData);
			Intent intent=new Intent(SensorConfigurationActivity.this, GenericCollectorActivity.class);
			startActivity(intent);
			}catch(Exception ex){
				Log.e(Constants.GENERIC_COLLECTOR_TAG,"Could not save sensor meta data because "+ex.getMessage(),ex);
				Toast.makeText(SensorConfigurationActivity.this, "Was unable to update sensor meta data because "+ex.getMessage(), Toast.LENGTH_LONG);
			}finally{
				if ( dbAdapter != null){
					dbAdapter.close();
				}
			}
		}
	}
	
	class ConfigCancelListener implements OnClickListener{
		private  SensorMetaData sensorMetaData;

		
		public void onClick(View arg0) {
			Intent intent=new Intent(SensorConfigurationActivity.this, GenericCollectorActivity.class);
			startActivity(intent);
		}
	}
}
