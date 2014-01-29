package com.javaapps.gdc.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity()
@Table(name = "DEVICE_CHECKIN")
public class DeviceCheckinData {
	@Id
	private String identifier;
	private String customIdentifier;
	private Date lastUploadDate;
	private int version;
	private String externalIP;
 
	public DeviceCheckinData()
	{
		
	}
	
	public DeviceCheckinData(String identifier,String customIdentifier,String externalIP, Date lastUploadDate, int version) {
		super();
		this.identifier = identifier;
		this.customIdentifier=customIdentifier;
		this.lastUploadDate = lastUploadDate;
		this.version = version;
		this.externalIP=externalIP;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	
	public String getCustomIdentifier() {
		return customIdentifier;
	}

	public void setCustomIdentifier(String customIdentifier) {
		this.customIdentifier = customIdentifier;
	}

	public Date getLastUploadDate() {
		return lastUploadDate;
	}
	public void setLastUploadDate(Date lastUploadDate) {
		this.lastUploadDate = lastUploadDate;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	
	public String getExternalIP() {
		return externalIP;
	}

	public void setExternalIP(String externalIP) {
		this.externalIP = externalIP;
	}

	@Override
	public String toString() {
		return "DeviceCheckinData [identifier=" + identifier
				+ ", lastUploadDate=" + lastUploadDate + ", version=" + version
				+ "]";
	}
		

}
