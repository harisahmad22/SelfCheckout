package org.driver;

import java.util.ArrayList;

import org.controlSoftware.attendant.AttendantData;
import org.controlSoftware.attendant.AttendantSoftware;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SupervisionStation;

public class AttendantUnit {
	
	private SupervisionStation attendantStation;
	private AttendantSoftware attendantSoftware;
	private ArrayList<SelfCheckoutStationUnit> checkoutStations;
	private AttendantData attendantData;
	public AttendantUnit()
	{
		checkoutStations = new ArrayList<SelfCheckoutStationUnit>();
		this.attendantData = new AttendantData();
		this.attendantStation = new SupervisionStation();
		this.attendantSoftware = new AttendantSoftware(attendantStation, attendantData, checkoutStations);
		//Have to add in attendant touch screen software 
	}
	
	public void attachCheckoutStationUnits(ArrayList<SelfCheckoutStationUnit> unitList)
	{
		checkoutStations = unitList;
		
		this.attendantSoftware.setCheckoutStationUnits(checkoutStations);
		
		//C
		for (SelfCheckoutStation station : this.attendantStation.supervisedStations())
		{
			this.attendantStation.remove(station);
		}
		
		for (SelfCheckoutStationUnit unit : checkoutStations)
		{
			this.attendantStation.add(unit.getSelfCheckoutStationHardware());
		}
	}

	public void stationStarted(int stationID) {
		//INFORM GUI TO DISPLAY NOTIFICATION OF STATION STARTUP
		
		//For now just print to console
		System.out.println("Station " + stationID + " has been successfully started up!");
		
	}
	
	public void stationShutdown(int stationID) {
		//INFORM GUI TO DISPLAY NOTIFICATION OF STATION shutdown
		
		//For now just print to console
		System.out.println("Station " + stationID + " is being shutdown!");		
	}
	
	public void handleNoPaper(int stationID) {
		// SIGNAL GUI TO DISPLAY LOW INK ALERT
		// For now, print to console
		System.out.println("Station " + stationID + " is out of paper in its receipt printer!");
	}
	
	public void handleNoInk(int stationID) {
		// SIGNAL GUI TO DISPLAY LOW INK ALERT
		// For now, print to console
		System.out.println("Station " + stationID + " is out of ink in its receipt printer!");
	}
	
	public SupervisionStation getAttendantStation() {
		return attendantStation;
	}

	public void setAttendantStation(SupervisionStation attendantStation) {
		this.attendantStation = attendantStation;
	}

	public AttendantSoftware getAttendantSoftware() {
		return attendantSoftware;
	}

	public void setAttendantSoftware(AttendantSoftware attendantSoftware) {
		this.attendantSoftware = attendantSoftware;
	}

}
