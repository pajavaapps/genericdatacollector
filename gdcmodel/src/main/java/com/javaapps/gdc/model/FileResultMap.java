package com.javaapps.gdc.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


public class FileResultMap {
	private String fileName;
	private Map<Integer, Integer> resultMap = new HashMap<Integer, Integer>();

	public FileResultMap(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

	public boolean allBatchesUploaded() {
		for (Entry<Integer, Integer> entry : resultMap.entrySet()) {
			if ((entry.getValue() / 100) != 2) {// status codes of
												// 200,201,202,204 are all good
				return false;
			}

		}
		return true;
	}

	public Map<Integer, Integer> getResultMap() {
		return resultMap;
	}

	public void setResultMap(Map<Integer, Integer> resultMap) {
		this.resultMap = resultMap;
	}

}