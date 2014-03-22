package com.javaapps.gdc.model;

import java.io.Serializable;
import java.util.Date;

import com.javaapps.gdc.interfaces.CsvWriter;

public interface GenericDataInterface extends  Serializable, CsvWriter,Comparable{

	public abstract Date getSampleDate();

	public abstract String toCSV();

	public abstract long getSampleDateInMillis();

	public abstract int compareTo(Object arg0);

}