package com.javaapps.gdc.dao;

import static org.junit.Assert.fail;

import com.javaapps.gdc.dao.GenericDataDAO;

public class MongoDeleter {

	public static void main(String args[]) {
		try {
			GenericDataDAO genericDataDAO = new GenericDataDAO();
			genericDataDAO.deleteByWildcard("b8b");
			System.out.println("delete finished");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
