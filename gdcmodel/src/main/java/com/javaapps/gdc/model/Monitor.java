package com.javaapps.gdc.model;

import java.util.Date;



public class Monitor  {

	private int totalPointsUploaded = 0;
	private int totalPointsProcessed = 0;
	private int totalPointsLogged =0;
	private int pointsInBuffer = 0;
	private long currentFileSize = 0;
	

	public void incrementTotalPointsLogged(int size) {
		totalPointsLogged += size;
	}


	public void incrementTotalPointsProcessed(int size) {
		totalPointsProcessed += size;
	}



	public void incrementTotalPointsUploaded(int size) {
		totalPointsUploaded += size;
	}

	
	public int getTotalPointsProcessed() {
		return totalPointsProcessed;
	}

	public int getTotalPointsUploaded() {
		return totalPointsUploaded;
	}

	public long getCurrentFileSize() {
		return currentFileSize;
	}

	public void setCurrentFileSize(long currentFileSize) {
		this.currentFileSize = currentFileSize;
	}

	public int getPointsInBuffer() {
		return pointsInBuffer;
	}

	public void setPointsInBuffer(int pointsInBuffer) {
		this.pointsInBuffer = pointsInBuffer;
	}


	public int getTotalPointsLogged() {
		return totalPointsLogged;
	}


}
