package com.javaapps.gdc.probes;

import android.content.Context;

import com.javaapps.gdc.pojos.SensorMetaData;

public abstract class Probe {

	protected SensorMetaData sensorMetaData;
	
	protected Probe (SensorMetaData sensorMetaData){
		this.sensorMetaData=sensorMetaData;
	}
	
	abstract public void collectData(Context context);
	
	abstract public void stopCollectingData(Context context);

}
