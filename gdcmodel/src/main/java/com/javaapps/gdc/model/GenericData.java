package com.javaapps.gdc.model;

import java.io.Serializable;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import com.javaapps.gdc.interfaces.CsvWriter;

@JsonTypeInfo(  
	    use = JsonTypeInfo.Id.NAME,  
	    include = JsonTypeInfo.As.PROPERTY,  
	    property = "dataType")  
	@JsonSubTypes({  
	    @Type(value = GForce.class, name = "GFORCE"),  
	    @Type(value = GPS.class, name = "GPS") }) 
public class GenericData implements Serializable, CsvWriter,Comparable {
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
		this.sampleDate=new Date(sampleDateInMillis);
	}

	public long getSampleDateInMillis() {
		return sampleDate.getTime();
	}

	@Override
	public int compareTo(Object arg0) {
		// TODO implement comparable
		return 1;
	}

}
