package com.javaapps.gdc.dao;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

public class MongoDBRenamer {

	public static void main(String[] args) {
		try
		{
			String srcStr="8861a57a2d0d212c";
			String destStr="a-don-samsung";		
			List<MongoCredential> credentialsList = new ArrayList<MongoCredential>();
			List<ServerAddress> serverAddressList = new ArrayList<ServerAddress>();
			credentialsList
					.add(MongoCredential.createMongoCRCredential("dschellberg",
							"kmplHfEnFDpBeVOWBXzg", "Off1973p".toCharArray()));
			serverAddressList.add(new ServerAddress("dharma.mongohq.com", 10075));
			MongoClient mongoClient = new MongoClient(serverAddressList, credentialsList);
			DB db = mongoClient.getDB("kmplHfEnFDpBeVOWBXzg");
			for (String collectionName : db.getCollectionNames()) {
				if ( collectionName.startsWith(srcStr)){
					String newCollectionName=collectionName.replace(srcStr,destStr);
					db.getCollection(collectionName).rename(newCollectionName);
				}
			}
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}

	}

}
