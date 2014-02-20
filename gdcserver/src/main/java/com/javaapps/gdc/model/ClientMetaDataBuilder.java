package com.javaapps.gdc.model;

import java.util.HashSet;
import java.util.Set;

import com.javaapps.gdc.types.DataType;

public class ClientMetaDataBuilder {

	Set<String> collectionNameSet = new HashSet<String>();

	public ClientMetaData build() {
		ClientMetaData clientMetaData = processCollectionStrings();
		return clientMetaData;
	}

	private ClientMetaData processCollectionStrings() {
		for (String collectionName:collectionNameSet)
		{
			int lastDotIndex=collectionName.lastIndexOf('.');
			if ( lastDotIndex >=0 && lastDotIndex < (collectionName.length()-1)){
				String dataTypeDate=collectionName.substring(lastDotIndex+1);
				int lastUnderscoreIndex=dataTypeDate.lastIndexOf('_');
				if ( lastUnderscoreIndex>=0 ){
					String dataType=dataTypeDate.substring(0,lastUnderscoreIndex);
					String dateStr=dataTypeDate.substring(lastUnderscoreIndex+1);
					MetaData metaData=new MetaData(DataType.valueOf(dataType));
					System.out.println(dataType+" "+dateStr);
				}
			}
		}
		return null;
	}

	public void addCollectionName(String collectionName) {
		int lastDotIndex = collectionName.lastIndexOf('.');
		if (lastDotIndex >= 0) {
			collectionName = collectionName.substring(0, lastDotIndex);
			collectionNameSet.add(collectionName);
		}
	}
}
