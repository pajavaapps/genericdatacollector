package com.javaapps.gdc.types;

public enum DataType {
GPS("gps"),GFORCE("gforce"),GENERIC("generic");

private String prefix;
private DataType(String prefix){
	this.prefix=prefix;
}
public String getPrefix() {
	return prefix;
}


}
