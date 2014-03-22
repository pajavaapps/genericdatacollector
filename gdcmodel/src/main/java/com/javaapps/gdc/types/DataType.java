package com.javaapps.gdc.types;

import java.util.ArrayList;
import java.util.List;

public enum DataType {
GPS("gps",new String[]{"sampleDate"},new String[]{"speed","altitude"}),
GFORCE("gforce",new String[]{"sampleDate"},new String[]{"x","y","z","gforce"}),
BLUETOOTH_DATA("blueTooth",new String[]{"sampleDate"},new String[]{"data"});

private String prefix;

private List<String>xAxisFields=new ArrayList<String>();

private List<String>yAxisFields=new ArrayList<String>();

private DataType(String prefix,String xAxisFields[],String yAxisFields[]){
	this.prefix=prefix;
	for ( String xAxisField:xAxisFields)
	{
	this.xAxisFields.add(xAxisField);
	}
	for ( String yAxisField:yAxisFields)
	{
	this.yAxisFields.add(yAxisField);
	}
}
public String getPrefix() {
	return prefix;
}
public List<String> getXAxisFields() {
	// TODO Auto-generated method stub
	return null;
}
public List<String> getYAxisFields() {
	// TODO Auto-generated method stub
	return null;
}


}
