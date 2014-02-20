package com.javaapps.gdc.model;

import java.util.ArrayList;
import java.util.List;

public class DayMetaData {

	private final String dateString;
	
	private final boolean  isGeospatial;
	
	private final List<MetaData> metaDataList;

	public DayMetaData(String dateString, boolean isGeospatial,
			List<MetaData> metaDataList) {
		super();
		this.dateString = dateString;
		this.isGeospatial = isGeospatial;
		this.metaDataList = metaDataList;
	}

	@Override
	public String toString() {
		return "DayMetaData [dateString=" + dateString + ", isGeospatial="
				+ isGeospatial + ", metaDataList=" + metaDataList + "]";
	}
		
	
}
