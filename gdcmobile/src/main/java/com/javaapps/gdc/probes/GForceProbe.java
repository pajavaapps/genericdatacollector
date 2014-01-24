package com.javaapps.gdc.probes;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.javaapps.gdc.Constants;
import com.javaapps.gdc.factories.GenericDataFactory;
import com.javaapps.gdc.io.DataBuffer;
import com.javaapps.gdc.model.GenericData;
import com.javaapps.gdc.pojos.SensorMetaData;
import com.javaapps.gdc.types.DataType;

public class GForceProbe extends Probe implements SensorEventListener{
	
	public GForceProbe(SensorMetaData sensorMetaData){
		super(sensorMetaData);
	}

	@Override
	public void collectData(Context context) {
		try {
			Log.i(Constants.GENERIC_COLLECTOR_TAG, "registering accelerometer");
			SensorManager sensorManager = (SensorManager) context
					.getSystemService(Context.SENSOR_SERVICE);
			if (sensorManager.getSensorList(Sensor.TYPE_LINEAR_ACCELERATION)
					.size() != 0) {
				Sensor sensor = sensorManager.getSensorList(
						Sensor.TYPE_LINEAR_ACCELERATION).get(0);
				sensorManager.registerListener(this, sensor,
						SensorManager.SENSOR_DELAY_GAME);
				Log.i(Constants.GENERIC_COLLECTOR_TAG, "registered accelerometer");
			} else {
				Log.e(Constants.GENERIC_COLLECTOR_TAG,
						"Accelerometer sensor not found!!!");
			}
		} catch (Throwable ex) {
			Log.e(Constants.GENERIC_COLLECTOR_TAG,
					"Cannot register sensor listener because "+ex.getMessage());
		}
		
	}

	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	public void onSensorChanged(SensorEvent sensorEvent) {
		try
		{
		DataBuffer dataBuffer=DataBuffer.getInstance(sensorMetaData);
		if ( sensorEvent == null ||sensorEvent.values.length < 3)
		{
			return;
		}		
		GenericData genericData=GenericDataFactory.createGenericData(sensorEvent);
		DataBuffer.getInstance(sensorMetaData).logData(genericData);
		}catch(Exception ex){
			Log.e(Constants.GENERIC_COLLECTOR_TAG,"unable to log gforce data because "+ex.getMessage(),ex);
		}
	}

	@Override
	public void stopCollectingData(Context context) {		
      try{
    	  Log.i(Constants.GENERIC_COLLECTOR_TAG, "unregistering accelerometer");
    	  SensorManager sensorManager = (SensorManager) context
  				.getSystemService(Context.SENSOR_SERVICE);
    	 sensorManager.unregisterListener(this);
      }catch(Exception ex){
    	  Log.e(Constants.GENERIC_COLLECTOR_TAG,"Unable to unregister gforce probe because "+ex.getMessage());
      }		
	}

	
}
