package com.javaapps.gdc.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.javaapps.gdc.model.ClientMetaDataBuilder;

public class ClientMetaDataBuilderTest {
	private final String TEST_COLLECTION_NAMES1[] = {
			"djs_gmail_com.123.gps_2014-01-31.2014-01-31-06-55",
			"djs_gmail_com.123.gps_2014-01-31.2014-01-31-07-55" };

	private final String TEST_COLLECTION_NAMES2[] = {
			"djs_gmail_com.123.gps_2014-01-30.2014-01-30-06-55",
			"djs_gmail_com.123.gps_2014-01-31.2014-01-31-06-55",
			"djs_gmail_com.123.gps_2014-01-31.2014-01-31-07-55",
			"djs_gmail_com.123.gforce_2014-01-30.2014-01-30-06-55",
			"djs_gmail_com.123.gforce_2014-01-31.2014-01-31-06-55",
			"djs_gmail_com.123.gforce_2014-01-31.2014-01-31-07-55"					
	};

	@Test
	public void addCollectionNameTest() {
		ClientMetaDataBuilder clientMetaDataBuilder = new ClientMetaDataBuilder();
		for (String collectionName : TEST_COLLECTION_NAMES1) {
			clientMetaDataBuilder.addCollectionName(collectionName);
		}
		List<String> testList=new ArrayList<String>( clientMetaDataBuilder.collectionNameSet);
		assertEquals(1, testList.size());
		assertEquals("djs_gmail_com.123.gps_2014-01-31",testList.get(0));
	}
	
	@Test
	public void buildTest(){
		ClientMetaDataBuilder clientMetaDataBuilder = new ClientMetaDataBuilder();
		for (String collectionName : TEST_COLLECTION_NAMES2) {
			clientMetaDataBuilder.addCollectionName(collectionName);
		}
		ClientMetaData clientMetaData=clientMetaDataBuilder.build();
		assertNotNull(clientMetaData);
	}

}
