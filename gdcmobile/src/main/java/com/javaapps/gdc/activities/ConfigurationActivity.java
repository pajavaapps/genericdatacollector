package com.javaapps.gdc.activities;

import com.javaapps.gdc.Constants;
import com.javaapps.gdc.R;
import com.javaapps.gdc.db.DBAdapter;
import com.javaapps.gdc.pojos.DeviceMetaData;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ConfigurationActivity extends Activity {

	private DeviceMetaData deviceMetaData;
	
	@Override
	public void onCreate(Bundle configDataState) {
		super.onCreate(configDataState);
		this.setContentView(R.layout.configuration);
		Button saveButton = (Button) findViewById(R.id.sysConfigSave);
		saveButton.setOnClickListener(new ConfigSaveListener());
		Button cancelButton = (Button) findViewById(R.id.sysConfigCancel);
		cancelButton.setOnClickListener(new ConfigCancelListener());
		setUI();
	}

	private void setUI()
	{
		DBAdapter dbAdapter=new DBAdapter(this);
		try
		{
			dbAdapter.open();
			deviceMetaData=dbAdapter.getDeviceMetaData();
			if ( deviceMetaData == null){
				deviceMetaData=createDefaultDeviceMetaData();
			}
			EditText emailView = (EditText) findViewById(R.id.email);
			emailView.setText(deviceMetaData.getEmail());
			EditText dataEndpointView = (EditText) findViewById(R.id.dataEndpoint);
			dataEndpointView.setText(deviceMetaData.getDataEndpoint());
			EditText deviceNameView = (EditText) findViewById(R.id.deviceName);
			deviceNameView.setText(deviceMetaData.getCustomIdentifier());
		}catch(Exception ex){
			Log.e(Constants.GENERIC_COLLECTOR_TAG,"Could not setup devicemeta data because "+ex.getMessage());
		}finally{
			dbAdapter.close();
		}
	}
	
	private DeviceMetaData createDefaultDeviceMetaData() {
		DeviceMetaData deviceMetaData=new DeviceMetaData();
		deviceMetaData.setDataEndpoint(DeviceMetaData.DEFAULT_DATA_ENDPOINT);
		deviceMetaData.setDeviceId(Secure.getString(this.getContentResolver(),
				Secure.ANDROID_ID));
		return deviceMetaData;
	}

	private void goBackToMainScreen() {
		Intent intent = new Intent(this, GenericCollectorActivity.class);
		startActivity(intent);
	}

	class ConfigSaveListener implements OnClickListener {
		public void onClick(View view) {
			DBAdapter dbAdapter=new DBAdapter(ConfigurationActivity.this);
			try
			{
				Log.i(Constants.GENERIC_COLLECTOR_TAG,"Saving device meta data");
				dbAdapter.open();
				EditText emailView = (EditText) findViewById(R.id.email);
				deviceMetaData.setEmail(emailView.getText().toString());
				EditText dataEndpointView = (EditText) findViewById(R.id.dataEndpoint);
				deviceMetaData.setDataEndpoint(dataEndpointView.getText().toString());
				EditText deviceNameView = (EditText) findViewById(R.id.deviceName);
				deviceMetaData.setCustomIdentifier(deviceNameView.getText().toString());
				dbAdapter.insertDeviceMetaData(deviceMetaData);
				Log.i(Constants.GENERIC_COLLECTOR_TAG,"Device meta data "+deviceMetaData+" saved");
			}catch(Exception ex){
				String errorStr="Unable to save configuration data because "+ex.getMessage();
				Toast.makeText(ConfigurationActivity.this, errorStr, Toast.LENGTH_LONG);
				Log.e(Constants.GENERIC_COLLECTOR_TAG,errorStr,ex);
			}
			finally{
			ConfigurationActivity.this.goBackToMainScreen();
			dbAdapter.close();
			}
		}
	}

	
	class ConfigCancelListener implements OnClickListener {
		public void onClick(View view) {
			ConfigurationActivity.this.goBackToMainScreen();
		}
	}
}
