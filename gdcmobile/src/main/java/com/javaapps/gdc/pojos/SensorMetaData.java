package com.javaapps.gdc.pojos;

import java.io.Serializable;

import com.javaapps.gdc.types.AggregationType;
import com.javaapps.gdc.types.DataType;

public class SensorMetaData implements Serializable {	
	private String id;
	private DataType dataType;
	private String dataSubType;
	private int samplingPeriod;
	private String description;
	private AggregationType aggregationType;
	private int aggregationPeriod;
	private String active;
	private double conversionFactor;
	
	public final static int DEFAULT_SAMPLING_PERIOD=500;
	public final static int DEFAULT_AGGREGATION_PERIOD=1000;
	public final static AggregationType DEFAULT_AGGREGATION_TYPE=AggregationType.SIMPLE;
	public final static double DEFAULT_CONVERSION_FACTOR=1.0;
	
	
	
	public SensorMetaData(String id, DataType dataType,String active)
	{
		this.id=id;
		this.dataType=dataType;
		this.active=active;
	}
	
	public SensorMetaData(String id, String dataType, String dataSubType,
			int samplingPeriod, String description, String aggregationType,
			int aggregationPeriod, String active, double conversionFactor) {
		this.id = id;
		this.dataType = DataType.valueOf(dataType);
		this.dataSubType = dataSubType;
		this.samplingPeriod = samplingPeriod;
		this.description = description;
		this.aggregationType = AggregationType.valueOf(aggregationType);
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
	
	public AggregationType getAggregationType() {
		return aggregationType;
	}

	public void setAggregationType(AggregationType aggregationType) {
		this.aggregationType = aggregationType;
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
				+ ", aggregationType=" + aggregationType
				+ ", aggregationPeriod=" + aggregationPeriod + ", active="
				+ active + ", conversionFactor=" + conversionFactor + "]";
	}
		
	
}
