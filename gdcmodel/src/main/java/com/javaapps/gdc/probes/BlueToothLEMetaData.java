package com.javaapps.gdc.probes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class BlueToothLEMetaData implements Serializable {


	private Map<String, BlueToothLEService> serviceMetaDataMap = new HashMap<String, BlueToothLEService>();

	public Map<String, BlueToothLEService> getServiceMetaDataMap() {
		return serviceMetaDataMap;
	}

	private BlueToothLEMetaData(
			Map<String, BlueToothLEService> serviceMetaDataMap) {
		this.serviceMetaDataMap = serviceMetaDataMap;
	}


	public BlueToothLEMetaData() {
	}

	@Override
	public String toString() {
		return "BlueToothLEMetaData [serviceMetaDataMap=" + serviceMetaDataMap
				+ "]";
	}

	public static class Builder {
		private Map<String, BlueToothLEService.Builder> serviceMetaDataBuilderMap = new HashMap<String, BlueToothLEService.Builder>();

		private BlueToothLEService.Builder addService(String serviceName,
				String serviceUUIDStr) {
			BlueToothLEService.Builder serviceBuilder = null;
			if (!serviceMetaDataBuilderMap.containsKey(serviceName)) {
				serviceBuilder = new BlueToothLEService.Builder(serviceName,
						UUID.fromString(serviceUUIDStr));
				serviceMetaDataBuilderMap.put(serviceName, serviceBuilder);
			} else {
				serviceBuilder = serviceMetaDataBuilderMap.get(serviceName);
			}
			return serviceBuilder;
		}

		public void addCharacteristic(String serviceName,
				String serviceUUIDStr, String characteristicName,
				String characteristicUUIDStr,
				String enableCharacteristicUUIDStr,
				int enableCharacteristicValue, int disableCharacteristicValue,
				boolean calibration) {
			BlueToothLEService.Builder serviceBuilder = addService(serviceName,
					serviceUUIDStr);
			serviceBuilder.addCharacteristic(characteristicName,
					characteristicUUIDStr, enableCharacteristicUUIDStr,
					enableCharacteristicValue, disableCharacteristicValue,
					calibration);
			serviceMetaDataBuilderMap.put(serviceName, serviceBuilder);
		}

		public BlueToothLEMetaData build() {
			Map<String, BlueToothLEService> serviceMetaDataMap = new HashMap<String, BlueToothLEService>();
			for (BlueToothLEService.Builder serviceMetaDataBuilder : serviceMetaDataBuilderMap
					.values()) {
				BlueToothLEService lowEnergyBlueToothServiceMetaData = serviceMetaDataBuilder
						.build();
				serviceMetaDataMap.put(
						lowEnergyBlueToothServiceMetaData.getServiceName(),
						lowEnergyBlueToothServiceMetaData);
			}
			return new BlueToothLEMetaData(serviceMetaDataMap);
		}
	}

}
