package com.javaapps.gdc.model;

import java.io.Serializable;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.javaapps.gdc.interfaces.CsvWriter;


public class GPS extends GenericData{

	private static final long serialVersionUID = 1L;


	private double latitude;

	private double longitude;

	private float speed;

	private float bearing;

	private double altitude;

    public GPS()
    {
    	
    }
    
	public GPS(String csvString) {
		String props[] = csvString.split("\\,");
		if (props.length < 6) {
			return;
		}
		systemDate = new Date();
		sampleDate = new Date(Long.parseLong(props[0]));
		latitude = Double.parseDouble(props[1]);
		longitude = Double.parseDouble(props[2]);
		speed = Float.parseFloat(props[3]);
		bearing = Float.parseFloat(props[4]);
		altitude = Double.parseDouble(props[5]);

	}

	public GPS(double latitude, double longitude, float speed,
			float bearing, double altitude, long sampleDateTime) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.systemDate = new Date();
		this.speed = speed;
		this.bearing = bearing;
		this.altitude = altitude;
		this.sampleDate = new Date(sampleDateTime);
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Date getSystemDate() {
		return systemDate;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public float getSpeed() {
		return speed;
	}

	public float getBearing() {
		return bearing;
	}

	public double getAltitude() {
		return altitude;
	}

	public Date getSampleDate() {
		return sampleDate;
	}

	

	@Override
	public String toString() {
		return "GPS [latitude=" + latitude + ", longitude=" + longitude
				+ ", speed=" + speed + ", bearing=" + bearing + ", altitude="
				+ altitude + "]";
	}

	public String toCSV() {
		return sampleDate.getTime() + "," + latitude + "," + longitude + ","
				+ speed + "," + bearing + "," + altitude + "\n";
	}

	@JsonIgnore
	public String getDisplayString() {
		return latitude + "," + longitude + "," + speed + "," + bearing;
	}

}
