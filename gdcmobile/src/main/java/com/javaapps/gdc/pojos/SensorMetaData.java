package com.javaapps.gdc.pojos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
	private String serviceString;

	private List<Service> serviceList = new ArrayList<Service>();

	public final static int DEFAULT_SAMPLING_PERIOD = 5000;
	public final static int DEFAULT_AGGREGATION_PERIOD = 5000;
	public final static AggregationType DEFAULT_AGGREGATION_TYPE = AggregationType.SIMPLE;
	public final static double DEFAULT_CONVERSION_FACTOR = 1.0;

	public SensorMetaData(String id, DataType dataType, String active) {
		this.id = id;
		this.dataType = dataType;
		this.active = active;
	}

	public SensorMetaData(String id, String dataType, String dataSubType,
			int samplingPeriod, String description, String aggregationType,
			int aggregationPeriod, String active, double conversionFactor,
			String serviceString) {
		this.id = id;
		//TODO remove!!
		if ( dataType.equals("GENERIC")){
			dataType="BLUETOOTH_DATA";
		}
		this.dataType = DataType.valueOf(dataType);
		this.dataSubType = dataSubType;
		this.samplingPeriod = samplingPeriod;
		this.description = description;
		this.aggregationType = AggregationType.valueOf(aggregationType);
		this.aggregationPeriod = aggregationPeriod;
		this.active = active;
		this.conversionFactor = conversionFactor;
		this.serviceString = serviceString;
	}

	public String getId() {
		return id;
	}

	public String getNormalizedSensorId()
	{
		if ( id == null){
			return null;
		}
		StringBuilder sb=new StringBuilder();
		String sensorId=id;
		for (int  ii=0;ii<sensorId.length();ii++){
			char ch=sensorId.charAt(ii);
			if ( Character.isLetterOrDigit(ch)){
				sb.append(ch);
			}
		}
		return sb.toString().trim();
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

	public List<Service> getServiceList() {
		return serviceList;
	}

	public void hydrateServiceString() {
		if (serviceString != null) {
			String services[] = serviceString.split("\n");
			for (String service : services) {
				String serviceParts[] = service.split("\\,");
				if (serviceParts.length > 2) {
					serviceList.add(new Service(serviceParts[0], UUID
							.fromString(serviceParts[1]), Boolean
							.parseBoolean(serviceParts[2])));
				}
			}
		}
	}

	public String getServiceString() {
		return serviceString;
	}

	public void dehyrdrateServiceString() {
		StringBuilder sb = new StringBuilder();
		for (Service service : serviceList) {
			sb.append(service.serviceName).append(",")
					.append(service.serviceUUID.toString()).append(",")
					.append(service.active).append("\n");
		}
		serviceString = sb.toString();
	}

	public void setServiceString(String serviceString) {
		this.serviceString = serviceString;
		hydrateServiceString();
	}

	@Override
	public String toString() {
		return "SensorMetaData [id=" + id + ", dataType=" + dataType
				+ ", dataSubType=" + dataSubType + ", samplingPeriod="
				+ samplingPeriod + ", description=" + description
				+ ", aggregationType=" + aggregationType
				+ ", aggregationPeriod=" + aggregationPeriod + ", active="
				+ active + ", conversionFactor=" + conversionFactor
				+ ", serviceList=" + serviceList + "]";
	}

	public static class Service implements Serializable {
		private String serviceName;
		private UUID serviceUUID;
		boolean active;

		public Service(String serviceName, UUID serviceUUID, boolean active) {
			super();
			this.serviceName = serviceName;
			this.serviceUUID = serviceUUID;
			this.active = active;
		}

		public String getServiceName() {
			return serviceName;
		}

		public void setServiceName(String serviceName) {
			this.serviceName = serviceName;
		}

		public UUID getServiceUUID() {
			return serviceUUID;
		}

		public void setServiceUUID(UUID serviceUUID) {
			this.serviceUUID = serviceUUID;
		}

		public boolean isActive() {
			return active;
		}

		public void setActive(boolean active) {
			this.active = active;
		}

			
		@Override
		public String toString() {
			return "Service [serviceName=" + serviceName + ", serviceUUID="
					+ serviceUUID + ", active=" + active + "]";
		}

	}
}
