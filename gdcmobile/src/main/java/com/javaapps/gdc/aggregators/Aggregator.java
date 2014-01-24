package com.javaapps.gdc.aggregators;

import com.javaapps.gdc.model.GenericData;

public interface Aggregator {

	public abstract GenericData getAggregatedValue(GenericData newGenericData);

}