package com.javaapps.gdc.probes;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BlueToothLEMetaDataFacade {
	
	private BlueToothLEMetaData blueToothLEMetaData;
	
	public Map<String,String> uuidMap=new HashMap<String,String>();
	public Map<String,Duple> dupleMap=new HashMap<String,Duple>();
	
	public BlueToothLEMetaDataFacade(BlueToothLEMetaData blueToothLEMetaData) {
		this.blueToothLEMetaData = blueToothLEMetaData;
		for ( BlueToothLEService service:blueToothLEMetaData.getServiceMetaDataMap().values()){
			uuidMap.put(service.getServiceUUID().toString(), service.getServiceName());
			for ( BlueToothLECharacteristic ch:service.getCharacteristicMap().values()){
				uuidMap.put(ch.getCharacteristicUUID().toString(), ch.getCharacteristicName());
				uuidMap.put(ch.getEnableCharacteristicUUID().toString()+"_"+ch.getEnableCharacteristicValue(), "Enable "+ch.getCharacteristicName());
				uuidMap.put(ch.getEnableCharacteristicUUID().toString(), "Enable "+ch.getCharacteristicName());
    			dupleMap.put(ch.getEnableCharacteristicUUID().toString(), new Duple(service.getServiceUUID(),ch.getCharacteristicUUID()));
     			dupleMap.put(ch.getEnableCharacteristicUUID().toString()+"_"+ch.getEnableCharacteristicValue(), new Duple(service.getServiceUUID(),ch.getCharacteristicUUID()));
				dupleMap.put(ch.getCharacteristicUUID().toString(), new Duple(service.getServiceUUID(),ch.getCharacteristicUUID()));
			}
		}
	}
	
	
	public String getServiceName(String characteristicUUID){
		Duple duple=dupleMap.get(characteristicUUID);
		return(uuidMap.get(duple.serviceUUID.toString()));
	}
	
	public Duple getDuple(String enableUUIDString){
		return dupleMap.get(enableUUIDString);
	}
	
	private BlueToothLEService getBlueToothLEService(String serviceName){
		return blueToothLEMetaData.getServiceMetaDataMap().get(serviceName);
	}
	
	public String getLabel(String uuidString){
		return uuidMap.get(uuidString);
	}
	
	public UUID getBlueToothLEServiceUUID(String serviceName){
		BlueToothLEService blueToothLEService=getBlueToothLEService(serviceName);
		if ( blueToothLEService != null ){
			return blueToothLEService.getServiceUUID();
		}else{
			return null;
		}
	}
	
	public BlueToothLECharacteristic getBlueToothLECharacteristic(String serviceUUID,String characteristicUUID){
		BlueToothLEService blueToothLEService=getBlueToothLEService(serviceUUID);
		if ( blueToothLEService != null ){
			 return blueToothLEService.getCharacteristicMap().get(characteristicUUID);
		}else{
			return null;
		}
	}
	
	public BlueToothLECharacteristic getCalibrationBlueToothLECharacteristic(
			String serviceUUID) {
		BlueToothLEService blueToothLEService = getBlueToothLEService(serviceUUID);
		if (blueToothLEService != null) {
			for (BlueToothLECharacteristic characteristic : blueToothLEService
					.getCharacteristicMap().values())
				if (characteristic.isCalibration()) {
					return characteristic;
				}
		}
		return null;
	}
	
	public Collection<BlueToothLEService> getBlueToothLEService()
	{
		return blueToothLEMetaData.getServiceMetaDataMap().values();
	}
	
	public static class Duple{
		private UUID serviceUUID;
		private UUID charUUID;
		public Duple(UUID serviceUUID, UUID charUUID) {
			super();
			this.serviceUUID = serviceUUID;
			this.charUUID = charUUID;
		}
		public UUID getServiceUUID() {
			return serviceUUID;
		}
		public UUID getCharUUID() {
			return charUUID;
		}
		
		@Override
		public String toString() {
			return "Duple [serviceUUID=" + serviceUUID + ", charUUID="
					+ charUUID + "]";
		}
		
		
	}
}
