package com.javaapps.gdc.probes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BlueToothLEService {

	private  String serviceName;

	private UUID serviceUUID;

	private  Map<String, BlueToothLECharacteristic> characteristicMap;
	
	public BlueToothLEService() {
	}

	public String getServiceName() {
		return serviceName;
	}

	public UUID getServiceUUID() {
		return serviceUUID;
	}

    	
	public Map<String, BlueToothLECharacteristic> getCharacteristicMap() {
		return characteristicMap;
	}

	
	
	@Override
	public String toString() {
		return "BlueToothLEService [serviceName=" + serviceName
				+ ", serviceUUID=" + serviceUUID + ", characteristicMap="
				+ characteristicMap + "]";
	}

	private BlueToothLEService(
			String serviceName,
			UUID serviceUUID,
			Map<String, BlueToothLECharacteristic> characteristicMap) {
		this.serviceName = serviceName;
		this.serviceUUID = serviceUUID;
		this.characteristicMap = characteristicMap;
	}
	
	public static class Builder {
		private final String serviceName;
		private final UUID serviceUUID;
		private final Map<String, BlueToothLECharacteristic> characteristicMap = new HashMap<String, BlueToothLECharacteristic>();

		public void addCharacteristic(String characteristicName,
				String characteristicUUIDStr,String enableCharacteristicUUIDStr,int enableCharacteristicValue,int disableCharacteristicValue) {
			BlueToothLECharacteristic characteristicMetaData = new BlueToothLECharacteristic(
					characteristicName, UUID.fromString(characteristicUUIDStr),UUID.fromString( enableCharacteristicUUIDStr),
					enableCharacteristicValue,disableCharacteristicValue);
			characteristicMap.put(characteristicName, characteristicMetaData);
		}

		public BlueToothLEService build() {
			return new BlueToothLEService(this.serviceName,
					this.serviceUUID, this.characteristicMap);
		}
		
		public  Builder(String serviceName, UUID serviceUUID) {
			this.serviceName = serviceName;
			this.serviceUUID = serviceUUID;
		}

	}

}
