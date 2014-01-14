package com.javaapps.gdc.pojos;

import com.javaapps.gdc.types.DataType;

public class SensorMetaData {	
	private String id;
	private DataType dataType;
	private String dataSubType;
	private int samplingPeriod;
	private String description;
	private String aggregationMethod;
	private int aggregationPeriod;
	private String active;
	private double conversionFactor;
	
	public SensorMetaData(String id, DataType dataType,String active)
	{
		this.id=id;
		this.dataType=dataType;
		this.active=active;
	}
	
	public SensorMetaData(String id, String dataType, String dataSubType,
			int samplingPeriod, String description, String aggregationMethod,
			int aggregationPeriod, String active, double conversionFactor) {
		this.id = id;
		this.dataType = DataType.valueOf(dataType);
		this.dataSubType = dataSubType;
		this.samplingPeriod = samplingPeriod;
		this.description = description;
		this.aggregationMethod = aggregationMethod;
		this.aggregationPeriod = aggregationPeriod;
		this.active = active;
		this.conversionFactor = conversionFactor;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public DataType getDataType() {
		return dataType;
	}
	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}
	public String getDataSubType() {
		return dataSubType;
	}
	public void setDataSubType(String dataSubType) {
		this.dataSubType = dataSubType;
	}
	
	public int getSamplingPeriod() {
		return samplingPeriod;
	}
	public void setSamplingPeriod(int samplingPeriod) {
		this.samplingPeriod = samplingPeriod;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAggregationMethod() {
		return aggregationMethod;
	}
	public void setAggregationMethod(String aggregationMethod) {
		this.aggregationMethod = aggregationMethod;
	}
	public int getAggregationPeriod() {
		return aggregationPeriod;
	}
	public void setAggregationPeriod(int aggregationPeriod) {
		this.aggregationPeriod = aggregationPeriod;
	}
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	public double getConversionFactor() {
		return conversionFactor;
	}
	public void setConversionFactor(double conversionFactor) {
		this.conversionFactor = conversionFactor;
	}
	@Override
	public String toString() {
		return "SensorMetaData [id=" + id + ", dataType=" + dataType
				+ ", dataSubType=" + dataSubType + ", SamplingPeriod="
				+ samplingPeriod + ", description=" + description
				+ ", aggregationMethod=" + aggregationMethod
				+ ", aggregationPeriod=" + aggregationPeriod + ", active="
				+ active + ", conversionFactor=" + conversionFactor + "]";
	}
		
	
}
