package com.javaapps.gdc.probes;

import java.util.UUID;

public class BlueToothLECharacteristic {

	private String characteristicName;

	private UUID characteristicUUID;

	private UUID enableCharacteristicUUID;

	private int enableCharacteristicValue;

	private int disableCharacteristicValue = 0;
	
	public BlueToothLECharacteristic() {
	}

	public String getCharacteristicName() {
		return characteristicName;
	}

	public UUID getCharacteristicUUID() {
		return characteristicUUID;
	}

	public UUID getEnableCharacteristicUUID() {
		return enableCharacteristicUUID;
	}

	public int getEnableCharacteristicValue() {
		return enableCharacteristicValue;
	}

	public int getDisableCharacteristicValue() {
		return disableCharacteristicValue;
	}

	public BlueToothLECharacteristic(String characteristicName,
			UUID characteristicUUID, UUID enableCharacteristicUUID,
			int enableCharacteristicValue) {
		this.characteristicName = characteristicName;
		this.characteristicUUID = characteristicUUID;
		this.enableCharacteristicUUID = enableCharacteristicUUID;
		this.enableCharacteristicValue = enableCharacteristicValue;
	}

	public BlueToothLECharacteristic(String characteristicName,
			UUID characteristicUUID, UUID enableCharacteristicUUID,
			int enableCharactersisticValue, int disableCharacteristicValue) {
		this(characteristicName, characteristicUUID, enableCharacteristicUUID,
				enableCharactersisticValue);
		this.disableCharacteristicValue = disableCharacteristicValue;
	}

	@Override
	public String toString() {
		return "BlueToothLECharacteristic [characteristicName="
				+ characteristicName + ", characteristicUUID="
				+ characteristicUUID + ", enableCharacteristicUUID="
				+ enableCharacteristicUUID + ", enableCharactersisticValue="
				+ enableCharacteristicValue + ", disableCharacteristicValue="
				+ disableCharacteristicValue + "]";
	}
	
	

}
