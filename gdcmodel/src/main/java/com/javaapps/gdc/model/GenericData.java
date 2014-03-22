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
	    @Type(value = GPS.class, name = "GPS"),
	    @Type(value = BluetoothData.class, name = "BluetoothData") }) 
public abstract class GenericData implements GenericDataInterface {
	
	protected Date systemDate;

	protected Date sampleDate;

	
	public Date getSystemDate() {
		return systemDate;
	}

	public void setSystemDate(Date systemDate) {
		this.systemDate = systemDate;
	}

	@Override
	public Date getSampleDate() {
		return sampleDate;
	}

	public void setSampleDate(Date sampleDate) {
		this.sampleDate = sampleDate;
	}

	
	public abstract String toCSV(); 

	public void setSampleDateInMillis(long sampleDateInMillis) {
		this.sampleDate=new Date(sampleDateInMillis);
	}

	@Override
	public long getSampleDateInMillis() {
		return sampleDate.getTime();
	}

	@Override
	public int compareTo(Object object) {
		GenericData genericData=(GenericData)object;
		return ((int)(getSampleDateInMillis()-genericData.getSampleDateInMillis()));
	}



}
