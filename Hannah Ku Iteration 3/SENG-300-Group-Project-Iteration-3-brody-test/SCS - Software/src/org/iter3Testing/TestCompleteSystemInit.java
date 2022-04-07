package org.iter3Testing;

import java.util.ArrayList;

import org.controlSoftware.attendant.AttendantSoftware;
import org.driver.AttendantUnit;
import org.driver.SelfCheckoutStationUnit;
import org.lsmr.selfcheckout.devices.SupervisionStation;

//This Class will create a list of 10 checkout stations
//And link it to a supervision station
//THIS SHOULD BE MOVED TO ITER3TESTING
public class TestCompleteSystemInit {
	
	private ArrayList<SelfCheckoutStationUnit> checkoutStationUnits = new ArrayList<SelfCheckoutStationUnit>();
	private AttendantUnit attendantUnit;
	
	public TestCompleteSystemInit(int stationCount)
	{
		for (int i = 0; i < stationCount; i++)
		{
			SelfCheckoutStationUnit unit = new SelfCheckoutStationUnit(i);
			checkoutStationUnits.add(unit);
		}
		
		this.attendantUnit = new AttendantUnit();
		attendantUnit.attachCheckoutStationUnits(checkoutStationUnits);
		
		for (SelfCheckoutStationUnit unit : checkoutStationUnits)
		{
			unit.attachAttendant(this.attendantUnit);
		}
		
	}
	
	public ArrayList<SelfCheckoutStationUnit> getCheckoutStationUnits() {
		return checkoutStationUnits;
	}

	public AttendantUnit getAttendantUnit() {
		return attendantUnit;
	}

}
