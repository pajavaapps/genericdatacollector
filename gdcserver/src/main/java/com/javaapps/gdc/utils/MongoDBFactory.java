package com.javaapps.gdc.utils;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

public class MongoDBFactory {

	private String username;
	private String password;
	private String mongoHost;
	private String dbName;
	private int mongoPort;

	public MongoDBFactory(String mongoHost, int mongoPort, String dbName,
			String username, String password) {
		this.mongoHost = mongoHost;
		this.mongoPort = mongoPort;
		this.dbName = dbName;
		this.username = username;
		this.password = password;
	}

	public DB createMongoDB() throws UnknownHostException {
		List<MongoCredential> credentialsList = new ArrayList<MongoCredential>();
		List<ServerAddress> serverAddressList = new ArrayList<ServerAddress>();
		credentialsList.add(MongoCredential.createMongoCRCredential(username,
				dbName, password.toCharArray()));
		serverAddressList.add(new ServerAddress(mongoHost, mongoPort));
		MongoClient mongoClient = new MongoClient(serverAddressList,
				credentialsList);
		DB db = mongoClient.getDB(dbName);
		return db;
	}

}
