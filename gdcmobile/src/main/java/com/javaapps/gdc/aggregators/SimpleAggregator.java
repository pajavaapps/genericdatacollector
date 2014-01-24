package com.javaapps.gdc.aggregators;

import com.javaapps.gdc.model.GenericData;

public class SimpleAggregator implements Aggregator {
	private GenericData genericData;

	private int aggregationPeriod;

	public SimpleAggregator(int aggregationPeriod) {
		this.aggregationPeriod = aggregationPeriod;
	}

	/* (non-Javadoc)
	 * @see com.javaapps.gdc.aggregators.Aggregator#getAggregatedValue(com.javaapps.gdc.model.GenericData)
	 */
	public GenericData getAggregatedValue(GenericData newGenericData) {
		if (this.genericData == null) {
			this.genericData = newGenericData;
			return null;
		}
		if (aggregationPeriodExpired(newGenericData)) {
			GenericData retGenericData = this.genericData;
			this.genericData = newGenericData;
			return retGenericData;
		} else {
			return null;
		}
	}

	private boolean aggregationPeriodExpired(GenericData newGenericData) {
		if (aggregationPeriod > (Math.abs(newGenericData.getSampleDateInMillis() - this.genericData
				.getSampleDateInMillis()))) {
			return false;
		} else {
			return true;
		}
	}
}
