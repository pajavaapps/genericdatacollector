package com.javaapps.gdc.probes;

import java.util.Collection;
import java.util.UUID;

public class BlueToothLEMetaDataFacade {
	
	private BlueToothLEMetaData blueToothLEMetaData;

	public BlueToothLEMetaDataFacade(BlueToothLEMetaData blueToothLEMetaData) {
		this.blueToothLEMetaData = blueToothLEMetaData;
	}
	
	private BlueToothLEService getBlueToothLEService(String serviceName){
		return blueToothLEMetaData.getServiceMetaDataMap().get(serviceName);
	}
	
	public UUID getBlueToothLEServiceUUID(String serviceName){
		BlueToothLEService blueToothLEService=getBlueToothLEService(serviceName);
		if ( blueToothLEService != null ){
			return blueToothLEService.getServiceUUID();
		}else{
			return null;
		}
	}
	
	public BlueToothLECharacteristic getBlueToothLECharacteristic(String serviceName,String characteristicName){
		BlueToothLEService blueToothLEService=getBlueToothLEService(serviceName);
		if ( blueToothLEService != null ){
			 return blueToothLEService.getCharacteristicMap().get(characteristicName);
		}else{
			return null;
		}
	}
	
	public Collection<BlueToothLEService> getBlueToothLEService()
	{
		return blueToothLEMetaData.getServiceMetaDataMap().values();
	}
}
