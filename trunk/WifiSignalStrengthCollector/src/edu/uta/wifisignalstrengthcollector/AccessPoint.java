package edu.uta.wifisignalstrengthcollector;

import java.util.Date;


public class AccessPoint {
	String macAddress; 
	int signalLevel; 
	int distance;
	Date timestamp; 
	int id; 
	String SSID; 
	
	
	public String getSSID() {
		return SSID;
	}
	public void setSSID(String SSID) {
		this.SSID = SSID;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	public String getMacAddress() {
		return macAddress;
	}
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress.toUpperCase();
	}
	public int getSignalLevel() {
		return signalLevel;
	}
	public void setSignalLevel(int signalLevel) {
		this.signalLevel = signalLevel;
	}
	public int getDistance() {
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	} 
	
}
