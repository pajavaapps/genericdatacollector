package com.javaapps.gdc.model;

import java.util.ArrayList;
import java.util.List;
import com.javaapps.gdc.types.DataType;

import com.javaapps.gdc.types.DataType;

public class MetaData {
	private final String dataType;
	
	private final List<String> yAxisFields;
	
	private final List<String> xAxisFields;

	public MetaData(String dataType, List<String> yAxisFields,
			List<String> xAxisFields) {
		super();
		this.dataType = dataType;
		this.yAxisFields = yAxisFields;
		this.xAxisFields = xAxisFields;
	}

	public MetaData(DataType dataType) {
		this.dataType=dataType.name();
		xAxisFields=dataType.getXAxisFields();
		yAxisFields=dataType.getYAxisFields();
	}

	public String getDataType() {
		return dataType;
	}

	public List<String> getyAxisFields() {
		return yAxisFields;
	}

	public List<String> getxAxisFields() {
		return xAxisFields;
	}

	@Override
	public String toString() {
		return "MetaData [dataType=" + dataType + ", yAxisFields="
				+ yAxisFields + ", xAxisFields=" + xAxisFields + "]";
	}
	
}
