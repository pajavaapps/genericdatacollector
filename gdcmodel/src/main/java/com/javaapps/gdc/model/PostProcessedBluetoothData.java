package com.javaapps.gdc.model;

import java.util.Date;

public class PostProcessedBluetoothData extends GenericData{

	private double value;
	
	public PostProcessedBluetoothData()
	{		
	}
	
	
	public PostProcessedBluetoothData(String csvString)
	{
		String props[] = csvString.split("\\,");
		if (props.length < 2) {
			return;
		}
		sampleDate = new Date(Long.parseLong(props[0].trim()));
		value = Double.parseDouble(props[1].trim());
	}
	
	@Override
	public String toCSV() {
		return sampleDate.getTime() + ","+value+"\n";
	}

}
