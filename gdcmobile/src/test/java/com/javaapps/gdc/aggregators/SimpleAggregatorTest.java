package com.javaapps.gdc.aggregators;

import static org.junit.Assert.*;

import org.junit.*;

import com.javaapps.gdc.model.GenericData;

public class SimpleAggregatorTest {
	
	@Test 
	public void getAggregatedValueTest()
	{
		Aggregator simpleAggregator=new SimpleAggregator(20);
		GenericData genericData=new GenericData();
		long beginningPoint=System.currentTimeMillis();
		genericData.setSampleDateInMillis(beginningPoint);
		genericData.setValue(2.0);
	    GenericData returnedGenericData=simpleAggregator.getAggregatedValue(genericData);
		assertNull(returnedGenericData);
		genericData=new GenericData();
		genericData.setSampleDateInMillis(beginningPoint+20);
		genericData.setValue(3.0);
		 returnedGenericData=simpleAggregator.getAggregatedValue(genericData);
		assertNotNull(returnedGenericData);
		assertEquals(2.0,returnedGenericData.getValue(),0.001);
		genericData=new GenericData();
		genericData.setSampleDateInMillis(beginningPoint+40);
		genericData.setValue(5.0);
		returnedGenericData=simpleAggregator.getAggregatedValue(genericData);
		assertNotNull(returnedGenericData);
		assertEquals(3.0,returnedGenericData.getValue(),0.001);
	}

}
