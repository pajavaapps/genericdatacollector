package com.javaapps.gdc.model;

import java.io.Serializable;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnore;
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
		if ( sampleDate != null)
		{
		return sampleDate.getTime();
		}else{
			return 0;
		}
	}

	@Override
	public int compareTo(Object object) {
		GenericData genericData=(GenericData)object;
		if ( genericData == null || genericData.getSampleDate() == null){
			return -1;
		}
		return ((int)(getSampleDateInMillis()-genericData.getSampleDateInMillis()));
	}

@JsonIgnore
   @Override
   public String getSensorId()
   {
	   return null;
   }
   
@JsonIgnore
   @Override
   public String getSensorDescription(){
	   return null;
   }

}
