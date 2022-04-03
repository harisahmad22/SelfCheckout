package org.driver;

import java.util.ArrayList;

import org.controlSoftware.attendant.AttendantSoftware;
import org.lsmr.selfcheckout.devices.SupervisionStation;

//This Class will create a list of 10 checkout stations
//And link it to a supervision station
public class MultipleUnitsWithAttendant {
	
	private ArrayList<SelfCheckoutStationUnit> checkoutStations = new ArrayList<SelfCheckoutStationUnit>();
	private SupervisionStation attendantStation;
	private AttendantSoftware attendantSoftware;
	
	public MultipleUnitsWithAttendant(int stationCount)
	{
		this.attendantStation = new SupervisionStation();
		
		for (int i = 0; i < stationCount; i++)
		{
			SelfCheckoutStationUnit unit = new SelfCheckoutStationUnit(i);
			checkoutStations.add(unit);
			this.attendantStation.add(unit.getSelfCheckoutStationHardware());
		}
		
		this.setAttendantSoftware(new AttendantSoftware(attendantStation, checkoutStations));
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
