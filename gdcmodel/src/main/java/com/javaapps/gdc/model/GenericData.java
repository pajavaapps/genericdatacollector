package com.javaapps.gdc.model;

import java.io.Serializable;
import java.util.Date;

import com.javaapps.gdc.interfaces.CsvWriter;

public class GenericData implements Serializable, CsvWriter {
	protected Date systemDate;

	protected Date sampleDate;

	protected double value;

	public GenericData(String csvString) {
		String props[] = csvString.split("\\,");
		if (props.length < 3) {
			return;
		}
		this.sampleDate.setTime(Long.parseLong(props[0]));
		this.value = Float.parseFloat(props[1]);
	}

	public GenericData() {

	}

	public Date getSystemDate() {
		return systemDate;
	}

	public void setSystemDate(Date systemDate) {
		this.systemDate = systemDate;
	}

	public Date getSampleDate() {
		return sampleDate;
	}

	public void setSampleDate(Date sampleDate) {
		this.sampleDate = sampleDate;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public String toCSV() {
		return sampleDate.getTime() + "," + value + "\n";
	}

	public void setSampleDateInMillis(long sampleDateInMillis) {
		this.sampleDate.setTime(sampleDateInMillis);
	}

	public long getSampleDateInMillis() {
		return sampleDate.getTime();
	}

}
