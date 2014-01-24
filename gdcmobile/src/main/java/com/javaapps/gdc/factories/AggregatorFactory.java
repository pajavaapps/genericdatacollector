package com.javaapps.gdc.factories;

import com.javaapps.gdc.aggregators.Aggregator;
import com.javaapps.gdc.aggregators.SimpleAggregator;
import com.javaapps.gdc.exceptions.OperationNotSupportedException;
import com.javaapps.gdc.pojos.SensorMetaData;
import com.javaapps.gdc.types.AggregationType;

public class AggregatorFactory {

	public static Aggregator createAggregator(SensorMetaData sensorMetaData) throws OperationNotSupportedException {
		if ( sensorMetaData.getAggregationType() == AggregationType.SIMPLE)
		{
		return new SimpleAggregator(sensorMetaData.getAggregationPeriod());
		}else{
			throw new OperationNotSupportedException("Unable to initilize aggregate because the type "+sensorMetaData.getAggregationType()+ "is not supported");
		}
	}

}
